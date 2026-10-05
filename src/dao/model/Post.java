package dao.model;

import dao.MessageComparator;
import sorteddata.SortedData;
import sorteddata.SortedDataFactory;

import java.util.UUID;

public class Post implements HasUUID {
    public final UUID id;
    public final UUID poster;
    public final String topic;
    public final SortedData<Message> messages;

    public Post(UUID id, UUID poster, String topic) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public Post(UUID id) {
        this(id, null, null);
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public UUID getUUID() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

