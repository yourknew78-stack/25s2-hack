package persistentdata.serialization;

import dao.model.User;

import java.util.UUID;

/**
 * TODO: Document your schema here
 */
public class UserSerializer implements Serializer<User, String[]> {
    @Override
    public String[] serialize(User object) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public User deserialize(String[] data) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

