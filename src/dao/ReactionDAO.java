package dao;

import dao.model.Reaction;
import reactions.ReactionType;
import sorteddata.LazySortedData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DAO for managing reactions using the DAO design pattern.
 *
 * On top of the inherited SortedData store (kept in sync for the DAO contract),
 * this DAO maintains secondary indexes so that every operation stays fast even
 * with a million reactions on a single message:
 *  - a HashMap keyed by the (user, message, type) identity for O(1) existence
 *    checks, relying on Reaction.equals/hashCode;
 *  - per (user, message) buckets kept sorted by timestamp, so
 *    getReactionsByUser returns chronological order without re-sorting;
 *  - per message insertion-ordered lists, so getReactionsOnMessage only pays
 *    for a sort when it is actually called.
 */
public class ReactionDAO extends DAO<Reaction> {
    private static ReactionDAO instance;

    private record UserMessageKey(UUID userUUID, UUID messageUUID) {}

    private final Map<Reaction, Reaction> index = new HashMap<>();
    private final Map<UserMessageKey, ArrayList<Reaction>> byUserMessage = new HashMap<>();
    private final Map<UUID, ArrayList<Reaction>> byMessage = new HashMap<>();
    private final Map<UUID, ReactionReportIndex> reports = new HashMap<>();

    private ReactionDAO() {
        super(ReactionComparator.getInstance());
        this.data = new LazySortedData<>(comparator);
    }

    public static ReactionDAO getInstance() {
        if (instance == null) {
            instance = new ReactionDAO();
        }
        return instance;
    }

    @Override
    public boolean add(Reaction reaction) {
        if (reaction == null) return false;
        return addReaction(reaction.userUUID(), reaction.messageUUID(), reaction.type(), reaction.timestamp());
    }

    @Override
    public void clear() {
        super.clear();
        this.data = new LazySortedData<>(comparator);
        index.clear();
        byUserMessage.clear();
        byMessage.clear();
        reports.clear();
    }

    public boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        Reaction reaction = new Reaction(userUUID, messageUUID, type, timestamp);
        if (index.putIfAbsent(reaction, reaction) != null) {
            return false; // this user already left this type of reaction on this message
        }

        data.insert(reaction);

        UserMessageKey key = new UserMessageKey(userUUID, messageUUID);
        ArrayList<Reaction> bucket = byUserMessage.computeIfAbsent(key, k -> new ArrayList<>());
        bucket.add(upperBound(bucket, timestamp), reaction);

        byMessage.computeIfAbsent(messageUUID, k -> new ArrayList<>()).add(reaction);
        reports.computeIfAbsent(messageUUID, k -> new ReactionReportIndex()).add(reaction);

        return true;
    }

    public boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        Reaction removed = index.remove(new Reaction(userUUID, messageUUID, type));
        if (removed == null) {
            return false;
        }

        ((LazySortedData<Reaction>) data).remove(removed);

        ArrayList<Reaction> bucket = byUserMessage.get(new UserMessageKey(userUUID, messageUUID));
        if (bucket != null) bucket.remove(removed);

        ArrayList<Reaction> messageReactions = byMessage.get(messageUUID);
        if (messageReactions != null) messageReactions.remove(removed);
        reports.get(messageUUID).remove(removed, messageReactions);
        if (messageReactions.isEmpty()) reports.remove(messageUUID);

        return true;
    }

    public List<ReactionType> getReactionsByUser(UUID userUUID, UUID messageUUID) {
        ArrayList<Reaction> bucket = byUserMessage.get(new UserMessageKey(userUUID, messageUUID));
        List<ReactionType> reactionTypes = new ArrayList<>();
        if (bucket != null) {
            for (Reaction reaction : bucket) {
                reactionTypes.add(reaction.type());
            }
        }
        return reactionTypes;
    }

    public List<Reaction> getReactionsOnMessage(UUID messageUUID) {
        ArrayList<Reaction> messageReactions = byMessage.get(messageUUID);
        List<Reaction> reactions = new ArrayList<>();
        if (messageReactions != null) {
            // stable sort: equal timestamps keep insertion order
            reactions = new ArrayList<>(messageReactions);
            reactions.sort(Comparator.comparingLong(Reaction::timestamp));
        }
        return reactions;
    }

    public boolean hasReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        return index.containsKey(new Reaction(userUUID, messageUUID, type));
    }

    /** Returns at most five current reactions, one per user, in chronological order. */
    public List<Reaction> getOldestReactionsOnMessage(UUID messageUUID) {
        ReactionReportIndex report = reports.get(messageUUID);
        return report == null ? new ArrayList<>() : report.oldest();
    }

    /** Returns representatives of the five leading types, ordered by count then age. */
    public List<Reaction> getOverviewReactionsOnMessage(UUID messageUUID) {
        ReactionReportIndex report = reports.get(messageUUID);
        return report == null ? new ArrayList<>() : report.overview();
    }

    public int getReactionCount(UUID messageUUID, ReactionType type) {
        ReactionReportIndex report = reports.get(messageUUID);
        return report == null ? 0 : report.count(type);
    }

    // Index of the first element with a timestamp strictly greater than the given one,
    // so equal timestamps keep insertion order (stable)
    private static int upperBound(ArrayList<Reaction> bucket, long timestamp) {
        int lo = 0, hi = bucket.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (bucket.get(mid).timestamp() <= timestamp) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
