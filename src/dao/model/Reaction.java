package dao.model;

import reactions.ReactionType;

import java.util.UUID;

public record Reaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) implements HasUUID {

    public Reaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        this(userUUID, messageUUID, type, 0L);
    }

    @Override
    public UUID getUUID() {
        // Deterministic composite ID: a reaction is identified by user + message + type
        String combined = userUUID.toString() + messageUUID.toString() + type.name();
        return UUID.nameUUIDFromBytes(combined.getBytes());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reaction reaction = (Reaction) obj;
        return userUUID.equals(reaction.userUUID) &&
                messageUUID.equals(reaction.messageUUID) &&
                type == reaction.type;
    }

    @Override
    public int hashCode() {
        return userUUID.hashCode() + messageUUID.hashCode() + type.hashCode();
    }

    public boolean isByUser(UUID userUUID) {
        return this.userUUID.equals(userUUID);
    }

    public boolean isOnMessage(UUID messageUUID) {
        return this.messageUUID.equals(messageUUID);
    }
}
