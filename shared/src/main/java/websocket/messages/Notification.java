package websocket.messages;

public class Notification extends ServerMessage {
    private String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
