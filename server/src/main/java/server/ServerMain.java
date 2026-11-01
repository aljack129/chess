package server;

import chess.*;
import dataaccess.DataAccessException;

public class ServerMain {
    public static void main(String[] args) {
        var port = 8080;
        try {
            Server server = new Server();
            server.run(port);
        } catch (DataAccessException e) {
            System.out.println("SQL Database error");
        }
        System.out.println("♕ 240 Chess Server running on port " + port);
    }
}
