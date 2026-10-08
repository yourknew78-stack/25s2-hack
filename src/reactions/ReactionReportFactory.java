package reactions;

/** Creates reporters without exposing their concrete implementations to callers. */
public class ReactionReportFactory {
    /** Creates a factory instance; reporter construction is available statically. */
    public ReactionReportFactory() {}

    /**
     * Selects the requested reporting algorithm, ignoring letter case.
     *
     * @param type "oldest" or "overview" in any capitalization
     * @return a new reporter for the requested algorithm
     * @throws IllegalArgumentException if the type is null or unsupported
     */
    public static IReactionReporter buildReporter(String type) {
        if ("oldest".equalsIgnoreCase(type)) {
            return new OldestReactionReporter();
        }
        if ("overview".equalsIgnoreCase(type)) {
            return new OverviewReactionReporter();
        }
        throw new IllegalArgumentException("Unknown reaction report type: " + type);
    }
}
