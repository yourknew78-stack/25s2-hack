package dao;

import dao.model.Reaction;

import java.util.Comparator;

/**
 * Orders reactions by message UUID, then user UUID, then reaction type.
 * Consistent with Reaction.equals: two reactions with the same
 * (user, message, type) triple compare as equal, regardless of timestamp,
 * so a user can only hold one reaction of a given type per message.
 */
public class ReactionComparator implements Comparator<Reaction> {
    private static ReactionComparator instance;

    private ReactionComparator() {}

    public static ReactionComparator getInstance() {
        if (instance == null) {
            instance = new ReactionComparator();
        }
        return instance;
    }

    @Override
    public int compare(Reaction r1, Reaction r2) {
        int messageCompare = r1.messageUUID().compareTo(r2.messageUUID());
        if (messageCompare != 0) {
            return messageCompare;
        }

        int userCompare = r1.userUUID().compareTo(r2.userUUID());
        if (userCompare != 0) {
            return userCompare;
        }

        return r1.type().compareTo(r2.type());
    }
}
