package websocket.commands;

public class Connect extends UserGameCommand {
    private int gameID;

    public Connect(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
    }
}
