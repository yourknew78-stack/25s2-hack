package reactions;

import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Reaction;
import dao.model.User;

import java.util.*;

public abstract class AbstractReactionReporter implements IReactionReporter {

    protected static final int MAX_REACTIONS = 5;

    protected ReactionDAO reactionDAO;
    protected UserDAO userDAO;

    @Override
    public final ReactionDisplayTag[] generateReport(Message message) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    protected abstract List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions);

    protected Comparator<ReactionDisplayTag> ordering() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

