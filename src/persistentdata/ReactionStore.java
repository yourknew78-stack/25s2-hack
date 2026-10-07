package persistentdata;

import dao.ReactionDAO;
import dao.model.Reaction;
import reactions.ReactionType;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Append-only binary log of reaction adds and removes.
 *
 * Each record is one byte of flags (type ordinal, whether each UUID is inlined,
 * and add versus remove), followed by either a raw 16-byte UUID or a varint
 * dictionary index, and a varint timestamp for adds. UUIDs are written in full
 * only the first time they appear, so repeated users and messages stay cheap
 * enough to average at most 40 bytes per stored reaction.
 */
class ReactionStore {
    private static final ReactionType[] TYPES = ReactionType.values();
    private static final int USER_INLINE = 0x10;
    private static final int MESSAGE_INLINE = 0x20;
    private static final int REMOVED = 0x40;
    private static final Path PATH = Path.of("saved", "reactions.bin");

    private final Map<UUID, Integer> uuidIndex = new HashMap<>();
    private final List<UUID> uuidList = new ArrayList<>();
    private final byte[] rec = new byte[48];
    private final byte[] block = new byte[1 << 16];

    private int recLen;
    private int blockLen;
    private long fileLen;
    private int live;
    private FileOutputStream out;
    private boolean opened;

    ReactionStore() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "reaction-store-flush"));
    }

    void appendAdd(UUID user, UUID message, ReactionType type, long timestamp, ReactionDAO dao) {
        openForAppend();
        encode(user, message, type, timestamp, false);
        emit();
        live++;
    }

    void appendRemove(UUID user, UUID message, ReactionType type, ReactionDAO dao) {
        openForAppend();
        encode(user, message, type, 0L, true);
        emit();
        live--;
        if (live <= 0) {
            resetEmpty();
            return;
        }
        if (fileLen + blockLen > 40L * live) {
            compact(dao);
        }
    }

    void load(ReactionDAO dao) {
        drain();
        closeOut();
        opened = false;
        uuidIndex.clear();
        uuidList.clear();
        live = 0;
        dao.clear();
        try {
            if (Files.exists(PATH)) {
                byte[] data = Files.readAllBytes(PATH);
                int valid = parse(data, dao);
                if (valid < data.length) {
                    truncate(data, valid);
                }
                fileLen = valid;
            } else {
                fileLen = 0;
            }
            reopen();
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }

    void flush() {
        drain();
    }

    void close() {
        drain();
        closeOut();
        opened = false;
    }

    private void openForAppend() {
        if (opened) return;
        try {
            Files.createDirectories(PATH.getParent());
            if (Files.exists(PATH) && Files.size(PATH) > 0 && uuidList.isEmpty()) {
                byte[] data = Files.readAllBytes(PATH);
                int valid = parse(data, null);
                if (valid < data.length) truncate(data, valid);
                fileLen = valid;
            } else if (!Files.exists(PATH)) {
                fileLen = 0;
            }
            reopen();
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }

    private void reopen() throws IOException {
        Files.createDirectories(PATH.getParent());
        out = new FileOutputStream(PATH.toFile(), true);
        opened = true;
    }

    private void emit() {
        if (blockLen + recLen > block.length) drain();
        System.arraycopy(rec, 0, block, blockLen, recLen);
        blockLen += recLen;
    }

    private void drain() {
        if (out == null || blockLen == 0) return;
        try {
            out.write(block, 0, blockLen);
            out.flush();
            fileLen += blockLen;
            blockLen = 0;
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }

    private void closeOut() {
        if (out == null) return;
        try {
            out.close();
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        } finally {
            out = null;
        }
    }

    private void resetEmpty() {
        closeOut();
        opened = false;
        blockLen = 0;
        uuidIndex.clear();
        uuidList.clear();
        live = 0;
        try {
            Files.createDirectories(PATH.getParent());
            Files.write(PATH, new byte[0]);
            fileLen = 0;
            reopen();
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }

    private void compact(ReactionDAO dao) {
        closeOut();
        opened = false;
        blockLen = 0;
        uuidIndex.clear();
        uuidList.clear();
        live = 0;
        Path tmp = PATH.resolveSibling("reactions.bin.tmp");
        try {
            Files.createDirectories(PATH.getParent());
            try (BufferedOutputStream tmpOut = new BufferedOutputStream(new FileOutputStream(tmp.toFile()), block.length)) {
                Iterator<Reaction> it = dao.getAll();
                while (it.hasNext()) {
                    Reaction reaction = it.next();
                    encode(reaction.userUUID(), reaction.messageUUID(), reaction.type(), reaction.timestamp(), false);
                    tmpOut.write(rec, 0, recLen);
                    live++;
                }
            }
            try {
                Files.move(tmp, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, PATH, StandardCopyOption.REPLACE_EXISTING);
            }
            fileLen = Files.size(PATH);
            reopen();
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }

    private void truncate(byte[] data, int valid) throws IOException {
        try (FileOutputStream truncated = new FileOutputStream(PATH.toFile())) {
            truncated.write(data, 0, valid);
        }
    }

    private int parse(byte[] data, ReactionDAO dao) {
        int pos = 0;
        while (pos < data.length) {
            int start = pos;
            int dictSize = uuidList.size();
            int liveBefore = live;
            try {
                pos = readRecord(data, pos, dao);
            } catch (Truncated ignored) {
                rollback(dictSize);
                live = liveBefore;
                return start;
            }
        }
        return pos;
    }

    private void rollback(int dictSize) {
        while (uuidList.size() > dictSize) {
            uuidIndex.remove(uuidList.remove(uuidList.size() - 1));
        }
    }

    private int readRecord(byte[] data, int pos, ReactionDAO dao) {
        if (pos >= data.length) throw new Truncated();
        int header = data[pos++] & 0xFF;
        int ordinal = header & 0x0F;
        if (ordinal >= TYPES.length) throw new PersistentDataException("Malformed reaction record");
        boolean userNew = (header & USER_INLINE) != 0;
        boolean messageNew = (header & MESSAGE_INLINE) != 0;
        boolean removed = (header & REMOVED) != 0;

        UUID user;
        if (userNew) {
            user = readUuid(data, pos);
            pos += 16;
        } else {
            pos = readVarint(data, pos);
            user = uuidAt(scratch);
        }

        UUID message;
        if (messageNew) {
            message = readUuid(data, pos);
            pos += 16;
        } else {
            pos = readVarint(data, pos);
            if (userNew && scratch == uuidList.size()) message = user;
            else message = uuidAt(scratch);
        }

        long timestamp = 0L;
        if (!removed) {
            pos = readVarint(data, pos);
            timestamp = scratch;
        }

        if (userNew) commit(user);
        if (messageNew) commit(message);
        if (dao != null) {
            if (removed) dao.removeReaction(user, message, TYPES[ordinal]);
            else dao.addReaction(user, message, TYPES[ordinal], timestamp);
        }
        live += removed ? -1 : 1;
        return pos;
    }

    private UUID uuidAt(long index) {
        if (index < 0 || index >= uuidList.size()) {
            throw new PersistentDataException("Malformed reaction record");
        }
        return uuidList.get((int) index);
    }

    private void encode(UUID user, UUID message, ReactionType type, long timestamp, boolean removed) {
        boolean userNew = !uuidIndex.containsKey(user);
        int userId = userNew ? uuidList.size() : uuidIndex.get(user);
        boolean same = user.equals(message);
        boolean messageNew = !same && !uuidIndex.containsKey(message);
        int messageId = same ? userId : (messageNew ? uuidList.size() + (userNew ? 1 : 0) : uuidIndex.get(message));

        recLen = 0;
        int header = type.ordinal();
        if (userNew) header |= USER_INLINE;
        if (messageNew) header |= MESSAGE_INLINE;
        if (removed) header |= REMOVED;
        rec[recLen++] = (byte) header;
        if (userNew) writeUuid(user);
        else writeVarint(userId);
        if (messageNew) writeUuid(message);
        else writeVarint(messageId);
        if (!removed) writeVarint(timestamp);

        if (userNew) commit(user);
        if (messageNew) commit(message);
    }

    private void commit(UUID id) {
        uuidIndex.put(id, uuidList.size());
        uuidList.add(id);
    }

    private void writeUuid(UUID id) {
        writeLong(id.getMostSignificantBits());
        writeLong(id.getLeastSignificantBits());
    }

    private void writeLong(long value) {
        for (int shift = 56; shift >= 0; shift -= 8) {
            rec[recLen++] = (byte) (value >>> shift);
        }
    }

    private void writeVarint(long value) {
        while (true) {
            int bits = (int) (value & 0x7F);
            value >>>= 7;
            if (value == 0) {
                rec[recLen++] = (byte) bits;
                return;
            }
            rec[recLen++] = (byte) (bits | 0x80);
        }
    }

    private long scratch;

    private int readVarint(byte[] data, int pos) {
        long result = 0;
        int shift = 0;
        while (true) {
            if (pos >= data.length || shift > 63) throw new Truncated();
            int b = data[pos++] & 0xFF;
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                scratch = result;
                return pos;
            }
            shift += 7;
        }
    }

    private UUID readUuid(byte[] data, int pos) {
        if (pos + 16 > data.length) throw new Truncated();
        long hi = 0;
        long lo = 0;
        for (int i = 0; i < 8; i++) hi = (hi << 8) | (data[pos + i] & 0xFFL);
        for (int i = 0; i < 8; i++) lo = (lo << 8) | (data[pos + 8 + i] & 0xFFL);
        return new UUID(hi, lo);
    }

    private static final class Truncated extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
