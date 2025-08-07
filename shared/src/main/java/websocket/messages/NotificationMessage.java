package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage{

    private String notificationMessage;

    public NotificationMessage(String notificationMessage){
        super(ServerMessageType.NOTIFICATION);
        this.notificationMessage = notificationMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(notificationMessage, that.notificationMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), notificationMessage);
    }

    public String getNotificationMessage(){
        return notificationMessage;
    }
}
