package ui;

import chess.ChessBoard;
import client.ServerFacade;

public class ConsoleUI {
    ServerFacade facade;
    ChessBoard board;

    public ConsoleUI(ServerFacade facade) {
        this.facade = facade;
        this.board = new ChessBoard();
    }


    public void run() {
    }
}
