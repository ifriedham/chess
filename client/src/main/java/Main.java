import chess.*;
import ui.*;
import client.ServerFacade;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Server server = new Server();
        var port = server.run(8080);
        System.out.println("Started HTTP server on " + port);

        ServerFacade facade = new ServerFacade(port);
        ConsoleUI ui = new ConsoleUI(facade);
        ui.run();

        server.stop();
    }
}