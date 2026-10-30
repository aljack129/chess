package dataaccess.sql;

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
        var statement = "SELECT count(*) AS row_count FROM users";
        try {
            return executeUpdate(statement);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public int gameSize() {
        var statement = "SELECT count(*) AS row_count FROM games";
        try {
            return executeUpdate(statement);
        } catch (Exception e) {
            return -1;
        }
    }


    @Override
    public int authSize() {
        var statement = "SELECT count(*) AS row_count FROM authorized";
        try {
            return executeUpdate(statement);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void createUser(String username, String password, String email) {
    }

    @Override
    public UserData getUserData(String username) {
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
            return null;
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
        CREATE TABLE IF NOT EXISTS: users (
            'username' varchar(256) NOT NULL,
            'password'  varchar(256) NOT NULL,
            'email' varchar(256)  NOT NULL,
            'json' text DEFAULT NULL,
            PRIMARY KEY ('username'),
            INDEX(username)
        )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci        
        """,
            """
           CREATE TABLE IF NOT EXISTS: authorized(
                'username' varchar(256) NOT NULL,
                'authToken' varchar(256),
                'json' text,
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
