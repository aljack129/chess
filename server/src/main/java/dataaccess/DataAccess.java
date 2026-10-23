package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    void clear();

    int userSize();

    int gameSize();

    int authSize();

    void createUser(String username, String password, String email);

    UserData getUserData(String username);

    String addAuth(String username);

    void deleteAuth(String authToken);

    UserData verifyUser(String authToken);

    int createGame(String name);

    ArrayList getGames();

    GameData findGame(int gameID);

    void updateGame(GameData game);
}
