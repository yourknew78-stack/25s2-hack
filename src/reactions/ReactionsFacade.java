package reactions;

import dao.ReactionDAO;
import dao.UserDAO;
import dao.PostDAO;
import dao.model.Message;
import dao.model.Reaction;
import persistentdata.DataManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ReactionsFacade {
    private static ReactionDAO reactionDAO = ReactionDAO.getInstance();
    private static UserDAO userDAO = UserDAO.getInstance();
    private static PostDAO postDAO = PostDAO.getInstance();
    private static DataManager dataManager;

    // Cache of message IDs known to exist; rebuilt by a full scan on a cache miss.
    // Messages are never deleted, so entries never become stale.
    private static final Set<UUID> knownMessageIds = new HashSet<>();

    private static boolean messageExists(UUID messageUUID) {
        if (messageUUID == null) return false;
        if (knownMessageIds.contains(messageUUID)) return true;
        for (Iterator<Message> it = postDAO.getAllMessages(); it.hasNext(); ) {
            knownMessageIds.add(it.next().id());
        }
        return knownMessageIds.contains(messageUUID);
    }

    /**
     * Adds a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully added, and false otherwise.
     * Users may have an arbitrary number of reactions on a single message, but only one of a given type.
     */
    public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        if (userUUID == null || messageUUID == null || type == null) return false;
        if (userDAO.getByUUID(userUUID) == null) return false;
        if (!messageExists(messageUUID)) return false;
        if (!reactionDAO.addReaction(userUUID, messageUUID, type, timestamp)) return false;
        try {
            DataManager.getInstance().reactionAdded(userUUID, messageUUID, type, timestamp);
            return true;
        } catch (RuntimeException e) {
            reactionDAO.removeReaction(userUUID, messageUUID, type);
            return false;
        }
    }

    /**
     * Removes a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully removed, and false otherwise.
     */
    public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        if (userUUID == null || messageUUID == null || type == null) return false;
        if (userDAO.getByUUID(userUUID) == null) return false;
        if (!messageExists(messageUUID)) return false;
        Reaction removed = reactionDAO.get(new Reaction(userUUID, messageUUID, type));
        if (removed == null) return false;
        if (!reactionDAO.removeReaction(userUUID, messageUUID, type)) return false;
        try {
            DataManager.getInstance().reactionRemoved(userUUID, messageUUID, type);
            return true;
        } catch (RuntimeException e) {
            reactionDAO.addReaction(userUUID, messageUUID, type, removed.timestamp());
            return false;
        }
    }

    /**
     * Fetches all reactions made by a particular user on a particular message.
     * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
     * They must be returned in chronological (time-based) order, from oldest to newest.
     */
    public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
        if (userUUID == null || messageUUID == null) return null;
        if (userDAO.getByUUID(userUUID) == null) return null;
        if (!messageExists(messageUUID)) return null;
        return reactionDAO.getReactionsByUser(userUUID, messageUUID);
    }

    /**
     * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent data.
     */
    public static void loadPersistentData() {
        knownMessageIds.clear();
        DataManager.getInstance().readAll();
    }
}
