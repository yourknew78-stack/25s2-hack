package reactions;

/**
 * One entry in a reaction summary, as returned by IReactionReporter.
 *
 * <p>The meaning of {@code label} depends on the reporting algorithm in use —
 * see algorithms.md for the two algorithms and their example outputs.
 */
public record ReactionDisplayTag(
        ReactionType type,        // The reaction type
        String label              // A display label for the front-end (e.g., emoji or text)
) {}
