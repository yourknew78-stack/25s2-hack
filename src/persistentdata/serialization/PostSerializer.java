package persistentdata.serialization;
import dao.model.Post;

import java.util.UUID;

/**
 * Converts between Posts and String[] by converting each field of Post
 * (UUID, poster, and topic) to a string, which becomes one of the entries
 * within the array
 */
public class PostSerializer implements Serializer<Post, String[]> {

    @Override
    public String[] serialize(Post object) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public Post deserialize(String[] data) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

