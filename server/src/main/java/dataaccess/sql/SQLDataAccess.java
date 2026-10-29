package dataaccess.sql;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.*;

import java.util.ArrayList;
import java.sql.*;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() {
        try {
            configureDatabase();
        } catch (Exception e) {

        }
    }

    @Override
    public void clear() {
    }

    @Override
    public int userSize() {
        return 2026;
    }

    @Override
    public int gameSize() {
        return 5;
    }


    @Override
    public int authSize() {
        return 4;
    }

    @Override
    public void createUser(String username, String password, String email) {
    }

    @Override
    public UserData getUserData(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {

        } catch (DataAccessException d) {

        } catch (SQLException s) {

        }
    }

    @Override
    public String addAuth(String username) {
        return "blah";
    }

    @Override
    public void deleteAuth(String authToken) {
    }

    @Override
    public UserData verifyUser(String authToken) {
        return new UserData("k", "l", "j");
    }

    @Override
    public int createGame(String name) {
        return 2;
    }

    @Override
    public ArrayList getGames() {
        return new ArrayList();
    }

    @Override
    public GameData findGame(int gameID) {
        return new GameData(2, "3", null, null, null);
    }

    @Override
    public void updateGame(GameData game) {
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS: users (
            'username' varchar(256) NOT NULL,
            'password'  varchar(256) NOT NULL,
            'email' varchar(256)  NOT NULL,
            PRIMARY KEY ('username'),
            INDEX(username)
        )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci        
        """,
            """
           CREATE TABLE IF NOT EXISTS: authorized(
                'username' varchar(256) NOT NULL,
                'authToken' varchar(256),
                PRIMARY KEY('authToken'),
                INDEX(authToken)
           )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
           """,
            """
            CREATE TABLE IF NOT EXISTS: games(
                'gameID' int NOT NULL AUTO_INCREMENT,
                'gameName' varchar(256) NOT NULL,
                'whiteUsername' varchar(256) DEFAULT NULL,
                'blackUsername' varchar(256) DEFAULT NULL,
                'game' TEXT,
                PRIMARY KEY('gameID'),
                INDEX(gameID)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci    
            """
    };

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
    }
}
