package reactions;

import dao.model.Reaction;
import dao.model.User;

import java.util.*;

public class OldestReactionReporter extends AbstractReactionReporter {

    @Override
    protected List<ReactionDisplayTag> processReactions(Collection<Reaction> reactions) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    protected Comparator<ReactionDisplayTag> ordering() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

