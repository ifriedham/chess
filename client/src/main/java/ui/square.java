package ui;

import chess.ChessPiece;

public class square {
    private boolean isOccupied;
    private ChessPiece piece;
    private int x;
    private int y;

    public square() {
        this.isOccupied = false;
        this.piece = null;
        this.x = 0;
        this.y = 0;
    }
    public square(int x, int y, ChessPiece piece, boolean isOccupied) {
        this.x = x;
        this.y = y;
        this.isOccupied = isOccupied;
        this.piece = piece;
    }
}
