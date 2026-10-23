package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.*;
import chess.*;
import model.*;

public class ServiceTests {

    private DataAccess database = new MemoryDataAccess();
    private Services services = new Services(database);

    private void initialize(DataAccess data) {
        data.createUser("user1", "carrot", "user1@email.com");
        data.createUser("user2", "carrot", "user2@email.com");
        data.createUser("user3", "carrot", "user3@email.com");
        data.createUser("user4", "carrot", "user4@email.com");
        data.createUser("user5", "carrot", "user5@email.com");

        data.addAuth("user1");
        data.addAuth("user2");
        data.addAuth("user3");

        data.createGame("game1");
        data.createGame("game2");
        data.createGame("game3");
        data.createGame("game4");
        data.createGame("game5");
    }

    @Test
    public void clearTest() {
        initialize(database);
        assert database.userSize() != 0;
        assert database.gameSize() != 0;
        assert database.authSize() != 0;
        services.clear();
        assert database.userSize() == 0;
        assert database.authSize() == 0;
        assert database.gameSize() == 0;

    }

    @Test
    public void registerUserTest() {
        DataAccess database2 = new MemoryDataAccess();

        assert database.userSize() == 0;
        assert database.authSize() == 0;
        assert database2.userSize() == 0;
        assert database2.authSize() == 0;

        services.registerUser(new RegisterUserInput("1", "e", "email"));
        database2.createUser("1", "e", "email");
        database2.addAuth("1");

        assert database.userSize() == database2.userSize();
        assert database.authSize() == database2.authSize();

        assertThrows(AlreadyTakenException.class, () -> {
            services.registerUser(new RegisterUserInput("1", "k", "mail"));
        });
    }

    @Test
    public void loginUserTest() {
        DataAccess database2 = new MemoryDataAccess();
        initialize(database);
        initialize(database2);

        database.createUser("lion", "pride", "safarimail");
        database2.createUser("lion", "pride", "safarimail");
        services.loginUser(new LoginInput("lion", "pride"));
        database2.addAuth("lion");

        assertEquals(database2.authSize(), database.authSize());

        assertThrows(NotFoundException.class, () -> services.loginUser(new LoginInput("kevin", "alone")));

        database.createUser("larry", "king", "magicemail");
        assertThrows(UnauthorizedException.class, () -> services.loginUser(new LoginInput("larry", "smith")));

    }

    @Test
    public void logoutUserTest() {
        DataAccess database2 = new MemoryDataAccess();
        initialize(database);
        initialize(database2);

        database.createUser("muffin", "man", "dreary.com");
        database2.createUser("muffin", "man", "dreary.com");
        String auth1 = database.addAuth("muffin");
        String auth2 = database2.addAuth("muffin");

        services.logoutUser(auth1);
        database2.deleteAuth(auth2);

        assertEquals(database2.authSize(), database.authSize());

        assertThrows(UnauthorizedException.class, () -> services.logoutUser("JuliusCeaser"));
    }

    @Test
    public void listGamesTest() {
        DataAccess database2 = new MemoryDataAccess();
        initialize(database);
        initialize(database2);

        database.createUser("mark", "rober", "crunchlabs");
        String authToken = database.addAuth("mark");
        assertEquals(database2.getGames(), services.listGames(authToken));

        assertThrows(UnauthorizedException.class, () -> services.listGames("Cleopatra"));
    }

    @Test
    public void createGameTest() {
        DataAccess database2 = new MemoryDataAccess();
        initialize(database);
        initialize(database2);

        database.createUser("Alex", "Lind", "Dawnsglade");
        String authToken = database.addAuth("Alex");
        services.createGame(authToken, new CreateGameInput("Antora"));
        database2.createGame("Antora");

        assertEquals(database2.gameSize(), database.gameSize());
        assertEquals(database2.getGames(), database.getGames());

        assertThrows(UnauthorizedException.class, () -> services.createGame("Sphinx", new CreateGameInput("Egypt")));
    }

    @Test
    public void joinGameTest() {
        DataAccess database2 = new MemoryDataAccess();
        initialize(database);
        initialize(database2);

        int gameID1 = database.createGame("magic");
        int gameID2 = database2.createGame("magic");

        database.createUser("admin", "adminpassword", "admin.net");
        String authToken = database.addAuth("admin");
        services.joinGame(authToken, new JoinGameInput(gameID1, ChessGame.TeamColor.WHITE));

        database2.createUser("admin", "adminpassword", "admin.net");
        UserData user = database2.getUserData("admin");
        GameData game = database2.findGame(gameID2);
        game = game.addUser(ChessGame.TeamColor.WHITE, user);
        database2.updateGame(game);

        assertEquals(database2.findGame(gameID2), database.findGame(gameID1));

        assertThrows(UnauthorizedException.class, () -> services.joinGame("Batman", new JoinGameInput(1, ChessGame.TeamColor.BLACK)));

        assertThrows(NotFoundException.class, () -> services.joinGame(authToken, new JoinGameInput(2026, ChessGame.TeamColor.WHITE)));

        database.createUser("cool", "so cool", "coolest");
        String auth2 = database.addAuth("cool");
        assertThrows(AlreadyTakenException.class, () -> services.joinGame(auth2, new JoinGameInput(gameID1, ChessGame.TeamColor.WHITE)));

    }
}
