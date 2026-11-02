package dataaccess;


import chess.ChessGame;
import dataaccess.sql.SQLDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class DataAccessTests {

    private DataAccess initialize() throws DataAccessException {
        try {
            DataAccess database = new SQLDataAccess();
            database.clear();
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
        } catch (Exception e) {
            throw new DataAccessException("datbase fail");
        }
    }


    @Test
    public void clearTest() {
        assertDoesNotThrow(() -> {
            DataAccess database = initialize();
            assert database.userSize() != 0;
            assert database.gameSize() != 0;
            database.clear();
            assert database.userSize() == 0;
            assert database.gameSize() == 0;
        });
    }

    @Test
    public void createUserTest() {
        assertDoesNotThrow(() -> {
            DataAccess database = initialize();
            int init_size = database.userSize();
            UserData user = new UserData("Bob", "Ross", "l@m.com");
            database.createUser("Bob", "Ross", "l@m.com");
            assert database.userSize() == init_size + 1;
            assert user.equals(database.getUserData("Bob"));
        });
    }


    @Test
    public void authTest() {
        assertDoesNotThrow(() -> {
            DataAccess database = initialize();
            UserData user = new UserData("Matt", "Meese", "mm@studioc.com");
            database.createUser("Matt", "Meese", "mm@studioc.com");

            String authToken = database.addAuth("Matt");
            assertEquals(user, database.verifyUser(authToken));
            assertNotEquals(new UserData("Matt", "Meese", "kittens"), database.verifyUser(authToken));

            database.deleteAuth(authToken);
            assertEquals(null, database.verifyUser(authToken));
            assertNotEquals(user, database.verifyUser(authToken));
        });
    }

    @Test
    public void gameTest() {
        assertDoesNotThrow(() -> {
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

            GameData changedGame = new GameData(5, "changedGame", null, null, new ChessGame());
            database.createGame("changedGame");
            UserData user = new UserData("mark", "ruffalo", "the hulk");
            changedGame = changedGame.addUser(ChessGame.TeamColor.BLACK, user);
            database.updateGame(changedGame);

            assert database.findGame(5).equals(changedGame);
        });
    }
}
