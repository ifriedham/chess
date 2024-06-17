package websocket.messages;

public class LoadGame extends ServerMessage{
    private int gameID;
    public LoadGame(int gameID) {
        super(ServerMessageType.LOAD_GAME);
        this.gameID = gameID;
    }
}
