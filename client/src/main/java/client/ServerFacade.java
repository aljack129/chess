package client;

import com.google.gson.Gson;

import java.util.ArrayList;

import model.*;
import chess.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterUserResult register(RegisterUserInput input) throws ResponseException {
        var request = buildRequest("POST", "/user", input, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterUserResult.class);

    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    public LoginResult logIn(LoginInput input) throws ResponseException {
        var request = buildRequest("POST", "/session", input, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logOut(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, authToken);
        sendRequest(request);
    }

    public ArrayList listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ArrayList.class);
    }

    public CreateGameResult createGame(String authToken, CreateGameInput input) throws ResponseException {
        var request = buildRequest("POST", "/game", input, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(String authToken, JoinGameInput input) throws ResponseException {
        var request = buildRequest("PUT", "/game", input, authToken);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        BodyPublisher requestBody;
        if (body != null) {
            requestBody = BodyPublishers.ofString(new Gson().toJson(body));
        } else {
            requestBody = BodyPublishers.noBody();
        }

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, requestBody);
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authToken", authToken);
        }

        return request.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException("Could not send to server");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (status / 100 != 2) {
            if (status == 400) {
                throw new ResponseException("Bad request");
            } else if (status == 401) {
                throw new ResponseException("Unauthorized");
            } else if (status == 403) {
                throw new ResponseException("Already Taken");
            } else if (status == 500) {
                throw new ResponseException("Server error");
            }
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }
}
