package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private static Services services = new Services(new MemoryDataAccess());

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
        try {
            String json = ctx.body();
            Gson gson = new Gson();
            RegisterUserInput input = null;
            try {
                input = gson.fromJson(json, RegisterUserInput.class);
            } catch (Exception e) {
                ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
                return;
            }

            if (input.username() == null || input.password() == null || input.email() == null) {
                ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
                return;
            }

            RegisterUserResult result = services.registerUser(input);
            ctx.status(200).result(gson.toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403).result(new Gson().toJson(Map.of("message", "Error: already taken")));
        }
    }

    private static void clear(Context ctx) {
        services.clear();
        ctx.status(200);
    }

    private static void logIn(Context ctx) {
        String json = ctx.body();
        Gson gson = new Gson();
        LoginInput input = null;
        try {
            input = gson.fromJson(json, LoginInput.class);
        } catch (Exception e) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
            return;
        }

        if (input.username() == null || input.password() == null) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
            return;
        }

        try {
            LoginResult result = services.loginUser(input);
            ctx.status(200).result(gson.toJson(result));

        } catch (UnauthorizedException e) {
            ctx.status(401).result(new Gson().toJson(Map.of("message", "Error: unauthorized")));

        } catch (NotFoundException e) {
            ctx.status(401).result(new Gson().toJson(Map.of("message", "Error: bad request")));

        }
    }

    private static void logOut(Context ctx) {
        try {
            services.logoutUser(ctx.header("authorization"));
            ctx.status(200);
        } catch (UnauthorizedException e) {
            ctx.status(401).result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private static void listGames(Context ctx) {
        try {
            ArrayList<GameData> gameList = services.listGames(ctx.header("authorization"));

            ctx.result(new Gson().toJson(Map.of("games", gameList)));
        } catch (UnauthorizedException e) {
            ctx.status(401).result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private static void createGame(Context ctx) {

        String json = ctx.body();
        Gson gson = new Gson();
        CreateGameInput input = null;
        try {
            input = gson.fromJson(json, CreateGameInput.class);
        } catch (Exception e) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
            return;
        }

        if (input.gameName() == null) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
            return;
        }

        try {
            CreateGameResult result = services.createGame(ctx.header("authorization"), input);
            ctx.result(gson.toJson(result));
        } catch (UnauthorizedException e) {
            ctx.status(401).result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private static void joinGame(Context ctx) {

        String json = ctx.body();
        Gson gson = new Gson();
        JoinGameInput input = null;
        try {
            input = gson.fromJson(json, JoinGameInput.class);
        } catch (Exception e) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
            return;
        }

        if (input == null || input.playerColor() == null) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
            return;
        }

        try {
            services.joinGame(ctx.header("authorization"), input);
            ctx.status(200);
        } catch (AlreadyTakenException e) {
            ctx.status(403).result(new Gson().toJson(Map.of("message", "Error: already taken")));
        } catch (NotFoundException e) {
            ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedException e) {
            ctx.status(401).result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
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
