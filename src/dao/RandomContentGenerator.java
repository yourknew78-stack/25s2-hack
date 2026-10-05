package dao;

import dao.model.Message;
import dao.model.Post;
import dao.model.User;

import java.util.Random;
import java.util.UUID;

public class RandomContentGenerator {
    private static String[] NAMES;
    private static final String[] TOPICS = new String[] {"UML", "design patterns", "data structures", "persistent data",
            "modelling", "software construction", "exam", "mini project", "group project", "singleton", "observer",
            "factory", "strategy", "state", "facade", "DAO", "IntelliJ", "Android Studio", "AVL tree", "tree balancing",
            "concurrency"};
    private static final String[] POST_NAMES = new String[] {"Question about %s", "I love %s", "Study session: %s",
            "Practicing %s", "I don't understand %s", "Applications of %s", "How to implement %s?"};

    /**
     * Fills the DAOs with a reasonable amount of data, for testing purposes
     */
    public static void populateRandomData() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private static Random random;

    public static void generateUser() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void generatePost() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void generateComment() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void generateComment(Post post) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

