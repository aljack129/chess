package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", Server::register);
        javalin.delete("/db", ctx -> ctx.result("{}"));
        javalin.post("/session", Server::logIn);
        javalin.delete("/session", Server::logOut);
        javalin.get("/game", Server::listGames);
        javalin.post("/game", Server::createGame);
        javalin.put("/game", Server::joinGame);

    }

    private static void register(Context ctx){
        var result = Map.of("username", "","authToken", "");
        ctx.result(new Gson().toJson(result));
    }

    private static void clear(){

    }

    private static void logIn(Context ctx){

    }

    private static void logOut(Context ctx){

    }

    private static void listGames(Context ctx){

    }

    private static void createGame(Context ctx){

    }

    private static void joinGame(Context ctx){

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
