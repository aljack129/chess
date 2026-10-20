package dataaccess;

public record UserData(String username, String password, String email, AuthData authData) {
    public UserData changeAuth(AuthData newAuth){
        return new UserData(username, password, email, newAuth);
    }
}
