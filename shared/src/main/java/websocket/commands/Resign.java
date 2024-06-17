package websocket.commands;

public class Resign extends UserGameCommand {
    public Resign(String authToken) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
    }
}
