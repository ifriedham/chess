package WebSocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WSClient extends Endpoint {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
    }

    public Session session;
    private ChessBoard board;
    private ChessGame game;

    public WSClient(String url, ChessBoard chessBoard) throws Exception {
        board = chessBoard;

        url = url.replace("http", "ws") + "connect";
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
                }
            }

            private void notify(String message) {
                NotificationResponse notification = new Gson().fromJson(message, NotificationResponse.class);
                chessBoard.notify(notification.message());
            }

        });
    }

    private void loadGame(String message) {
        LoadGameResponse loadGameResponse = new Gson().fromJson(message, LoadGameResponse.class);
        game = loadGameResponse.game();
        board.setGame(game);
    }

    private void error(String message) {
        ErrorResponse errorResponse = new Gson().fromJson(message, ErrorResponse.class);
        System.out.println("Error: " + errorResponse.message());
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
