package dao;

import dao.model.Reaction;
import reactions.ReactionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ReactionDAO extends DAO<Reaction> {
    private static ReactionDAO instance;

    private ReactionDAO() {
        super(ReactionComparator.getInstance());
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static ReactionDAO getInstance() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public List<ReactionType> getReactionsByUser(UUID userUUID, UUID messageUUID) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public List<Reaction> getReactionsOnMessage(UUID messageUUID) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean hasReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

