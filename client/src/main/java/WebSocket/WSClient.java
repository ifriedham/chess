package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.Notification;
import websocket.messages.Error;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGame;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint {

    public Session session;

    public WSClient(String url, ChessBoard chessBoard) throws Exception {
        url = url.replace("http", "ws") + "/connect";
        URI uri = new URI(url);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                try {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME:
                            loadGame(message);
                            break;
                        case ERROR:
                            error(message);
                        case NOTIFICATION:
                            this.notify(message);
                    }
                } catch(Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            private void notify(String message) {
                Notification notification = new Gson().fromJson(message, Notification.class);
                chessBoard.notify();
            }

        });
    }

    private void loadGame(String message) {
        LoadGame loadGameResponse = new Gson().fromJson(message, LoadGame.class);
        ChessGame game = loadGameResponse.game();
        game.setBoard(game.getBoard());
    }

    private void error(String message) {
        Error errorResponse = new Gson().fromJson(message, Error.class);
        System.out.println("Error: " + errorResponse.message());
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
