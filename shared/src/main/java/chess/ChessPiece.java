package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = getDirections(this.type);
        int i = 0;

        switch (this.type) {
            case KING -> {

            }

            case QUEEN -> {

            }

            case BISHOP -> {
                for (int[] direction : directions) {
                    moves.addAll(getLinearMoves(board, myPosition, direction[0], direction[1]));
                }
            }

            case KNIGHT -> {

            }

            case ROOK -> {

            }

            case PAWN -> {

            }
        }

        return moves;
    }

    private Collection<ChessMove> getLinearMoves(ChessBoard board, ChessPosition myPosition, int horizontalDir, int verticalDir) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        for (int i = 1; i < 8; i++) {  // loops through all squares in a straight line (including diagonals?)
            int nextRow = myPosition.getRow() + (i * horizontalDir);
            int nextCol = myPosition.getColumn() + (i * verticalDir);

            if (nextRow <= 0 || nextRow > 8 || nextCol <= 0 || nextCol > 8) break;  // next pos. invalid

            ChessPiece nextSpot = board.getPiece(new ChessPosition(nextRow, nextCol));
            if (nextSpot == null) {  // spot is empty, add to list
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
            } else { // spot is occupied by an...
                // ally
                if (nextSpot.getTeamColor() == this.getTeamColor()) break;

                    // enemy
                else {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                    break;
                }
            }
        }

        return possibleMoves;
    }

    private int[][] getDirections(PieceType type) {
        switch (type) {
            case KING -> {
            }
            case QUEEN -> {
            }
            case BISHOP -> {
                return new int[][]{
                        {1, 1},   // North East
                        {-1, 1},  // South East
                        {-1, -1}, // South West
                        {1, -1}   // North West
                };
            }
            case KNIGHT -> {
            }
            case ROOK -> {
            }
            case PAWN -> {
            }
        }

        return new int[0][];
    }
}
