package reactions;

import dao.PostDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.User;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Detects spam using the reference algorithm's normalized reaction score.
 * Each reaction contributes {@code 1 / min(frequency, 3)}; the total is divided
 * by the number of distinct threads on which the user has reacted.
 * A normalized score of at least five indicates spam.
 */
public class SpamDetector {
    private static final float SPAM_THRESHOLD = 5.0f;
    private static final int MAX_FREQUENCY_FOR_CALCULATION = 3;

    private final PostDAO postDAO;
    private final UserDAO userDAO;
    private final ReactionDAO reactionDAO;

    /** Creates a detector using the application's shared DAO instances. */
    public SpamDetector() {
        this(PostDAO.getInstance(), UserDAO.getInstance(), ReactionDAO.getInstance());
    }

    /**
     * Creates a detector with explicit data-access dependencies.
     * The DAOs must describe the same consistent application state during a check.
     *
     * @param postDAO provides the messages to examine
     * @param userDAO identifies registered users
     * @param reactionDAO provides chronological user reactions and complete frequencies
     * @throws NullPointerException if any DAO is null
     */
    public SpamDetector(PostDAO postDAO, UserDAO userDAO, ReactionDAO reactionDAO) {
        this.postDAO = Objects.requireNonNull(postDAO, "postDAO");
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
        this.reactionDAO = Objects.requireNonNull(reactionDAO, "reactionDAO");
    }

    /**
     * Checks a registered user's reactions across all messages.
     * Only threads with reactions by that user contribute to the divisor.
     *
     * @param user the user to check by UUID; may be null
     * @return true if the normalized score reaches the threshold; false for a
     *         missing or unregistered user, or a user with no reactions
     * @throws RuntimeException if accessing the message or user store fails
     */
    public boolean checkSpamForUser(User user) {
        if (user == null || user.id() == null || userDAO.getByUUID(user.id()) == null) {
            return false;
        }

        float totalScore = 0.0f;
        Set<UUID> reactedThreads = new HashSet<>();
        Iterator<Message> messages = postDAO.getAllMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            List<ReactionType> userReactions =
                    reactionDAO.getReactionsByUser(user.id(), message.id());
            if (!userReactions.isEmpty()) {
                totalScore += calculateMessageScore(message.id(), userReactions);
                reactedThreads.add(message.thread());
            }
        }

        return !reactedThreads.isEmpty()
                && totalScore / reactedThreads.size() >= SPAM_THRESHOLD;
    }

    /**
     * Uses complete frequencies rather than the display report's top five types.
     * Chronological addition preserves the reference's floating-point behaviour.
     * As in the reference, unavailable frequency data makes this message's
     * contribution zero.
     *
     * @param messageId the message whose reaction frequencies are used
     * @param userReactions the user's reaction types, ordered from oldest to newest
     * @return the score contribution before normalization by thread count
     */
    private float calculateMessageScore(UUID messageId, List<ReactionType> userReactions) {
        float score = 0.0f;
        for (ReactionType type : userReactions) {
            int frequency;
            try {
                frequency = reactionDAO.getReactionCount(messageId, type);
            } catch (RuntimeException unavailableFrequency) {
                // Preserve the reference's treatment of failed frequency retrieval.
                return 0.0f;
            }
            int cappedFrequency = Math.min(frequency, MAX_FREQUENCY_FOR_CALCULATION);
            if (cappedFrequency > 0) {
                score += 1.0f / cappedFrequency;
            }
        }
        return score;
    }

    /**
     * Returns the inclusive threshold for the normalized score.
     *
     * @return the spam threshold, 5.0
     */
    public float getSpamThreshold() {
        return SPAM_THRESHOLD;
    }

    /**
     * Returns the frequency cap applied before taking the reciprocal.
     *
     * @return the maximum frequency used in scoring, 3
     */
    public int getMaxFrequencyForCalculation() {
        return MAX_FREQUENCY_FOR_CALCULATION;
    }
}
