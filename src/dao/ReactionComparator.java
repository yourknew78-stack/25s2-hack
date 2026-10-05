package dao;

import dao.model.Reaction;
import reactions.ReactionType;

import java.util.Comparator;
import java.util.UUID;

public class ReactionComparator implements Comparator<Reaction> {
    private static ReactionComparator instance;

    private ReactionComparator() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static ReactionComparator getInstance() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public int compare(Reaction r1, Reaction r2) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

