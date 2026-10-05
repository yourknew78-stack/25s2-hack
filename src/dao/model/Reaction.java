package dao.model;

import reactions.ReactionType;

import java.util.UUID;

public record Reaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) implements HasUUID {

    public Reaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        this(userUUID, messageUUID, type, 0L);
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public UUID getUUID() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean isByUser(UUID userUUID) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean isOnMessage(UUID messageUUID) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

