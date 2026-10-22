package dataaccess;

import java.util.UUID;

public record AuthData(String authToken, String username) {
    public AuthData createAuth() {
        return new AuthData(UUID.randomUUID().toString(), username);
    }
}
