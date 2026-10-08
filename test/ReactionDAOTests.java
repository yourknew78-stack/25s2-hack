import dao.PostDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Reaction;
import persistentdata.DataManager;
import persistentdata.DataPipeline;
import java.lang.reflect.Field;
import java.util.Iterator;
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

    @Test
    public void moreThanFiveUsersAndEarlierReactionsMaintainIndexes() {
        User[] participants = new User[7];
        for (int i = 0; i < participants.length; i++) {
            participants[i] = new User(UUID.randomUUID(), User.Role.Member,
                    "coverageUser" + i, "password");
            assertTrue(UserDAO.getInstance().add(participants[i]));
            long timestamp = i == 6 ? 5 : 100 + i;
            assertTrue(ReactionsFacade.addReaction(participants[i].id(), message.id(),
                    ReactionType.HAPPY, timestamp));
        }
        // Replace one user's oldest representative, then remove it and refill.
        assertTrue(ReactionsFacade.addReaction(participants[0].id(), message.id(), ReactionType.SAD, 1));
        assertEquals(Arrays.asList(ReactionType.SAD, ReactionType.HAPPY),
                ReactionsFacade.getReactions(participants[0].id(), message.id()));
        assertTrue(ReactionsFacade.removeReaction(participants[0].id(), message.id(), ReactionType.SAD));
        assertTrue(ReactionsFacade.removeReaction(participants[6].id(), message.id(), ReactionType.HAPPY));
        for (int i = 0; i < 6; i++) {
            assertEquals(Collections.singletonList(ReactionType.HAPPY),
                    ReactionsFacade.getReactions(participants[i].id(), message.id()));
        }
        assertEquals(Collections.emptyList(),
                ReactionsFacade.getReactions(participants[6].id(), message.id()));
    }

    @Test
    public void reverseTypeAgesExerciseSummaryTruncation() {
        ReactionType[] types = ReactionType.values();
        for (int i = 0; i < types.length; i++) {
            assertTrue(add(types[i], 100 - i));
        }
        ReactionType[] reversed = types.clone();
        for (int i = 0; i < types.length; i++) reversed[i] = types[types.length - 1 - i];
        assertReactions(reversed);
        // Remove a non-earliest, non-oldest representative while keeping its type alive.
        assertTrue(ReactionsFacade.addReaction(otherUser.id(), message.id(), types[0], 200));
        assertTrue(ReactionsFacade.removeReaction(otherUser.id(), message.id(), types[0]));
        assertReactions(reversed);
    }

    @Test
    public void removingAbsentReactionExercisesDAOFailurePath() {
        // The facade rejects missing reactions before calling this DAO method.
        assertFalse(ReactionDAO.getInstance().removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertReactions();
    }

    @Test
    public void persistenceFailureRollsBackAddition() throws Exception {
        withFailingPersistence(() -> {
            assertFalse(add(ReactionType.HAPPY, 10));
            assertReactions();
        });
        assertTrue(add(ReactionType.HAPPY, 10));
        assertReactions(ReactionType.HAPPY);
    }

    @Test
    public void persistenceFailureRestoresRemovedReactionAndTimestamp() throws Exception {
        // Seed the DAO directly so these UUIDs have not yet been persisted.
        assertTrue(ReactionDAO.getInstance().addReaction(user.id(), message.id(), ReactionType.HAPPY, 30));
        assertTrue(ReactionDAO.getInstance().addReaction(user.id(), message.id(), ReactionType.SAD, 20));
        withFailingPersistence(() -> {
            assertFalse(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
            assertReactions(ReactionType.SAD, ReactionType.HAPPY);
            Reaction restored = ReactionDAO.getInstance().get(
                    new Reaction(user.id(), message.id(), ReactionType.HAPPY));
            assertNotNull(restored);
            assertEquals(30L, restored.timestamp());
        });
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), ReactionType.HAPPY));
        assertReactions(ReactionType.SAD);
    }

    /** White-box fault injection for Task 4 coverage; restored even if an assertion fails.
     * No files are deleted or permissions changed to simulate an I/O failure.
     */
    private void withFailingPersistence(Runnable assertions) throws Exception {
        DataManager manager = DataManager.getInstance();
        Field pipeline = DataManager.class.getDeclaredField("userPipeline");
        pipeline.setAccessible(true);
        Object original = pipeline.get(manager);
        DataPipeline<User, String[]> failing = new DataPipeline<User, String[]>(null, null, null, "unused") {
            @Override public void writeFrom(Iterator<User> users) {
                throw new IllegalStateException("Injected persistence failure for coverage");
            }
        };
        try {
            pipeline.set(manager, failing);
            assertions.run();
        } finally {
            pipeline.set(manager, original);
        }
    }
}
