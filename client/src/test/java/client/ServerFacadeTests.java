package client;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import server.Server;
import model.*;
import chess.*;

import java.util.ArrayList;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerLogInOutTest() {
        assertDoesNotThrow(() -> {
            serverFacade.clear();
            RegisterUserResult registerResult = serverFacade.register(new RegisterUserInput("maggie", "magic", "myemail"));
            serverFacade.logOut(registerResult.authToken());

            LoginResult result = serverFacade.logIn(new LoginInput("maggie", "magic"));
            assertNotNull(result);

            assertThrows(ResponseException.class, () -> {
                serverFacade.logIn(new LoginInput("maggie", "whimsy"));
            });
        });
    }

    @Test
    public void createListJoinGames() {
        assertDoesNotThrow(() -> {
            serverFacade.clear();
            RegisterUserResult result = serverFacade.register(new RegisterUserInput("maggie", "magic", "myemail"));
            String auth = result.authToken();

            int gameId = serverFacade.createGame(auth, new CreateGameInput("InspiringName")).gameID();
            GameData game = new GameData(gameId, "InspiringName", null, null, new ChessGame());
            ArrayList games = serverFacade.listGames(auth);
            assertEquals(new ArrayList(List.of(game)), games);

            serverFacade.joinGame(auth, new JoinGameInput(gameId, ChessGame.TeamColor.WHITE));
            game = new GameData(gameId, "InspiringName", "maggie", null, new ChessGame());
            games = serverFacade.listGames(auth);
            assertEquals(new ArrayList(List.of(game)), games);
        });
    }


}
