package websocket.commands;

public class Connect extends UserGameCommand {
    private final String playerColor;
    private int gameID;

    public Connect(String authToken, String playerColor, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
        this.playerColor = playerColor;
    }


    public int getGameID() {
        return gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
