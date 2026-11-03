package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.*;

import java.util.ArrayList;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() throws DataAccessException {
        try {
            configureDatabase();
        } catch (Exception e) {
            throw new DataAccessException("");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String[] clearStatements = {
                "TRUNCATE TABLE users;",
                "TRUNCATE TABLE authorized;",
                "TRUNCATE TABLE games;"
        };
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : clearStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("database couldn't configure");
        }
    }

    @Override
    public int userSize() {
        var statement = "SELECT count(*) AS row_count FROM users";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("row_count");
                    }
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    @Override
    public int gameSize() {
        var statement = "SELECT count(*) AS row_count FROM games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("row_count");
                    }
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }


    @Override
    public int authSize() {
        var statement = "SELECT count(*) AS row_count FROM authorized";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("row_count");
                    }
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        var statement = "INSERT INTO users(username, password, email, json) VALUES(?, ?, ?, ?)";
        UserData user = new UserData(username, password, email);
        String json = new Gson().toJson(user);
        try {
            executeUpdate(statement, username, password, email, json);
        } catch (Exception e) {

        }
    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users where username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        UserData user = new Gson().fromJson(json, UserData.class);
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("database problem");
        }
        return null;
    }

    @Override
    public String addAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO authorized(authToken, username, json) VALUES(?, ?, ?)";
        AuthData auth = new AuthData(null, username).createAuth();
        String json = new Gson().toJson(auth);
        try {
            executeUpdate(statement, auth.authToken(), username, json);
            return auth.authToken();
        } catch (Exception e) {
            throw new DataAccessException("database problem");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authorized WHERE authToken=?";
        try {
            executeUpdate(statement, authToken);
        } catch (Exception e) {
            throw new DataAccessException("database problem");
        }
    }

    @Override
    public UserData verifyUser(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM authorized where authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        UserData user = getUserData(username);
                        return user;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("database problems");
        }
    }

    @Override
    public int createGame(String name) throws DataAccessException {
        var statement = "INSERT INTO games(gameName, game, json) VALUES(?, ?, ?)";
        GameData game = new GameData(0, name, null, null, new ChessGame());
        String json = new Gson().toJson(game);
        try {
            int gameID = executeUpdate(statement, name, game.game(), json);
            game = new GameData(gameID, game.gameName(), null, null, game.game());
            updateGame(game);
            return gameID;
        } catch (Exception e) {
            throw new DataAccessException("database problem");
        }
    }

    @Override
    public ArrayList getGames() throws DataAccessException {
        ArrayList games = new ArrayList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData game = new Gson().fromJson(rs.getString("json"), GameData.class);
                        games.add(game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("database problem");
        }
        return games;
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM games where gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        return game;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("database problem");
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ?, json = ? WHERE gameID =?";
        String json = new Gson().toJson(game);
        try {
            executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.game(), json, game.gameID());
        } catch (Exception e) {
            throw new DataAccessException("d");
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param instanceof AuthData p) {
                        ps.setString(i + 1, p.toString());
                    } else if (param instanceof GameData p) {
                        ps.setString(i + 1, p.toString());
                    } else if (param instanceof UserData p) {
                        ps.setString(i + 1, p.toString());
                    } else if (param instanceof ChessGame p) {
                        ps.setString(i + 1, new Gson().toJson(p));
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        }
    }


    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(256) NOT NULL,
            password VARCHAR(256) NOT NULL,
            email VARCHAR(256)  NOT NULL,
            json TEXT DEFAULT NULL,
            PRIMARY KEY (username),
            INDEX(username)
        );        
        """,
            """
           CREATE TABLE IF NOT EXISTS authorized(
                username VARCHAR(256) NOT NULL,
                authToken VARCHAR(256),
                json TEXT,
                PRIMARY KEY(authToken),
                INDEX(authToken)
           );
           """,
            """
            CREATE TABLE IF NOT EXISTS games(
                gameID int NOT NULL AUTO_INCREMENT,
                gameName varchar(256) NOT NULL,
                whiteUsername varchar(256) DEFAULT NULL,
                blackUsername varchar(256) DEFAULT NULL,
                game TEXT,
                json TEXT,
                PRIMARY KEY(gameID),
                INDEX(gameID)
            );   
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("database couldn't configure");
        }
    }


}
