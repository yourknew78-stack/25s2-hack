package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Reader;

public class CSVReader implements FormattedReader<String[]> {
    private final CSVFormat format;
    private final Reader reader;

    public CSVReader(CSVFormat format, Reader reader) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private boolean eof = false;

    public boolean hasNext() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    // These format strings are provided to give you some ideas about what error cases might be encountered,
    // but they aren't complete. If you haven't seen these before, you can fill in the %s with .formatted:
    // for example, "hello %s".formatted("Bernardo") returns "hello Bernardo"
    private static final String LINE_TOO_SHORT_MESSAGE = "Line was too short: expected %s fields but found %s";
    private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
    private static final String IMPROPER_ESCAPE_MESSAGE = "EOF reached unexpectedly while escaped";
    private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";

    public String[] getNext() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static class CSVIOException extends PersistentDataException {
        public CSVIOException(String message) {
            super(message);
            throw new UnsupportedOperationException("TODO: 待实现");
        }
    }
}

