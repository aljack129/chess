package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class SQLDataAccess implements DataAccess {
    @Override
    public void clear() {
    }

    @Override
    public int userSize() {
    }

    @Override
    public int gameSize() {
    }

    @Override
    public int authSize() {
    }

    @Override
    public void createUser(String username, String password, String email) {
    }

    @Override
    public UserData getUserData(String username) {
    }

    @Override
    public String addAuth(String username) {
    }

    @Override
    public void deleteAuth(String authToken) {
    }

    @Override
    public UserData verifyUser(String authToken) {
    }

    @Override
    public int createGame(String name) {
    }

    @Override
    public ArrayList getGames() {
    }

    @Override
    public GameData findGame(int gameID) {
    }

    @Override
    public void updateGame(GameData game) {
    }
}
