package dao.model;

public class TimestampFormatterTimeSinceEnglish implements TimestampFormatter {
    /**
     * Generates a textual label describing how long ago timestamp was,
     * compared with the current system time
     * @param timestamp the UNIX time in milliseconds to compare to
     * @return a String-based timestamp descriptor
     */
    @Override
    public String format(long timestamp) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

