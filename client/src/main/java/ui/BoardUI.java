package ui;

import java.io.PrintStream;

import chess.*;
import static ui.EscapeSequences.*;

public class BoardUI {
    private static ChessBoard board = new ChessBoard();

    private static final int DRAWING_SIZE_IN_SQUARES = 10;
    private static final int BOARD_SIZE_IN_SQUARES = 8;


    public BoardUI() {
    }


    public static void printBoard(PrintStream out) {
        // TEMP
        board.resetBoard();
        // END TEMP


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
        String[] headers = new String[0];
        String headerLine = "";
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        // create header text
        switch (team) {
            case "WHITE":
                headerLine = "    A   B  C   D   E  F   G   H    ";

                //headers = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
                break;

            case "BLACK":
                headerLine = "    H   G  F   E   D  C   B   A    ";

                //headers = new String[] {"H", "G", "F", "E", "D", "C", "B", "A"};
                break;
        }

        out.print(headerLine);

        /*for (int col = 0; col < DRAWING_SIZE_IN_SQUARES; col++) {
            // print corner squares
            if (col == 0 || col == DRAWING_SIZE_IN_SQUARES - 1) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(EMPTY);
            }
            // print header row
            else {
                int paddingSize = (EMPTY.length() - 1) / 2; // Subtract 1 for the letter itself
                String headerSquare = String.format("%" + paddingSize + "s" + headers[col - 1] + "%" + paddingSize + "s", "", "");
                out.print(headerSquare);
            }
        }*/

        out.print(RESET_TEXT_COLOR);
        out.println(RESET_BG_COLOR);
    }

    private static void drawRows(PrintStream out, String team) {
        switch (team) {
            case "WHITE":

                break;

            case "BLACK":
                for (int row = 1; row <= BOARD_SIZE_IN_SQUARES; row++) {
                    for (int col = 0; col < DRAWING_SIZE_IN_SQUARES; col++) {
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
                                    // TODO: print board with both perspectives
                                    out.print(SET_BG_COLOR_LIGHT_PURPLE);
                                    //out.print(SET_TEXT_COLOR_BLACK);
                                }
                                else {
                                    out.print(SET_BG_COLOR_DARK_PURPLE);
                                    //out.print(SET_TEXT_COLOR_WHITE);
                                }
                            }
                            else {
                                if (col % 2 == 0) {
                                    out.print(SET_BG_COLOR_DARK_PURPLE);
                                    //out.print(SET_TEXT_COLOR_WHITE);
                                }
                                else {
                                    out.print(SET_BG_COLOR_LIGHT_PURPLE);
                                    //out.print(SET_TEXT_COLOR_BLACK);
                                }
                            }
                            String piece = getPiece(out, row, col);
                            out.print(piece);
                            out.print(RESET_TEXT_COLOR);
                        }
                    }
                    out.println(RESET_BG_COLOR);
                }
                break;
        }





    }

    private static String getPiece(PrintStream out, int boardRow, int boardCol) {
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

