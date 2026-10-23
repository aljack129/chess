package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {

    private Map<String, UserData> users = new HashMap<>();
    private Map<String, AuthData> authorized = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();

    public MemoryDataAccess() {

    }

    @Override
    public int userSize() {
        return users.size();
    }

    @Override
    public int gameSize() {
        return games.size();
    }

    @Override
    public int authSize() {
        return authorized.size();
    }

    @Override
    public void clear() {
        users = new HashMap<>();
        authorized = new HashMap<>();
        games = new HashMap<>();
    }

    @Override
    public String addAuth(String username) {
        AuthData newAuth = (new AuthData(null, username)).createAuth();
        authorized.put(newAuth.authToken(), newAuth);
        return newAuth.authToken();
    }

    @Override
    public void deleteAuth(String authToken) {
        authorized.remove(authToken);
    }

    @Override
    public void createUser(String username, String password, String email) {
        users.put(username, new UserData(username, password, email));
    }

    @Override
    public UserData getUserData(String username) {
        return users.get(username);
    }

    @Override
    public UserData verifyUser(String authToken) {
        if (authorized.containsKey(authToken)) {
            return users.get(authorized.get(authToken).username());
        }
        return null;
    }

    @Override
    public int createGame(String name) {
        int gameID = games.size() + 1;
        games.put(gameID, new GameData(gameID, name, null, null, new ChessGame()));
        return gameID;
    }

    @Override
    public ArrayList getGames() {
        ArrayList<GameData> gameList = new ArrayList();
        for (int key : games.keySet()) {
            gameList.add(games.get(key));
        }
        return gameList;
    }

    @Override
    public GameData findGame(int gameID) {
        for (int key : games.keySet()) {
            if (key == gameID) {
                return games.get(key);
            }
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) {
        int gameID = game.gameID();
        games.put(gameID, game);
    }


}
