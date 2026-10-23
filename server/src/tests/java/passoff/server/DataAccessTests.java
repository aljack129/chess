package passoff.server;


import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class DataAccessTests {

    private DataAccess initialize() {
        DataAccess database = new MemoryDataAccess();
        database.createUser("user1", "carrot", "user1@email.com");
        database.createUser("user2", "carrot", "user2@email.com");
        database.createUser("user3", "carrot", "user3@email.com");
        database.createUser("user4", "carrot", "user4@email.com");
        database.createUser("user5", "carrot", "user5@email.com");

        database.createGame("game1");
        database.createGame("game2");
        database.createGame("game3");
        database.createGame("game4");
        database.createGame("game5");

        return database;
    }


    @Test
    public void clearTest() {
        DataAccess database = initialize();
        assert database.userSize() != 0;
        assert database.gameSize() != 0;
        database.clear();
        assert database.userSize() == 0;
        assert database.gameSize() == 0;
    }

    @Test
    public void createUserTest() {
        DataAccess database = initialize();
        int init_size = database.userSize();
        UserData user = new UserData("Bob", "Ross", "l@m.com");
        database.createUser("Bob", "Ross", "l@m.com");
        assert database.userSize() == init_size + 1;
        assert user.equals(database.getUserData("Bob"));
    }

    /*
    @Test
    public void authTest() {
        DataAccess database = initialize();
        UserData user = new UserData("Matt", "Meese", "mm@studioc.com");
        database.createUser("Matt", "Meese", "mm@studioc.com");

        String auth = database.addAuth("Matt");
        user.changeAuth(new AuthData(auth, "Matt"));

        assert user.equals(database.getUserData("Matt"));
        assert user.equals(database.verifyUser(auth));

        database.deleteAuth("Matt");
        user.changeAuth(null);

        assert user.equals(database.getUserData("Matt"));
        assertNull(database.verifyUser(auth));
    }
    */
    @Test
    public void gameTest() {
        DataAccess database = initialize();
        GameData game = new GameData(database.gameSize() + 1, "newGame", null, null, new ChessGame());
        int gameID = database.createGame("newGame");

        assert game.equals(database.findGame(gameID));

        database.clear();

        ArrayList games = new ArrayList();
        games.add(new GameData(1, "game1", null, null, new ChessGame()));
        games.add(new GameData(2, "game2", null, null, new ChessGame()));
        games.add(new GameData(3, "game3", null, null, new ChessGame()));
        games.add(new GameData(4, "game4", null, null, new ChessGame()));

        database.createGame("game1");
        database.createGame("game2");
        database.createGame("game3");
        database.createGame("game4");

        assert games.equals(database.getGames());
    }
}
