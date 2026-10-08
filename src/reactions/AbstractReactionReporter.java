package reactions;

import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Reaction;

import java.util.*;

public abstract class AbstractReactionReporter implements IReactionReporter {

    protected static final int MAX_REACTIONS = 5;

    protected ReactionDAO reactionDAO = ReactionDAO.getInstance();
    protected UserDAO userDAO = UserDAO.getInstance();

    @Override
    public final ReactionDisplayTag[] generateReport(Message message) {
        if (message == null || message.id() == null) return new ReactionDisplayTag[0];
        List<ReactionDisplayTag> tags = processReactions(selectReactions(message));
        tags.sort(ordering());
        return tags.subList(0, Math.min(MAX_REACTIONS, tags.size()))
                .toArray(new ReactionDisplayTag[0]);
    }

    // Each selection comes from an incremental index, never the full message history.
    protected abstract Collection<Reaction> selectReactions(Message message);

    protected abstract List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions);

    protected Comparator<ReactionDisplayTag> ordering() {
        // Preserve the order supplied by the reporting index.
        return (left, right) -> 0;
    }
}

