package facade;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.request.*;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public AuthData login(String username, String password){
        LoginRequest loginRequest = new LoginRequest(username, password);
        var result = makeRequest("POST", "/session", loginRequest, AuthData.class, null);
        return result;
    }

    public AuthData register(String username, String password, String email){
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        var result = makeRequest("POST", "/user", registerRequest, AuthData.class, null);
        return result;
    }

    public void logout(String authToken){
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        makeRequest("DELETE", "/session", logoutRequest, null, authToken);
    }

    public Integer createGame(String gameName, String authToken){
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        var result = makeRequest("POST", "/game", createGameRequest, CreateGameResult.class, authToken);
        if (result == null){
            return  null;
        }
        return result.gameID();
    }

    public Collection<GameData> listGames(String authToken){
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        var result = makeRequest("GET", "/game", null, ListGamesResult.class, authToken);
        if (result == null){
            return  null;
        }
        return result.games();
    }

    public void joinGame(Integer gameID, String username, ChessGame.TeamColor playerColor, String authToken) throws IOException {
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, playerColor, gameID);
        JoinGameResult result = makeRequest("PUT", "/game", joinGameRequest, JoinGameResult.class, authToken);
        if (result == null){
            throw new IOException();
        }
        if (result.message() != null ) {
            throw new IOException(result.message());
        }
    }

    public <T>T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken){
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);


            if (authToken != null){
                http.addRequestProperty("Authorization", authToken);
            }

            if ( request != null && (method.equals("POST") || method.equals("PUT"))){
                writeBody(request, http);
            }

            http.connect();

            return readBody(http, responseClass);
        } catch (Exception e){
            return null;
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null){
            http.setRequestProperty("Content-Type", "application/json");
            try (OutputStream reqBody = http.getOutputStream()){
                OutputStreamWriter writer = new OutputStreamWriter(reqBody);
                new Gson().toJson(request, writer);
                writer.flush();
            }
        }
    }

    private <T>T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        InputStream responseBody;
        if (http.getResponseCode() >= 200 && http.getResponseCode() <=300){
            responseBody = http.getInputStream();
        } else {
            responseBody = http.getErrorStream();
            throw new IOException("Http error code" + http.getResponseCode());
        }

        try (InputStreamReader reader = new InputStreamReader(responseBody)){
            T response = new Gson().fromJson(reader, responseClass);

            if (http.getResponseCode() < 200 || http.getResponseCode() > 300){
                throw new IOException("Http error code" + http.getResponseCode());
            }

            return response;
        }
    }
}
