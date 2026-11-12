package client;

import java.util.ArrayList;
import java.util.Arrays;

import model.*;

public class Client {
    private Boolean signedIn = false;
    private AuthData auth = null;
    private final ServerFacade server;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] keywords = input.toLowerCase().split(" ");
            String command = (keywords.length > 0) ? keywords[0] : "help";
            String[] parameters = Arrays.copyOfRange(keywords, 1, keywords.length);
            return switch (command) {
                case "login" -> logIn(parameters);
                case "register" -> register(parameters);
                case "logout" -> logOut();
                case "list" -> listGames();
                case "creategame" -> createGame(parameters);
                case "playgame" -> playGame(parameters);
                case "observegame" -> observeGame(parameters);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String logIn(String... params) throws ResponseException {
        if (!signedIn) {
            if (params.length < 2) {
                throw new ResponseException("Expected: <username> <password>");
            }
            String username = params[0];
            String password = params[1];
            LoginResult result = server.logIn(new LoginInput(username, password));
            auth = new AuthData(username, result.authToken());
            signedIn = true;
            return "Logged in as " + username;
        }
        throw new ResponseException("Already logged in");
    }

    public String register(String... params) {
        if (!signedIn) {
            if (params.length < 3) {
                throw new ResponseException("Expected: <username> <password>");
            }
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterUserResult result = server.register(new RegisterUserInput(username, password, email));
            auth = new AuthData(username, result.authToken());
            signedIn = true;
            return "Registered and logged in as" + username;
        }
        throw new ResponseException("Already logged in");
    }

    public String logOut() {
        if (signedIn) {
            server.logOut(auth.authToken());
            return "Logged out as" + auth.username();
        }
        throw new ResponseException("Not signed in");
    }

    public String clear() {
        server.clear();
        return "Database cleared";
    }

    public String createGame(String... params) {
        if (signedIn) {
            String gameName = params[0];
            CreateGameResult result = server.createGame(auth.authToken(), new CreateGameInput(gameName));
            return gameName + " has GameId " + result.gameID();
        }
        throw new ResponseException("Not signed in");
    }

    public String playGame(String... params) {
        if (signedIn) {
            return "gamename";
        }
        throw new ResponseException("Not signed in");
    }

    public String observeGame(String... params) {
        if (signedIn) {
            return "gamename";
        }
        throw new ResponseException("Not signed in");
    }

    public String listGames() {
        if (signedIn) {
            ArrayList<GameData> games = server.listGames(auth.authToken());
            var result = new StringBuilder();
            for (GameData game : games) {
                String white;
                if (game.whiteUsername() != null) {
                    white = game.whiteUsername();
                } else {
                    white = "No Player";
                }

                String black;
                if (game.blackUsername() != null) {
                    black = game.blackUsername();
                } else {
                    black = "No Player";
                }

                String gameInfo = """
                        %d : %s
                        white: %s
                        black: %s
                        """.formatted(game.gameID(), game.gameName(), white, black);
                result.append(gameInfo);
            }
            return "Games:\n" + result;
        }
        throw new ResponseException("Not signed in");
    }

    public String help() {
        if (signedIn) {
            return """
                    - list
                    - createGame <game name>
                    - play <gameID> [WHITE|BLACK]
                    - observe <gameID>
                    - clear
                    - logout
                    - quit
                    """;
        }
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - clear
                """;
    }
}
