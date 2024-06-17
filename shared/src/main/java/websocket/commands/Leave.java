package websocket.commands;

public class Leave extends UserGameCommand{
    public Leave(String authToken) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
    }
}
