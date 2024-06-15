package ui;

import java.io.PrintStream;

import chess.*;
import static ui.EscapeSequences.*;

public class BoardUI {
    private static ChessBoard board;

    private static final int DRAWING_SIZE_IN_SQUARES = 10;
    private static final int BOARD_SIZE_IN_SQUARES = 8;


    public BoardUI() {
        board = new ChessBoard();
    }


    public static void printBoard(PrintStream out) {
        drawWhiteBoard(out);
        out.println();
        drawBlackBoard(out);
    }

    private static void drawBlackBoard(PrintStream out) {
        drawHeader(out, "BLACK");
        drawRows(out, "BLACK");
        drawHeader(out, "BLACK");
    }

    private static void drawWhiteBoard(PrintStream out) {
        drawHeader(out, "WHITE");
        drawRows(out, "WHITE");
        drawHeader(out, "WHITE");
    }


    private static void drawHeader(PrintStream out, String team) {
        String[] header = new String[0];
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        switch (team) {
            case "WHITE":
                header = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
                break;
            case "BLACK":
                header = new String[] {"H", "G", "F", "E", "D", "C", "B", "A"};
                break;
        }
        for (int col = 0; col < DRAWING_SIZE_IN_SQUARES; ++col) {
            if (col == 0 || col == DRAWING_SIZE_IN_SQUARES -1) {
                out.print(EMPTY);
            }
            else {
                out.print(header[col]);
            }
        }
    }

    private static void drawRows(PrintStream out, String team) {
        int boardCol = 0;
        int boardRow = 0;
        for (int row = 1; row < BOARD_SIZE_IN_SQUARES - 2; row++) {
            for (int col = 0; col < DRAWING_SIZE_IN_SQUARES; col++) {
                boardCol = col - 1;
                boardRow = row - 1;

                // print side headers
                if (col == 0 || col == DRAWING_SIZE_IN_SQUARES - 1) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(boardRow);
                }
                // print board row
                else {
                    String piece = getPiece(boardRow, boardCol);

                    // set square color
                    if (row % 2 == 0) {
                        if (col % 2 == 0) {
                            out.print(SET_BG_COLOR_WHITE);
                        }
                        else {
                            out.print(SET_BG_COLOR_BLACK);
                        }
                    }
                    else {
                        if (col % 2 == 0) {
                            out.print(SET_BG_COLOR_BLACK);
                        }
                        else {
                            out.print(SET_BG_COLOR_WHITE);
                        }
                    }
                    out.print(piece);
                }
            }
        }
    }

    private static String getPiece(int boardRow, int boardCol) {
        ChessPosition position = new ChessPosition(boardRow, boardCol);

        if (board.getPiece(position) == null) {
            return EMPTY;
        }

        ChessPiece piece = board.getPiece(position);
        String pieceString = "";

        switch (piece.getTeamColor()) {
            case WHITE -> {
                switch (piece.getPieceType()) {
                    case KING -> pieceString = WHITE_KING;
                    case QUEEN -> pieceString = WHITE_QUEEN;
                    case ROOK -> pieceString = WHITE_ROOK;
                    case BISHOP -> pieceString = WHITE_BISHOP;
                    case KNIGHT -> pieceString = WHITE_KNIGHT;
                    case PAWN -> pieceString = WHITE_PAWN;
                }
            }
            case BLACK -> {
                switch (piece.getPieceType()) {
                    case KING -> pieceString = BLACK_KING;
                    case QUEEN -> pieceString = BLACK_QUEEN;
                    case ROOK -> pieceString = BLACK_ROOK;
                    case BISHOP -> pieceString = BLACK_BISHOP;
                    case KNIGHT -> pieceString = BLACK_KNIGHT;
                    case PAWN -> pieceString = BLACK_PAWN;
                }
            }
        }
        return pieceString;
    }
}

