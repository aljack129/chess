package service;

import chess.*;
import dataaccess.*;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Services {

    private DataAccess database;

    public Services(DataAccess data) {
        database = data;
    }

    public RegisterUserResult registerUser(RegisterUserInput input) {
        //RegisterUserInput input = new RegisterUserInput(username, password, email);
        UserData user = database.getUserData(input.username());
        if (user == null) {
            database.createUser(input.username(), input.password(), input.email());
            String authToken = database.addAuth(input.username());
            return new RegisterUserResult(input.username(), authToken);
        } else {
            throw new AlreadyTakenException("Error:username already taken");
        }
    }

    public LoginResult loginUser(LoginInput input) {
        UserData user = database.getUserData(input.username());
        if (user != null) {
            if (user.password().equals(input.password())) {
                String authToken = database.addAuth(input.username());
                return new LoginResult(input.username(), authToken);
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

    public CreateGameResult createGame(String authToken, CreateGameInput input) {
        UserData user = database.verifyUser(authToken);
        if (user != null) {
            return new CreateGameResult(database.createGame(input.gameName()));
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
                        database.updateGame(game);
                    } else {
                        throw new AlreadyTakenException("Error: Already Taken");
                    }
                } else {
                    if (game.blackUsername() == null) {
                        game = game.addUser(color, user);
                        database.updateGame(game);
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
