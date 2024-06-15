import chess.*;
import ui.*;
import client.ServerFacade;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ServerFacade facade = new ServerFacade(8080);
        ConsoleUI ui = new ConsoleUI(facade);
        ui.run();
    }
}