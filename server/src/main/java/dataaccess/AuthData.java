package dataaccess;

import java.util.UUID;

public record AuthData(String authToken) {
    public AuthData createAuth(){
        return new AuthData(UUID.randomUUID().toString());
    }
}
