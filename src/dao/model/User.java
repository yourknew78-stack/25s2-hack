package dao.model;

import java.util.UUID;

public record User(UUID id, Role role, String username, String password) implements HasUUID {
    public enum Role {Member, Admin}

    public UUID getUUID() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public User(UUID id) {
        this(id, Role.Member, null, null);
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public User(String username) {
        this(null, Role.Member, username, null);
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}
