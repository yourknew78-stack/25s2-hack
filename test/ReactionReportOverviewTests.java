import dao.PostDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import reactions.IReactionReporter;
import reactions.ReactionDisplayTag;
import reactions.ReactionType;
import reactions.ReactionsFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/** Overview black-box tests: expected reports come from algorithms.md. */
@RunWith(Parameterized.class)
public class ReactionReportOverviewTests {
    @Parameterized.Parameters(name = "reporter {index}")
    public static Collection<Object[]> reporters() {
        List<Object[]> parameters = new ArrayList<>();
        for (IReactionReporter reporter : new ReportSources().getReporters()) {
            parameters.add(new Object[]{reporter});
        }
        return parameters;
    }

    private final IReactionReporter reporter;
    private Message message;
    private Message otherMessage;
    private int userNumber;

    public ReactionReportOverviewTests(IReactionReporter reporter) {
        this.reporter = reporter;
    }

    @Before
    public void setUp() {
        clearData();
        userNumber = 0;
        User author = newUser();
        Post post = new Post(UUID.randomUUID(), author.id(), "Overview test");
        message = new Message(UUID.randomUUID(), author.id(), post.id, 1, "Target");
        otherMessage = new Message(UUID.randomUUID(), author.id(), post.id, 2, "Other");
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

    private User newUser() {
        User user = new User(UUID.randomUUID(), User.Role.Member,
                "overviewUser" + userNumber++, "password");
        assertTrue(UserDAO.getInstance().add(user));
        return user;
    }

    private User add(ReactionType type, long timestamp) {
        User user = newUser();
        add(user, message, type, timestamp);
        return user;
    }

    private void add(User user, Message target, ReactionType type, long timestamp) {
        assertTrue("Test setup must successfully add the reaction",
                ReactionsFacade.addReaction(user.id(), target.id(), type, timestamp));
    }

    private void remove(User user, ReactionType type) {
        assertTrue(ReactionsFacade.removeReaction(user.id(), message.id(), type));
    }

    private ReactionDisplayTag tag(ReactionType type, int count) {
        return new ReactionDisplayTag(type, Integer.toString(count));
    }

    private void assertReport(ReactionDisplayTag... expected) {
        ReactionDisplayTag[] actual = reporter.generateReport(message);
        assertNotNull("A valid message must return a report array", actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void emptyMessageReturnsEmptyArray() {
        assertReport();
    }

    @Test
    public void singleReactionDisplaysTypeAndNumericCount() {
        add(ReactionType.LOVE, 10);
        assertReport(tag(ReactionType.LOVE, 1));
    }

    @Test
    public void countsAllUsersAndOrdersByFrequencyBeforeAge() {
        add(ReactionType.SAD, 10);
        add(ReactionType.HAPPY, 20);
        add(ReactionType.LAUGH, 30);
        add(ReactionType.LAUGH, 40);
        add(ReactionType.HAPPY, 50);
        add(ReactionType.HAPPY, 60);
        assertReport(tag(ReactionType.HAPPY, 3), tag(ReactionType.LAUGH, 2), tag(ReactionType.SAD, 1));
    }

    @Test
    public void specificationExampleProducesExpectedReport() {
        ReactionType[] sequence = {ReactionType.HAPPY, ReactionType.ANGRY,
                ReactionType.HAPPY, ReactionType.HAPPY, ReactionType.LAUGH,
                ReactionType.LAUGH, ReactionType.HAPPY, ReactionType.ANGRY};
        for (int i = 0; i < sequence.length; i++) add(sequence[i], i + 10);
        assertReport(tag(ReactionType.HAPPY, 4), tag(ReactionType.ANGRY, 2), tag(ReactionType.LAUGH, 2));
    }

    @Test
    public void tiedCountsUseEarliestTimestampNotInsertionOrEnumOrder() {
        add(ReactionType.HAPPY, 60);
        add(ReactionType.SAD, 50);
        add(ReactionType.LAUGH, 40);
        add(ReactionType.HAPPY, 30);
        add(ReactionType.LAUGH, 20);
        add(ReactionType.SAD, 10);
        assertReport(tag(ReactionType.SAD, 2), tag(ReactionType.LAUGH, 2), tag(ReactionType.HAPPY, 2));
    }

    @Test
    public void multipleTypesFromSameUserAreAllCounted() {
        User user = newUser();
        add(user, message, ReactionType.SAD, 10);
        add(user, message, ReactionType.HAPPY, 20);
        add(user, message, ReactionType.LAUGH, 30);
        add(ReactionType.LAUGH, 40);
        assertReport(tag(ReactionType.LAUGH, 2), tag(ReactionType.SAD, 1), tag(ReactionType.HAPPY, 1));
    }

    @Test
    public void moreThanFiveTypesSelectsMostFrequentBeforeTruncating() {
        ReactionType[] types = ReactionType.values();
        long timestamp = 10;
        for (int i = 0; i < types.length; i++) {
            for (int count = 0; count <= i; count++) add(types[i], timestamp++);
        }
        ReactionDisplayTag[] expected = new ReactionDisplayTag[5];
        for (int i = 0; i < expected.length; i++) {
            int index = types.length - 1 - i;
            expected[i] = tag(types[index], index + 1);
        }
        assertReport(expected);
    }

    @Test
    public void tiedCountsAtFiveTypeLimitChooseOldestTypes() {
        ReactionType[] types = ReactionType.values();
        for (int i = 0; i < types.length; i++) add(types[i], 100 - i);
        ReactionDisplayTag[] expected = new ReactionDisplayTag[5];
        for (int i = 0; i < expected.length; i++) expected[i] = tag(types[types.length - 1 - i], 1);
        assertReport(expected);
    }

    @Test
    public void reactionsOnOtherMessageAreExcluded() {
        add(ReactionType.HAPPY, 10);
        User user = newUser();
        add(user, otherMessage, ReactionType.SAD, 20);
        add(user, otherMessage, ReactionType.HAPPY, 30);
        assertReport(tag(ReactionType.HAPPY, 1));
    }

    @Test
    public void removalUpdatesCountsAndOrderAfterEarlierReport() {
        User first = add(ReactionType.HAPPY, 10);
        add(ReactionType.SAD, 20);
        User second = add(ReactionType.HAPPY, 30);
        add(ReactionType.SAD, 40);
        add(ReactionType.HAPPY, 50);
        assertReport(tag(ReactionType.HAPPY, 3), tag(ReactionType.SAD, 2));
        remove(first, ReactionType.HAPPY);
        assertReport(tag(ReactionType.SAD, 2), tag(ReactionType.HAPPY, 2));
        remove(second, ReactionType.HAPPY);
        assertReport(tag(ReactionType.SAD, 2), tag(ReactionType.HAPPY, 1));
    }

    @Test
    public void deletingAllOfTypeOmitsItAndDeletingEverythingReturnsEmpty() {
        User happy = add(ReactionType.HAPPY, 10);
        User sad = add(ReactionType.SAD, 20);
        assertReport(tag(ReactionType.HAPPY, 1), tag(ReactionType.SAD, 1));
        remove(happy, ReactionType.HAPPY);
        assertReport(tag(ReactionType.SAD, 1));
        remove(sad, ReactionType.SAD);
        assertReport();
    }

    @Test
    public void reAddedTypeUsesNewTimestampForTieBreaking() {
        User happy = add(ReactionType.HAPPY, 10);
        add(ReactionType.SAD, 20);
        remove(happy, ReactionType.HAPPY);
        add(happy, message, ReactionType.HAPPY, 30);
        assertReport(tag(ReactionType.SAD, 1), tag(ReactionType.HAPPY, 1));
    }

    @Test
    public void additionAfterEarlierReportUpdatesSummary() {
        add(ReactionType.HAPPY, 10);
        assertReport(tag(ReactionType.HAPPY, 1));
        add(ReactionType.SAD, 20);
        add(ReactionType.SAD, 30);
        assertReport(tag(ReactionType.SAD, 2), tag(ReactionType.HAPPY, 1));
    }

    @Test
    public void countsWithMultipleDigitsAreSortedNumerically() {
        for (int i = 0; i < 2; i++) add(ReactionType.SAD, 10 + i);
        for (int i = 0; i < 12; i++) add(ReactionType.HAPPY, 20 + i);
        assertReport(tag(ReactionType.HAPPY, 12), tag(ReactionType.SAD, 2));
    }
}
