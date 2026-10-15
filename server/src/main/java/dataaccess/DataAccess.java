package dataaccess;

public interface DataAccess {
    void clear();
    void createUser(String username, String password, String email);
    String addAuth(String username);

    String getUserData(String username);
}
