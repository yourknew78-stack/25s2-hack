package reactions;

import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Reaction;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Template for selecting indexed reactions, formatting tags and limiting output.
 * Subclasses supply algorithm-specific selection and labels; the shared workflow
 * handles missing messages and caps every report at five entries.
 */
public abstract class AbstractReactionReporter implements IReactionReporter {
    private static final Comparator<ReactionDisplayTag> PRESERVE_INDEX_ORDER = (left, right) -> 0;

    /** Maximum number of display tags allowed by either reporting algorithm. */
    protected static final int MAX_REACTIONS = 5;

    /** Shared reaction store and incremental reporting index. */
    protected final ReactionDAO reactionDAO = ReactionDAO.getInstance();
    /** Shared user store for resolving display names. */
    protected final UserDAO userDAO = UserDAO.getInstance();

    /** Initializes the shared data access used by reporting subclasses. */
    public AbstractReactionReporter() {}

    /**
     * Generates a bounded report using the subclass selection and formatting hooks.
     *
     * @param message the message to summarize; may be null
     * @return at most five tags, or an empty array for a missing message or UUID
     */
    @Override
    public final ReactionDisplayTag[] generateReport(Message message) {
        if (message == null || message.id() == null) {
            return new ReactionDisplayTag[0];
        }
        List<ReactionDisplayTag> tags = processReactions(selectReactions(message));
        Comparator<ReactionDisplayTag> comparator = ordering();
        if (comparator != PRESERVE_INDEX_ORDER) {
            tags.sort(comparator);
        }
        List<ReactionDisplayTag> report = tags.size() > MAX_REACTIONS
                ? tags.subList(0, MAX_REACTIONS) : tags;
        return report.toArray(new ReactionDisplayTag[report.size()]);
    }

    /**
     * Selects bounded candidates from the incremental index without scanning history.
     *
     * @param message a message with a non-null UUID
     * @return candidates in report order
     */
    protected abstract Collection<Reaction> selectReactions(Message message);

    /**
     * Formats selected candidates without changing their order.
     *
     * @param reactions the indexed candidates
     * @return a mutable list of display tags
     */
    protected abstract List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions);

    /**
     * Supplies an optional ordering hook. The default preserves index order
     * without sorting; subclasses may supply another comparator to request a
     * stable sort. Current reporters rely on their already-ranked candidates.
     *
     * @return a comparator that preserves the selection order
     */
    protected Comparator<ReactionDisplayTag> ordering() {
        return PRESERVE_INDEX_ORDER;
    }
}
