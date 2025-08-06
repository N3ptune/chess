package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebsocketHandler {
    private static Gson gson = new Gson();
    private static Map<Integer, Map<Session, String>> gameSessions = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session){
        System.out.println("Client connected to: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message){
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMove(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode){
        for (Map<Session, String> clients : gameSessions.values()){
            clients.remove(session);
        }
        System.out.println("Connection closed");
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        int gameID = command.getGameID();
        gameSessions.putIfAbsent(gameID, new HashMap<>());
        gameSessions.get(gameID).put(session, command.getAuthToken());
    }

    private void handleMove(Session session, UserGameCommand command){}

    private void handleLeave(Session session, UserGameCommand command){}

    private void handleResign(Session session, UserGameCommand command){
        gameSessions.getOrDefault(command.getGameID(), Map.of()).remove(session);
    }

    private static void broadcast(int gameID, ServerMessage message) throws IOException {
        String json = gson.toJson(message);
        for (Session s : gameSessions.getOrDefault(gameID, Map.of()).keySet()){
            if (s.isOpen()){
                s.getRemote().sendString(json);
            }
        }
    }
}
