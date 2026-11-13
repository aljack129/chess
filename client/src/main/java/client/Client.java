package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import model.*;
import chess.*;
import ui.*;

public class Client {
    private Boolean signedIn = false;
    private String auth = null;
    private final ServerFacade server;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to chess! Sign in or Register to start");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.println(">>>");
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
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
            System.out.print(result.authToken());
            auth = result.authToken();
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
            auth = result.authToken();
            signedIn = true;
            return "Registered and logged in as " + username;
        }
        throw new ResponseException("Already logged in");
    }

    public String logOut() {
        if (signedIn) {
            server.logOut(auth);
            signedIn = false;
            auth = "";
            return "Logged out as ";
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
            CreateGameResult result = server.createGame(auth, new CreateGameInput(gameName));
            return gameName + " has GameId " + result.gameID();
        }
        throw new ResponseException("Not signed in");
    }

    public String playGame(String... params) {
        if (signedIn) {
            int gameId;
            try {
                gameId = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException("gameID must be a valid integer");
            }
            if (gameId < 1) {
                throw new ResponseException("gameID must be greater than 0");
            }

            String color_string = params[1].toUpperCase();
            ChessGame.TeamColor color;
            if (color_string.equals("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            } else if (color_string.equals("WHITE")) {
                color = ChessGame.TeamColor.WHITE;
            } else {
                throw new ResponseException("Expected [WHITE|BLACK]");
            }
            server.joinGame(auth, new JoinGameInput(gameId, color));
            GameData game = findGame(gameId);
            DrawChessBoard.main(new String[]{new Gson().toJson(game.game().getBoard()), color_string});
            return game.gameName() + " joined as " + color_string;
        }
        throw new ResponseException("Not signed in");
    }

    public String observeGame(String... params) {
        if (signedIn) {
            int gameId;
            try {
                gameId = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException("gameID must be a valid integer");
            }
            if (gameId < 1) {
                throw new ResponseException("gameID must be greater than 0");
            }
            GameData game = findGame(gameId);
            DrawChessBoard.main(new String[]{new Gson().toJson(game.game().getBoard()), "WHITE"});
            return game.gameName() + " joined as observer";
        }
        throw new ResponseException("Not signed in");
    }

    public String listGames() {
        if (signedIn) {
            ArrayList<GameData> games = server.listGames(auth);
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

    private GameData findGame(int gameID) {
        ArrayList<GameData> games = server.listGames(auth);
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new ResponseException("Game not found");
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
