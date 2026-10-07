package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;

public class CSVReader implements FormattedReader<String[]> {
    private final CSVFormat format;
    private final PushbackReader reader;

    public CSVReader(CSVFormat format, Reader reader) {
        this.format = format;
        this.reader = new PushbackReader(reader, 1);
    }

    private boolean eof = false;

    public boolean hasNext() {
        if (eof) return false;
        try {
            int next = reader.read();
            if (next == -1) {
                eof = true;
                return false;
            }
            reader.unread(next);
            return true;
        } catch (IOException e) {
            throw new CSVIOException(e.getMessage());
        }
    }

    // These format strings are provided to give you some ideas about what error cases might be encountered,
    // but they aren't complete. If you haven't seen these before, you can fill in the %s with .formatted:
    // for example, "hello %s".formatted("Bernardo") returns "hello Bernardo"
    private static final String LINE_TOO_SHORT_MESSAGE = "Line was too short: expected %s fields but found %s";
    private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
    private static final String IMPROPER_ESCAPE_MESSAGE = "EOF reached unexpectedly while escaped";
    private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";

    public String[] getNext() {
        if (!hasNext()) throw new CSVIOException(REACHED_EOF_MESSAGE);
        String[] row = new String[format.COLUMN_COUNT];
        StringBuilder field = new StringBuilder();
        int column = 0;
        boolean escaped = false;
        try {
            while (true) {
                int value = reader.read();
                if (value == -1) {
                    eof = true;
                    if (escaped) throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
                    row[column++] = field.toString();
                    break;
                }
                char c = (char) value;
                if (escaped) {
                    if (c == format.ESCAPE_MARKER) {
                        int next = reader.read();
                        if (next == format.ESCAPE_MARKER) {
                            field.append(c);
                        } else {
                            escaped = false;
                            if (next != -1) reader.unread(next);
                            else eof = true;
                        }
                    } else {
                        field.append(c);
                    }
                } else if (c == format.ESCAPE_MARKER && field.length() == 0) {
                    escaped = true;
                } else if (c == format.FIELD_SEPARATOR) {
                    if (column >= row.length - 1) {
                        throw new CSVIOException(LINE_TOO_LONG_MESSAGE.formatted(row.length));
                    }
                    row[column++] = field.toString();
                    field.setLength(0);
                } else if (c == format.LINE_SEPARATOR) {
                    row[column++] = field.toString();
                    break;
                } else {
                    field.append(c);
                }
            }
        } catch (IOException e) {
            throw new CSVIOException(e.getMessage());
        }
        if (column < row.length) {
            throw new CSVIOException(LINE_TOO_SHORT_MESSAGE.formatted(row.length, column));
        }
        return row;
    }

    public static class CSVIOException extends PersistentDataException {
        private static final long serialVersionUID = 1L;

        public CSVIOException(String message) {
            super(message);
        }
    }
}

