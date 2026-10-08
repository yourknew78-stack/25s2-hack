package reactions;

import dao.model.Reaction;
import dao.model.Message;
import dao.model.User;

import java.util.*;

public class OldestReactionReporter extends AbstractReactionReporter {

    @Override
    protected Collection<Reaction> selectReactions(Message message) {
        return reactionDAO.getOldestReactionsOnMessage(message.id());
    }

    @Override
    protected List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions) {
        List<ReactionDisplayTag> tags = new ArrayList<>(reactions.size());
        for (Reaction reaction : reactions) {
            User user = userDAO.getByUUID(reaction.userUUID());
            tags.add(new ReactionDisplayTag(reaction.type(), user.username()));
        }
        return tags;
    }

    @Override
    protected Comparator<ReactionDisplayTag> ordering() {
        return super.ordering();
    }
}

