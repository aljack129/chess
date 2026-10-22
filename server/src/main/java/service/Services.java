package service;

import chess.*;
import dataaccess.*;
import model.*;
import server.AlreadyTakenException;
import server.NotFoundException;
import server.UnauthorizedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Services {

    private DataAccess database;

    public Services(DataAccess data) {
        database = data;
    }

    public HashMap<String, String> registerUser(String username, String password, String email) {
        UserData user = database.getUserData(username);
        if (user == null) {
            database.createUser(username, password, email);
            String authToken = database.addAuth(username);
            return new HashMap<>(Map.of("usernmae", username, "authToken", authToken));
        } else {
            throw new AlreadyTakenException("Error:username already taken");
        }
    }

    public HashMap<String, String> loginUser(String username, String password) {
        UserData user = database.getUserData(username);
        if (user != null) {
            if (user.password().equals(password)) {
                String authToken = database.addAuth(username);
                return new HashMap<>(Map.of(username, authToken));
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } else {
            throw new NotFoundException("Error: bad request");
        }
    }

    public void logoutUser(String authToken) {
        UserData user = database.verifyUser(authToken);
        if (user != null) {
            database.deleteAuth(authToken);
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public ArrayList listGames(String authToken) {
        UserData user = database.verifyUser(authToken);
        if (user != null) {
            return database.getGames();
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public int createGame(String authToken, String gameName) {
        UserData user = database.verifyUser(authToken);
        if (user != null) {
            return database.createGame(gameName);
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) {
        UserData user = database.verifyUser(authToken);
        if (user != null) {
            GameData game = database.findGame(gameID);
            if (game != null) {
                if (color == ChessGame.TeamColor.WHITE) {
                    if (game.whiteUsername() == null) {
                        game = game.addUser(color, user);
                    } else {
                        throw new AlreadyTakenException("Error: Already Taken");
                    }
                } else {
                    if (game.blackUsername() == null) {
                        game = game.addUser(color, user);
                    } else {
                        throw new AlreadyTakenException("Error: Already Taken");
                    }
                }
            } else {
                throw new NotFoundException("Error: bad request");
            }
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void clear() {
        database.clear();
    }
}
