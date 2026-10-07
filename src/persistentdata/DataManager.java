package persistentdata;

import dao.PostDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.Reaction;
import dao.model.User;
import persistentdata.formatted.CSVFormat;
import persistentdata.formatted.CSVFormattedFactory;
import persistentdata.io.ComputerIOFactory;
import persistentdata.io.IOFactory;
import persistentdata.serialization.MessageSerializer;
import persistentdata.serialization.PostSerializer;
import persistentdata.serialization.ReactionSerializer;
import persistentdata.serialization.UserSerializer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class DataManager {
    private static DataManager instance;
    public static synchronized DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    private final IOFactory IO;

    // We have assumed that most solutions to the serialization task in week-5 will
    // use a 4-column schema for Users. If this is not the case, you may need to
    // change the number below.
    //
    // TODO: 每种数据用什么格式、几列、存到哪个文件, 全部由你决定(见 Task 3)。
    private DataPipeline<User, String[]> userPipeline;

    private DataPipeline<Post, String[]> postPipeline;

    private DataPipeline<Message, String[]> messagePipeline;

    // TODO: Task 3 —— 回应的持久化管线
    private DataPipeline<Reaction, String[]> reactionPipeline;

    private final UserDAO users;
    private final PostDAO posts;
    private final ReactionDAO reactions;
    private final ReactionStore reactionStore;
    private final Set<UUID> persistedUsers = new HashSet<>();
    private final Set<UUID> persistedMessages = new HashSet<>();

    private DataManager() {
        IO = new ComputerIOFactory();
        users = UserDAO.getInstance();
        posts = PostDAO.getInstance();
        reactions = ReactionDAO.getInstance();
        userPipeline = new DataPipeline<>(IO, new CSVFormattedFactory(new CSVFormat(4)),
                new UserSerializer(), "users");
        postPipeline = new DataPipeline<>(IO, new CSVFormattedFactory(new CSVFormat(3)),
                new PostSerializer(), "posts");
        messagePipeline = new DataPipeline<>(IO, new CSVFormattedFactory(new CSVFormat(5)),
                new MessageSerializer(), "messages");
        reactionPipeline = new DataPipeline<>(IO, new CSVFormattedFactory(new CSVFormat(4)),
                new ReactionSerializer(), "reactions");
        reactionStore = new ReactionStore();
    }

    public synchronized void readAll() {
        users.clear();
        posts.clear();
        userPipeline.readTo(users::add);
        postPipeline.readTo(posts::add);
        messagePipeline.readTo(message -> {
            Post post = posts.get(new Post(message.thread()));
            if (post != null) post.messages.insert(message);
        });
        reactionStore.load(reactions);
        rebuildPersistedIds();
    }

    public synchronized void writeAll() {
        writeModelData();
        reactionStore.flush();
    }

    public synchronized void reactionAdded(
            UUID userUUID, UUID messageUUID, reactions.ReactionType type, long timestamp) {
        ensureModelDataStored(userUUID, messageUUID);
        reactionStore.appendAdd(userUUID, messageUUID, type, timestamp, reactions);
    }

    public synchronized void reactionRemoved(
            UUID userUUID, UUID messageUUID, reactions.ReactionType type) {
        ensureModelDataStored(userUUID, messageUUID);
        reactionStore.appendRemove(userUUID, messageUUID, type, reactions);
    }

    private void ensureModelDataStored(UUID userUUID, UUID messageUUID) {
        if (!persistedUsers.contains(userUUID) || !persistedMessages.contains(messageUUID)) {
            writeModelData();
        }
    }

    private void writeModelData() {
        userPipeline.writeFrom(users.getAll());
        postPipeline.writeFrom(posts.getAll());
        messagePipeline.writeFrom(posts.getAllMessages());
        rebuildPersistedIds();
    }

    private void rebuildPersistedIds() {
        persistedUsers.clear();
        Iterator<User> userIterator = users.getAll();
        while (userIterator.hasNext()) persistedUsers.add(userIterator.next().getUUID());

        persistedMessages.clear();
        Iterator<Message> messageIterator = posts.getAllMessages();
        while (messageIterator.hasNext()) persistedMessages.add(messageIterator.next().id());
    }
}

