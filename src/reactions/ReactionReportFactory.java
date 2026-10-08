package reactions;

/**
 * Factory for creating reaction reporters using the Factory design pattern
 */
public class ReactionReportFactory {
    public static IReactionReporter buildReporter(String type) {
        if ("oldest".equalsIgnoreCase(type)) return new OldestReactionReporter();
        if ("overview".equalsIgnoreCase(type)) return new OverviewReactionReporter();
        throw new IllegalArgumentException("Unknown reaction report type: " + type);
    }
}

