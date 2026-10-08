package persistentdata.serialization;

import dao.model.Reaction;
import reactions.ReactionType;

import java.util.UUID;

public class ReactionSerializer implements Serializer<Reaction, String[]> {

    @Override
    public String[] serialize(Reaction object) {
        return new String[] {
                object.userUUID().toString(),
                object.messageUUID().toString(),
                object.type().name(),
                Long.toString(object.timestamp())
        };
    }

    @Override
    public Reaction deserialize(String[] data) {
        return new Reaction(
                UUID.fromString(data[0]),
                UUID.fromString(data[1]),
                ReactionType.valueOf(data[2]),
                Long.parseLong(data[3])
        );
    }
}

