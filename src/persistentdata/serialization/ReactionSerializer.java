package persistentdata.serialization;

import dao.model.Reaction;
import reactions.ReactionType;

import java.util.UUID;

public class ReactionSerializer implements Serializer<Reaction, String[]> {

    @Override
    public String[] serialize(Reaction object) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public Reaction deserialize(String[] data) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

