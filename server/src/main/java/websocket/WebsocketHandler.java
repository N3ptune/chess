package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.MakeMoveCommand;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebSocket
public class WebsocketHandler {
    private static Gson gson = new Gson();
    private static Map<Integer, Map<Session, String>> gameSessions = new HashMap<>();
    public static DataAccess dataAccess;

    public WebsocketHandler(){
    }

    public static void setDataAccess(DataAccess dao){
        WebsocketHandler.dataAccess = dao;
    }

    @OnWebSocketConnect
    public void onConnect(Session session){
        System.out.println("Client connected to: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMove(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason){
        for (Map<Session, String> clients : gameSessions.values()){
            clients.remove(session);
        }
        System.out.println("Connection closed");
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {

        int gameID = command.getGameID();

        try {

            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            GameData gameData = dataAccess.getGame(gameID);

            if (!validationchecks(authData, gameData, gameID, command)){
                return;
            }

            String username = authData.username();

            gameSessions.putIfAbsent(gameID, new HashMap<>());
            gameSessions.get(gameID).put(session, username);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            session.getRemote().sendString(gson.toJson(loadGameMessage));

            String role = determineRole(username, gameData);
            NotificationMessage notificationMessage = new NotificationMessage(username + " has joined the game as: " + role);
            broadcast(gameID, notificationMessage);
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            broadcast(gameID, errorMessage);
        }
    }

    private void handleMove(Session session, UserGameCommand command) throws IOException {
        int gameID = command.getGameID();

        try {
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            GameData gameData = dataAccess.getGame(gameID);

            if (!validationchecks(authData, gameData, gameID, command)){
                return;
            }



            String username = authData.username();

            ChessGame game = gameData.game();

            if (isGameOver(game)){
                return;
            }

            ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
            if (playerColor == null){
                ErrorMessage errorMessage = new ErrorMessage("Error: you are not a player");
                broadcast(gameID, errorMessage);
                return;
            }

            if (game.getTeamTurn() != playerColor){
                ErrorMessage errorMessage = new ErrorMessage("Error: it's not your turn");
                broadcast(gameID, errorMessage);
                return;
            }

            MakeMoveCommand makeMoveCommand = (MakeMoveCommand) command;
            ChessMove move = makeMoveCommand.getMove();

            game.makeMove(move);
            dataAccess.updateGame(gameID, game);

            NotificationMessage notificationMessage = new NotificationMessage(username + " made move: " + move);
            broadcast(gameID, notificationMessage);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            broadcast(gameID, loadGameMessage);

            checkGameState(gameID, game);

            if (isGameOver(game)){
                return;
            }

        } catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            broadcast(gameID, errorMessage);
        }
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        int gameID = command.getGameID();

        try {
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            GameData gameData = dataAccess.getGame(gameID);

            if (!validationchecks(authData, gameData, gameID, command)){
                return;
            }

            String username = authData.username();

            if (username.equals(gameData.whiteUsername())){
                dataAccess.joinGame(gameID, null, ChessGame.TeamColor.WHITE);
            } else if (username.equals(gameData.blackUsername())){
                dataAccess.joinGame(gameID, null, ChessGame.TeamColor.BLACK);
            }

            gameSessions.getOrDefault(gameID, Map.of()).remove(session);

            ServerMessage leaveMessage = new NotificationMessage(username + " has left the game");
            broadcast(gameID, leaveMessage);

        } catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            broadcast(gameID, errorMessage);
        }
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        int gameID = command.getGameID();

        try {
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            GameData gameData = dataAccess.getGame(gameID);

            if (!validationchecks(authData, gameData, gameID, command)){
                return;
            }

            String username = authData.username();
            ChessGame game = gameData.game();

            ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
            if (playerColor == null){
                ErrorMessage errorMessage = new ErrorMessage("Error: you are not a player");
                broadcast(gameID, errorMessage);
                return;
            }

            if (isGameOver(game)){
                return;
            }

            NotificationMessage notificationMessage = new NotificationMessage(username + " has resigned");
            broadcast(gameID, notificationMessage);

        } catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            broadcast(gameID, errorMessage);
        }
    }

    private void checkGameState(int gameID, ChessGame game) throws IOException {
        if (game.isInCheck(ChessGame.TeamColor.WHITE)){
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
                NotificationMessage notificationMessage = new NotificationMessage("White is in checkmate, Black wins");
                broadcast(gameID, notificationMessage);
            } else {
                NotificationMessage notificationMessage = new NotificationMessage("White is in check");
                broadcast(gameID, notificationMessage);
            }
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                NotificationMessage notificationMessage = new NotificationMessage("Black is in checkmate, White wins");
                broadcast(gameID, notificationMessage);
            } else {
                NotificationMessage notificationMessage = new NotificationMessage("Black is in check");
                broadcast(gameID, notificationMessage);
            }
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)){
            NotificationMessage notificationMessage = new NotificationMessage("Game ends in stalemate");
            broadcast(gameID, notificationMessage);
        }
    }

    private String determineRole(String username, GameData gameData){
        if (username.equals(gameData.whiteUsername())) {
            return "white player";
        } else if (username.equals(gameData.blackUsername())) {
            return "black player";
        } else {
            return "observer";
        }
    }

    private ChessGame.TeamColor getPlayerColor(String username, GameData gameData){
        if (username.equals(gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    private static void broadcast(int gameID, ServerMessage message) throws IOException {
        String json = gson.toJson(message);
        for (Session s : gameSessions.getOrDefault(gameID, Map.of()).keySet()){
            if (s.isOpen()){
                s.getRemote().sendString(json);
            }
        }
    }

    private boolean validationchecks(AuthData authData, GameData gameData, int gameID, UserGameCommand command) throws DataAccessException, IOException, SQLException {
        if (authData == null){
            ErrorMessage errorMessage = new ErrorMessage("Error: invalid auth token");
            broadcast(gameID, errorMessage);
            return false;
        }
        if (gameData == null){
            ErrorMessage errorMessage = new ErrorMessage("Error: game does not exist");
            broadcast(gameID, errorMessage);
            return false;
        }
        return true;
    }

    private boolean isGameOver(ChessGame game){
        return  game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInStalemate(ChessGame.TeamColor.BLACK);
    }
}
