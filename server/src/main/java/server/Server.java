package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private static final Services services = new Services(new MemoryDataAccess());

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", Server::register);
        javalin.delete("/db", Server::clear);
        javalin.post("/session", Server::logIn);
        javalin.delete("/session", Server::logOut);
        javalin.get("/game", Server::listGames);
        javalin.post("/game", Server::createGame);
        javalin.put("/game", Server::joinGame);

    }

    private static void register(Context ctx) {
        //var result = Map.of("username", "", "authToken", "");
        try {
            HashMap<String, String> body = ctx.bodyAsClass(HashMap.class);
            HashMap result = services.registerUser(body.get("username"), body.get("password"), body.get("email"));
            ctx.result(new Gson().toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", "Error: already taken")));
        }
    }

    private static void clear(Context ctx) {
        services.clear();
        ctx.status(200);
    }

    private static void logIn(Context ctx) {
        HashMap<String, String> body = ctx.bodyAsClass(HashMap.class);
        try {
            HashMap result = services.loginUser(body.get("username"), body.get("password"));
            ctx.result(new Gson().toJson(result));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        } catch (NotFoundException e) {
            ctx.status(400);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        }
    }

    private static void logOut(Context ctx) {
        try {
            services.logoutUser(ctx.header("authorization"));
            ctx.status(204);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private static void listGames(Context ctx) {
        try {
            ArrayList<GameData> gameList = services.listGames(ctx.header("authorization"));
            ArrayList mappedGames = new ArrayList();
            for (GameData game : gameList) {
                mappedGames.add(Map.of(
                        "gameId", game.gameID(),
                        "whiteUsername", game.whiteUsername().username(),
                        "blackUsername", game.blackUsername().username(),
                        "gameName", game.name()
                ));
            }
            ctx.result(new Gson().toJson(Map.of("games", mappedGames)));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private static void createGame(Context ctx) {
        HashMap<String, String> body = ctx.bodyAsClass(HashMap.class);
        try {
            int gameID = services.createGame(ctx.header("authorization"), body.get("gameName"));
            ctx.result(new Gson().toJson(Map.of("gameID", gameID)));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private static void joinGame(Context ctx) {
        HashMap<String, String> body = ctx.bodyAsClass(HashMap.class);
        String colorString = body.get("playerColor");
        ChessGame.TeamColor color;
        if (colorString == "WHITE") {
            color = ChessGame.TeamColor.WHITE;
        } else {
            color = ChessGame.TeamColor.BLACK;
        }
        try {
            services.joinGame(ctx.header("authorization"), Integer.valueOf(body.get("gameID")), color);
            ctx.status(200);
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", "Error: already taken")));
        } catch (NotFoundException e) {
            ctx.status(400);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            //ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
