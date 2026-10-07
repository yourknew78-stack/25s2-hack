package persistentdata.serialization;

import dao.model.User;

import java.util.UUID;

/**
 * TODO: Document your schema here
 */
public class UserSerializer implements Serializer<User, String[]> {
    @Override
    public String[] serialize(User object) {
        return new String[] {
                object.id().toString(),
                object.role().name(),
                object.username(),
                object.password()
        };
    }

    @Override
    public User deserialize(String[] data) {
        return new User(UUID.fromString(data[0]), User.Role.valueOf(data[1]), data[2], data[3]);
    }
}

