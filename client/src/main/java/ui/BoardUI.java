package ui;

import java.io.PrintStream;

import chess.*;
import static ui.EscapeSequences.*;

public class BoardUI {
    private static final int DRAWING_SIZE_IN_SQUARES = 10;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static ChessBoard board;
    private static PrintStream out = null;

    public BoardUI(PrintStream outStream, ChessGame givenGame) {
        board = givenGame.getBoard();
        out = outStream;
    }


    public void printBoards() {
        drawBoard("BLACK");
        out.println();
        drawBoard("WHITE");
    }

    private static void drawBoard(String team) {
        drawHeader(team);
        drawRows(team);
        drawHeader(team);
    }


    private static void drawHeader(String team) {
        String headerLine = "";
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        headerLine = switch (team) {
            case "WHITE" -> "    A   B  C   D   E  F   G   H    ";

            case "BLACK" -> "    H   G  F   E   D  C   B   A    ";
            default -> headerLine;
        };

        out.print(headerLine);
        out.print(RESET_TEXT_COLOR);
        out.println(RESET_BG_COLOR);
    }

    private static void drawRows(String team) {
        switch (team) {
            case "WHITE":
                for (int row = BOARD_SIZE_IN_SQUARES; row >= 1; row--) {
                    for (int col = 0; col < DRAWING_SIZE_IN_SQUARES; col++) {
                        printRow(row, col);
                    }
                    out.println(RESET_BG_COLOR);
                }
                break;

            case "BLACK":
                for (int row = 1; row <= BOARD_SIZE_IN_SQUARES; row++) {


                    for (int col = DRAWING_SIZE_IN_SQUARES - 1; col >= 0; col--) {
                        printRow(row, col);
                    }
                    out.println(RESET_BG_COLOR);
                }
                break;
        }
    }

    private static void printRow(int row, int col) {
        // print side header squares
        if (col == 0 || col == DRAWING_SIZE_IN_SQUARES - 1) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_BLACK);

            int paddingSize = (EMPTY.length() - 1) / 2; // Subtract 1 for the row number itself
            String sideSquare = String.format("%" + paddingSize + "s" + row + "%" + paddingSize + "s", "", "");
            out.print(sideSquare);
        }

        // print board row
        else {
            // set square color
            if (row % 2 == 0) {
                if (col % 2 == 0) {
                    out.print(SET_BG_COLOR_DARK_PURPLE);
                } else {
                    out.print(SET_BG_COLOR_LIGHT_PURPLE);
                }
            } else {
                if (col % 2 == 0) {
                    out.print(SET_BG_COLOR_LIGHT_PURPLE);
                } else {
                    out.print(SET_BG_COLOR_DARK_PURPLE);
                }
            }

            String piece = getPiece(row, col);
            out.print(piece);
            out.print(RESET_TEXT_COLOR);
        }
    }

    private static String getPiece(int boardRow, int boardCol) {
        ChessPosition position = new ChessPosition(boardRow, boardCol);

        if (board.getPiece(position) == null) {
            return EMPTY;
        }

        ChessPiece piece = board.getPiece(position);
        String pieceString = "";

        switch (piece.getPieceType()) {
            case KING -> pieceString = KING_PIECE;
            case QUEEN -> pieceString = QUEEN_PIECE;
            case ROOK -> pieceString = ROOK_PIECE;
            case BISHOP -> pieceString = BISHOP_PIECE;
            case KNIGHT -> pieceString = KNIGHT_PIECE;
            case PAWN -> pieceString = PAWN_PIECE;
        }

        switch (piece.getTeamColor()) {
            case WHITE -> {
                out.print(SET_TEXT_COLOR_WHITE);
            }
            case BLACK -> {
                out.print(SET_TEXT_COLOR_BLACK);
            }
        }
        return pieceString;
    }
}

