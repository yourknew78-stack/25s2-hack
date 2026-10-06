import dao.PostDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactions.ReactionType;
import reactions.ReactionsFacade;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;

/** Task 4: Task 1 functionality and statement coverage tests. */
public class ReactionDAOTests {
    private User user;
    private User otherUser;
    private Message message;
    private Message otherMessage;

    @Before
    public void setUp() {
        clearData();
        user = new User(UUID.randomUUID(), User.Role.Member, "testerA", "password");
        otherUser = new User(UUID.randomUUID(), User.Role.Member, "testerB", "password");
        assertTrue(UserDAO.getInstance().add(user));
        assertTrue(UserDAO.getInstance().add(otherUser));
        Post post = new Post(UUID.randomUUID(), user.id(), "Test thread");
        message = new Message(UUID.randomUUID(), user.id(), post.id, 1, "First message");
        otherMessage = new Message(UUID.randomUUID(), user.id(), post.id, 2, "Second message");
        assertTrue(post.messages.insert(message));
        assertTrue(post.messages.insert(otherMessage));
        assertTrue(PostDAO.getInstance().add(post));
    }

    @After
    public void tearDown() {
        clearData();
    }

    private void clearData() {
        ReactionDAO.getInstance().clear();
        PostDAO.getInstance().clear();
        UserDAO.getInstance().clear();
    }

    private boolean add(ReactionType type, long timestamp) {
        return ReactionsFacade.addReaction(user.id(), message.id(), type, timestamp);
    }

    private void assertReactions(ReactionType... expected) {
        assertEquals(Arrays.asList(expected),
                ReactionsFacade.getReactions(user.id(), message.id()));
    }

    @Test
    public void validUserAndMessageWithoutReactionsReturnEmptyList() {
        assertReactions();
    }

    @Test
    public void addingReactionReturnsTrueAndMakesItQueryable() {
        assertTrue(add(ReactionType.HAPPY, 10));
        assertReactions(ReactionType.HAPPY);
    }

    @Test
    public void duplicateReturnsFalseAndPreservesOriginalTimestamp() {
        assertTrue(add(ReactionType.HAPPY, 30));
        assertTrue(add(ReactionType.SAD, 20));
        assertFalse(add(ReactionType.HAPPY, 10));
        assertReactions(ReactionType.SAD, ReactionType.HAPPY);
    }

    @Test
    public void differentTypesAreReturnedInTimestampOrder() {
        assertTrue(add(ReactionType.HAPPY, 40));
        assertTrue(add(ReactionType.SAD, 10));
        assertTrue(add(ReactionType.LAUGH, 30));
        assertTrue(add(ReactionType.LOVE, 20));
        assertReactions(ReactionType.SAD, ReactionType.LOVE,
                ReactionType.LAUGH, ReactionType.HAPPY);
    }

    @Test
    public void everyEnumTypeCanBeAdded() {
        ReactionType[] types = ReactionType.values();
        for (int i = types.length - 1; i >= 0; i--) {
            assertTrue(add(types[i], i));
        }
        assertReactions(types);
    }

    @Test
    public void usersAndMessagesHaveIndependentReactions() {
        assertTrue(add(ReactionType.HAPPY, 10));
        assertTrue(ReactionsFacade.addReaction(otherUser.id(), message.id(), ReactionType.HAPPY, 20));
        assertTrue(ReactionsFacade.addReaction(user.id(), otherMessage.id(), ReactionType.HAPPY, 30));
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertReactions();
        assertEquals(Collections.singletonList(ReactionType.HAPPY),
                ReactionsFacade.getReactions(otherUser.id(), message.id()));
        assertEquals(Collections.singletonList(ReactionType.HAPPY),
                ReactionsFacade.getReactions(user.id(), otherMessage.id()));
    }

    @Test
    public void removingExistingReactionOnlyRemovesSelectedType() {
        assertTrue(add(ReactionType.HAPPY, 10));
        assertTrue(add(ReactionType.LAUGH, 20));
        assertTrue(add(ReactionType.SAD, 30));
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.LAUGH));
        assertReactions(ReactionType.HAPPY, ReactionType.SAD);
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.SAD));
        assertReactions();
    }

    @Test
    public void removingMissingOrAlreadyRemovedReactionReturnsFalse() {
        assertFalse(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertTrue(add(ReactionType.SAD, 10));
        assertFalse(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertReactions(ReactionType.SAD);
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.SAD));
        assertFalse(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.SAD));
        assertReactions();
    }

    @Test
    public void anotherUserCannotRemoveThisUsersReaction() {
        assertTrue(add(ReactionType.HAPPY, 10));
        assertFalse(ReactionsFacade.removeReaction(otherUser.id(), message.id(), ReactionType.HAPPY));
        assertFalse(ReactionsFacade.removeReaction(user.id(), otherMessage.id(), ReactionType.HAPPY));
        assertReactions(ReactionType.HAPPY);
    }

    @Test
    public void removedTypeCanBeAddedAgainWithNewTimestamp() {
        assertTrue(add(ReactionType.HAPPY, 10));
        assertTrue(add(ReactionType.SAD, 20));
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertTrue(add(ReactionType.HAPPY, 30));
        assertReactions(ReactionType.SAD, ReactionType.HAPPY);
    }

    @Test
    public void nullArgumentsFailGracefullyWithoutChangingExistingReactions() {
        assertTrue(add(ReactionType.HAPPY, 10));
        assertFalse(ReactionsFacade.addReaction(null, message.id(), ReactionType.SAD, 20));
        assertFalse(ReactionsFacade.addReaction(user.id(), null, ReactionType.SAD, 20));
        assertFalse(ReactionsFacade.addReaction(user.id(), message.id(), null, 20));
        assertFalse(ReactionsFacade.removeReaction(null, message.id(), ReactionType.HAPPY));
        assertFalse(ReactionsFacade.removeReaction(user.id(), null, ReactionType.HAPPY));
        assertFalse(ReactionsFacade.removeReaction(user.id(), message.id(), null));
        assertNull(ReactionsFacade.getReactions(null, message.id()));
        assertNull(ReactionsFacade.getReactions(user.id(), null));
        assertReactions(ReactionType.HAPPY);
    }

    @Test
    public void unknownUserFailsGracefullyWithoutChangingExistingReactions() {
        UUID missingUser = UUID.randomUUID();
        assertTrue(add(ReactionType.HAPPY, 10));
        assertFalse(ReactionsFacade.addReaction(missingUser, message.id(), ReactionType.SAD, 20));
        assertFalse(ReactionsFacade.removeReaction(missingUser, message.id(), ReactionType.HAPPY));
        assertNull(ReactionsFacade.getReactions(missingUser, message.id()));
        assertReactions(ReactionType.HAPPY);
    }

    @Test
    public void unknownMessageFailsGracefullyWithoutChangingExistingReactions() {
        UUID missingMessage = UUID.randomUUID();
        assertTrue(add(ReactionType.HAPPY, 10));
        assertFalse(ReactionsFacade.addReaction(user.id(), missingMessage, ReactionType.SAD, 20));
        assertFalse(ReactionsFacade.removeReaction(user.id(), missingMessage, ReactionType.HAPPY));
        assertNull(ReactionsFacade.getReactions(user.id(), missingMessage));
        assertReactions(ReactionType.HAPPY);
    }

    @Test
    public void messageAddedAfterEarlierLookupIsRecognised() {
        assertReactions(); // Populate the facade's message cache first.
        Post post = new Post(UUID.randomUUID(), user.id(), "New thread");
        Message later = new Message(UUID.randomUUID(), user.id(), post.id, 3, "Later message");
        assertTrue(post.messages.insert(later));
        assertTrue(PostDAO.getInstance().add(post));
        assertTrue(ReactionsFacade.addReaction(user.id(), later.id(), ReactionType.LOVE, 40));
        assertEquals(Collections.singletonList(ReactionType.LOVE),
                ReactionsFacade.getReactions(user.id(), later.id()));
        assertReactions();
    }
}
