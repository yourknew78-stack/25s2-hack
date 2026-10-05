package dao;

import dao.model.Message;

import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {
    private static MessageComparator instance;

    /**
     * Implements the Singleton design pattern. Because creating multiple instances
     * of the MessageComparator would simply waste memory, we may use the Singleton
     * design pattern to ensure only a single instance exists.
     * @return the instance
     */
    public static MessageComparator getInstance() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
    private MessageComparator() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Checks if two Messages are identical in these fields: timestamp, thread, poster, and ID.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return the result of the comparison, in line with standard Java comparison rules
     */
    @Override
    public int compare(Message o1, Message o2) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}
