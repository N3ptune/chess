package websocket;

import com.google.gson.Gson;
import state.ClientState;
import ui.GameplayUI;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;

@ClientEndpoint
public class GameClientEndpoint {

    private Session session;
    private ClientState state;
    private GameplayUI ui;
    private static final Gson gson = new Gson();

    public GameClientEndpoint(ClientState state, GameplayUI ui){
        this.state = state;
        this.ui = ui;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connected");
    }

    @OnMessage
    public void onMessage(String messageJson) {
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

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason.getReasonPhrase());
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void sendMessage(Object command){
        try {
            if (session != null && session.isOpen()) {
                String json = gson.toJson(command);
                session.getBasicRemote().sendText(json);
            } else {
                System.err.println("WebSocket session is not open.");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}

