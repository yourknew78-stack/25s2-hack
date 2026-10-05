package reactions;

import dao.ReactionDAO;
import dao.UserDAO;
import dao.PostDAO;
import dao.model.Message;
import persistentdata.DataManager;

import java.util.List;
import java.util.UUID;

public class ReactionsFacade {
    private static ReactionDAO reactionDAO;
    private static UserDAO userDAO;
    private static PostDAO postDAO;
    private static DataManager dataManager;

    /**
     * Adds a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully added, and false otherwise.
     * Users may have an arbitrary number of reactions on a single message, but only one of a given type.
     */
    public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Removes a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully removed, and false otherwise.
     */
    public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Fetches all reactions made by a particular user on a particular message.
     * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
     * They must be returned in chronological (time-based) order, from oldest to newest.
     */
    public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent data.
     */
    public static void loadPersistentData() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

