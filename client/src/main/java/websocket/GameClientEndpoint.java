package websocket;

import com.google.gson.Gson;
import state.ClientState;
import ui.GameplayUI;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import java.io.IOException;

public class GameClientEndpoint implements WebSocketListener {

    private Session session;
    private ClientState state;
    private GameplayUI ui;
    private static final Gson gson = new Gson();

    public GameClientEndpoint(ClientState state, GameplayUI ui){
        this.state = state;
        this.ui = ui;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        System.out.println("WebSocket connected");
    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {

    }

    @Override
    public void onWebSocketText(String messageJson) {
        ServerMessage message = gson.fromJson(messageJson, ServerMessage.class);
        switch (message.getServerMessageType()){
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = gson.fromJson(messageJson, LoadGameMessage.class);
                state.setGame(loadGameMessage.getGame());
                GameplayUI.writeBoard(state.getPlayerColor(), state, null, new String[]{});
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = gson.fromJson(messageJson, NotificationMessage.class);
                System.out.println("NOTIFICATION: " + notificationMessage.getNotificationMessage());
            }
            case ERROR -> {
                ErrorMessage errorMessage = gson.fromJson(messageJson, ErrorMessage.class);
                System.out.println("ERROR: " + errorMessage.getErrorMessage());
            }
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason);
        this.session = null;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket error");
    }

    public void sendMessage(Object command){
        try {
            if (session != null && session.isOpen()) {
                String json = gson.toJson(command);
                session.getRemote().sendString(json);
            } else {
                System.err.println("WebSocket session is not open.");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}

