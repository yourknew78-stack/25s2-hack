package persistentdata.serialization;

import dao.model.Message;

import java.util.UUID;

/**
 * Converts between Messages and String[] by converting each field of Post
 * (UUID, poster, thread, timestamp, and message) to a string, which becomes one of the entries
 * within the array
 */
public class MessageSerializer implements Serializer<Message, String[]> {

    @Override
    public String[] serialize(Message object) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public Message deserialize(String[] data) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

