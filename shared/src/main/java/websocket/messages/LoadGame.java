package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage{
    private ChessGame game; //gameID
    public LoadGame(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame game() {
        return game;
    }
}
