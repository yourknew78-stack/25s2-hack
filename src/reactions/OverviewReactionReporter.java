package reactions;

import dao.model.Reaction;

import java.util.*;

public class OverviewReactionReporter extends AbstractReactionReporter {

    @Override
    protected List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    protected Comparator<ReactionDisplayTag> ordering() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

