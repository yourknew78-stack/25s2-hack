package reactions;

import dao.model.Reaction;
import dao.model.Message;

import java.util.*;

public class OverviewReactionReporter extends AbstractReactionReporter {

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

    @Override
    protected Comparator<ReactionDisplayTag> ordering() {
        // Stable sorting retains the earliest-current order for equal counts.
        return Comparator.comparingInt((ReactionDisplayTag tag) -> Integer.parseInt(tag.label()))
                .reversed();
    }
}

