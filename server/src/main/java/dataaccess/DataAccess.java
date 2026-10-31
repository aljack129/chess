package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    void clear();

    int userSize();

    int gameSize();

    int authSize();

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUserData(String username) throws DataAccessException;

    String addAuth(String username) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    UserData verifyUser(String authToken) throws DataAccessException;

    int createGame(String name) throws DataAccessException;

    ArrayList getGames() throws DataAccessException;

    GameData findGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;
}
