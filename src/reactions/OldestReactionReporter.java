package reactions;

import dao.model.Reaction;
import dao.model.Message;
import dao.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Displays up to five oldest reactions from distinct users, labelled by username. */
public class OldestReactionReporter extends AbstractReactionReporter {

    /** Creates a reporter using the shared reaction and user stores. */
    public OldestReactionReporter() {}

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

}
