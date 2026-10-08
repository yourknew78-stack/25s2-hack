package reactions;

/**
 * One entry in a reaction summary, as returned by IReactionReporter.
 *
 * The label is a username for Oldest and a decimal reaction count for Overview.
 * Both components are immutable; tags can safely be shared between callers.
 *
 * @param type the summarized reaction type
 * @param label the algorithm-specific display text
 */
public record ReactionDisplayTag(
        ReactionType type,
        String label
) {}
