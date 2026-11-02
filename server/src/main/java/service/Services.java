package service;

import chess.*;
import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Services {

    private DataAccess database;

    public Services(DataAccess data) {
        database = data;
    }

    public RegisterUserResult registerUser(RegisterUserInput input) throws DataAccessException {
        //RegisterUserInput input = new RegisterUserInput(username, password, email);
        try {
            UserData user = database.getUserData(input.username());
            if (user == null) {
                String hashed_password = BCrypt.hashpw(input.password(), BCrypt.gensalt());
                database.createUser(input.username(), hashed_password, input.email());
                String authToken = database.addAuth(input.username());
                return new RegisterUserResult(input.username(), authToken);
            } else {
                throw new AlreadyTakenException("Error:username already taken");
            }
        } catch (DataAccessException d) {
            throw new DataAccessException("Error: DataAccess");
        }
    }

    public LoginResult loginUser(LoginInput input) throws DataAccessException {
        try {
            UserData user = database.getUserData(input.username());
            if (user != null) {
                if (BCrypt.checkpw(user.password(), input.password())) {
                    String authToken = database.addAuth(input.username());
                    return new LoginResult(input.username(), authToken);
                } else {
                    throw new UnauthorizedException("Error: unauthorized");
                }
            } else {
                throw new NotFoundException("Error: bad request");
            }
        } catch (DataAccessException d) {
            throw new DataAccessException("Error: dataaccess");
        }
    }

    public void logoutUser(String authToken) throws DataAccessException {
        try {
            UserData user = database.verifyUser(authToken);
            if (user != null) {
                database.deleteAuth(authToken);
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("database problems");
        }
    }

    public ArrayList listGames(String authToken) throws DataAccessException {
        try {
            UserData user = database.verifyUser(authToken);

            if (user != null) {
                return database.getGames();
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException d) {
            throw new DataAccessException("database problems");
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameInput input) throws DataAccessException {
        try {
            UserData user = database.verifyUser(authToken);
            if (user != null) {
                return new CreateGameResult(database.createGame(input.gameName()));
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException d) {
            throw new DataAccessException("database problems");
        }
    }

    public void joinGame(String authToken, JoinGameInput input) throws DataAccessException {
        try {
            UserData user = database.verifyUser(authToken);
            if (user != null) {
                GameData game = database.findGame(input.gameID());
                if (game != null) {
                    if (input.playerColor() == ChessGame.TeamColor.WHITE) {
                        if (game.whiteUsername() == null) {
                            game = game.addUser(input.playerColor(), user);
                            database.updateGame(game);
                        } else {
                            throw new AlreadyTakenException("Error: Already Taken");
                        }
                    } else if (input.playerColor() == ChessGame.TeamColor.BLACK) {
                        if (game.blackUsername() == null) {
                            game = game.addUser(input.playerColor(), user);
                            database.updateGame(game);
                        } else {
                            throw new AlreadyTakenException("Error: Already Taken");
                        }
                    } else {
                        throw new ForbiddenException("Error: Forbidden");
                    }
                } else {
                    throw new NotFoundException("Error: bad request");
                }
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException d) {
            throw new DataAccessException("database problems");
        }
    }

    public void clear() throws DataAccessException {
        database.clear();
    }
}
