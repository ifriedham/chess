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

        switch (type) {
            case QUEEN, BISHOP, ROOK -> {  // all move in a straight line
                int[][] directions = getDirections(type);
                for (int[] direction : directions) {
                    moves.addAll(getLinearMoves(board, myPosition, direction[0], direction[1]));
                }
            }

            case KING, KNIGHT -> { // both move to a single spot
                int[][] directions = getDirections(type);
                for (int[] direction : directions) {
                    ChessMove newMove = getSingleMove(board, myPosition, direction[0], direction[1]);
                    if (newMove != null) moves.add(newMove);
                }
            }

            case PAWN -> { // all sorts of crazy
                int[][] directions = getDirections(type);
                for (int[] direction : directions) {
                    ChessMove newMove = null;

                    // first move
                    if ((direction[1] == 0) && isFirstMove(pieceColor, myPosition)){
                        newMove = getPawnMove(board, myPosition, direction[0], direction[1]);
                        ChessMove extraMove = null;
                        if (newMove != null){
                            switch (pieceColor){
                                case WHITE -> extraMove = getPawnMove(board, myPosition, direction[0] + 1, direction[1]);
                                case BLACK -> extraMove = getPawnMove(board, myPosition, direction[0] - 1, direction[1]);
                            }
                        }
                        if (newMove != null) moves.add(newMove);
                        if (extraMove != null) moves.add(extraMove);
                    }

                    newMove = getPawnMove(board, myPosition, direction[0], direction[1]);

                    if (newMove != null){
                        if (canPromote(newMove, pieceColor)) {
                            moves.addAll(getPawnPromotions(newMove));
                        } else moves.add(newMove);
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getPawnPromotions(ChessMove newMove) {
        ArrayList<ChessMove> promotions = new ArrayList<>();
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.QUEEN));
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.ROOK));
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.KNIGHT));
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.BISHOP));
        return promotions;
    }

    private boolean canPromote(ChessMove newMove, ChessGame.TeamColor pieceColor) {
        switch(pieceColor){
            case WHITE -> {
                if (newMove.getEndPosition().getRow() == 8) return true;
            }
            case BLACK -> {
                if (newMove.getEndPosition().getRow() == 1) return true;
            }
        }
        return false;
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
                switch (this.getTeamColor()){
                    case WHITE -> {
                        return new int [][]{
                                {1, -1}, // North West
                                {1, 0},  // North
                                {1, 1},  // North East
                        };
                    }
                    case BLACK -> {
                        return new int [][]{
                                {-1, -1},  // South West
                                {-1, 0},   // South
                                {-1, 1},   // South East
                        };
                    }
                }
            }
        }

        return new int[0][];
    }

    private boolean isOccupied (ChessBoard board, ChessPosition nextPos){
        return board.getPiece(nextPos) != null;
    }

    private boolean isOutOfBounds (ChessPosition position){
        return position.getRow() <= 0
                || position.getRow() > 8
                || position.getColumn() <= 0
                || position.getColumn() > 8;
    }

    private Collection<ChessMove> getLinearMoves(ChessBoard board, ChessPosition myPosition, int verticalDir, int horizontalDir) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        for (int i = 1; i < 8; i++) {  // loops through all squares in a straight line (including diagonals?)
            int nextRow = myPosition.getRow() + (i * verticalDir);
            int nextCol = myPosition.getColumn() + (i * horizontalDir);
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

    private ChessMove getSingleMove(ChessBoard board, ChessPosition myPosition, int verticalDir, int horizontalDir) {
        int nextRow = myPosition.getRow() + verticalDir;
        int nextCol = myPosition.getColumn() + horizontalDir;
        ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

        if (isOutOfBounds(nextPos)) return null;
        if (isOccupied(board, nextPos)) {
            if (board.getPiece(nextPos).getTeamColor() != this.getTeamColor()){ // space is occupied by enemy
                return new ChessMove(myPosition, nextPos, null);
            } else return null;
        } else return new ChessMove(myPosition, nextPos, null);
    }

    private ChessMove getPawnMove(ChessBoard board, ChessPosition myPosition, int verticalDir, int horizontalDir) {
        int nextRow = myPosition.getRow() + verticalDir;
        int nextCol = myPosition.getColumn() + horizontalDir;
        ChessPosition nextPos = new ChessPosition(nextRow, nextCol);
        boolean occupied = isOccupied(board, nextPos);

        if (isOutOfBounds(nextPos)) return null;

        // diagonal move
        if (horizontalDir != 0){
            if (occupied) return new ChessMove(myPosition, nextPos, null);
        }

        // straight forward
        if (horizontalDir == 0) {
            if (!occupied) return new ChessMove(myPosition, nextPos, null);
        }

        return null;
    }

    private boolean isFirstMove(ChessGame.TeamColor teamColor, ChessPosition myPosition) {
        switch (teamColor){
            case WHITE -> {
                if (myPosition.getRow() == 2)return true;
            }
            case BLACK -> {
                if (myPosition.getRow() == 7)return true;
            }
        }
        return false;
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
