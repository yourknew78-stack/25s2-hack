package reactions;

import dao.model.Reaction;
import dao.model.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Displays up to five leading reaction types, labelled by their complete counts. */
public class OverviewReactionReporter extends AbstractReactionReporter {

    /** Creates a reporter using the shared incremental reaction index. */
    public OverviewReactionReporter() {}

    @Override
    protected Collection<Reaction> selectReactions(Message message) {
        return reactionDAO.getOverviewReactionsOnMessage(message.id());
    }

    @Override
    protected List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions) {
        List<ReactionDisplayTag> tags = new ArrayList<>(reactions.size());
        for (Reaction reaction : reactions) {
            int count = reactionDAO.getReactionCount(reaction.messageUUID(), reaction.type());
            tags.add(new ReactionDisplayTag(reaction.type(), Integer.toString(count)));
        }
        return tags;
    }

}
