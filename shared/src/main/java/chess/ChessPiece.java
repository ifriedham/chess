package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
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

        switch (this.type) {
            case QUEEN, BISHOP, ROOK -> {  // all move in a straight line
                int[][] directions = getDirections(this.type);
                for (int[] direction : directions) {
                    moves.addAll(getLinearMoves(board, myPosition, direction[0], direction[1]));
                }
            }

            case KING, KNIGHT -> { // both move to a single spot
                int[][] directions = getDirections(this.type);
                for (int[] direction : directions) {
                    ChessMove newMove = getSingleMoves(board, myPosition, direction[0], direction[1]);
                    if (newMove != null) moves.add(newMove);
                }
            }

            case PAWN -> { // all sorts of crazy

            }
        }

        return moves;
    }

    private int[][] getDirections(PieceType type) {
        switch (type) {
            case KING, QUEEN -> {
                return new int[][]{
                        {1, 0},   // North
                        {1, 1},   // North East
                        {0, 1},   // East
                        {-1, 1},  // South East
                        {-1, 0},  // South
                        {-1, -1}, // South West
                        {0, -1},  // West
                        {1, -1}   // North West
                };
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
                return new int[][]{
                        {2, 1},   // NNE
                        {1, 2},   // NEE
                        {-1, 2},  // SEE
                        {-2, 1},  // SSE
                        {-2, -1}, // SSW
                        {-1, -2}, // SWW
                        {1, -2},  // NWW
                        {2, -1}   // NNW
                };

            }
            case ROOK -> {
                return new int[][]{
                        {1, 0},  // North
                        {0, 1},  // East
                        {-1, 0}, // South
                        {0, -1}, // West
                };
            }
            case PAWN -> {
            }
        }

        return new int[0][];
    }

    private boolean isOccupied (ChessBoard board, ChessPosition nextPos){
        if (board.getPiece(nextPos) != null) return true;  // spot is occupied
        else return false;  // Spot is empty
    }

    private boolean isOutOfBounds (ChessPosition position){
        if (position.getRow() <= 0
                || position.getRow() > 8
                || position.getColumn() <= 0
                || position.getColumn() > 8)
            return true;  // next position is out of bounds
        else return false;  // next position is within bounds
    }

    private Collection<ChessMove> getLinearMoves(ChessBoard board, ChessPosition myPosition, int horizontalDir, int verticalDir) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        for (int i = 1; i < 8; i++) {  // loops through all squares in a straight line (including diagonals?)
            int nextRow = myPosition.getRow() + (i * horizontalDir);
            int nextCol = myPosition.getColumn() + (i * verticalDir);
            ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

            if (isOutOfBounds(nextPos)) break; // out of bounds
            if (isOccupied(board, nextPos)) {
                if (board.getPiece(nextPos).getTeamColor() != this.getTeamColor()){ // space is occupied by enemy
                    possibleMoves.add(new ChessMove(myPosition, nextPos, null));
                    break;
                } else break;
            } else possibleMoves.add(new ChessMove(myPosition, nextPos, null));
        }
        return possibleMoves;
    }

    private ChessMove getSingleMoves(ChessBoard board, ChessPosition myPosition, int horizontalDir, int verticalDir) {
        int nextRow = myPosition.getRow() + horizontalDir;
        int nextCol = myPosition.getColumn() + verticalDir;
        ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

        if (isOutOfBounds(nextPos)) return null;
        if (isOccupied(board, nextPos)) {
            if (board.getPiece(nextPos).getTeamColor() != this.getTeamColor()){ // space is occupied by enemy
                return new ChessMove(myPosition, nextPos, null);
            } else return null;
        } else return new ChessMove(myPosition, nextPos, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
