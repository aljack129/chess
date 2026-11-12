package client;

import java.util.Arrays;

import model.*;

public class Client {
    private Boolean signedIn = false;
    private String authToken = "";
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
        if (params.length < 2) {
            throw new ResponseException("Expected: <username> <password>");
        }
        String username = params[0];
        String password = params[1];
        server.logIn(new LoginInput(username, password));
        signedIn = true;
        return "Logged in as " + username;
    }

    public String register(String... params) {
        if (params.length < 3) {
            throw new ResponseException("Expected: <username> <password>");
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        server.register(new RegisterUserInput(username, password, email));
        signedIn = true;
        return "Registered and logged in as" + username;
    }

    public String logOut() {
        return "Logged out as";
    }

    public String clear() {
        return "Database cleared";
    }

    public String createGame(String... params) {
        return "has GameId";
    }

    public String playGame(String... params) {
        return "gamename";
    }

    public String observeGame(String... params) {
        return "gamename";
    }

    public String listGames() {
        return "Games:";
    }

    public String help() {
        if (signedIn) {
            return """
                    - list
                    - createGame <game name>
                    - play <gameID> <color>
                    - observe <gameID> <color>
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
