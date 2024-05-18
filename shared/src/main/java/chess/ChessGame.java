package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceToMove = getBoard().getPiece(startPosition);

        // invalid move (no piece there)
        if (pieceToMove == null) return null;

        // get list of all standard moves, regardless of King's safety
        Collection<ChessMove> allMoves = pieceToMove.pieceMoves(getBoard(), startPosition);

        // filter all moves into all legal moves
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            if (!simulateMove(move)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());

        if (piece == null) throw new InvalidMoveException("NO PIECE TO MOVE?!");

        if (piece.getTeamColor() != getTeamTurn()) throw new InvalidMoveException("IT IS NOT THEIR TURN");

        // get list of valid moves for piece
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        // if the given move isn't on the list of valid moves (or the list returns null), throw exception
        if (legalMoves == null || !legalMoves.contains(move)) throw new InvalidMoveException("INVALID MOVE");

        // simulate moves to see if they put the king in check
        if (simulateMove(move)) throw new InvalidMoveException("MOVE LEAVES YOUR KING IN CHECK");

        // simulate moves, check for checkmate
        if (isInCheckmate(turn)) throw new InvalidMoveException("YOU ARE IN CHECKMATE");

        // check if the move results in a stalemate
        if (isInStalemate(turn)) throw new InvalidMoveException("STALEMATE");


        // no issues found, make the move
        movePiece(getBoard(), move);

        // next turn
        setTeamTurn(oppositeColor(getTeamTurn()));
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, getBoard());
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        Collection<ChessMove> moves = getAllMoves(oppositeColor(teamColor), board);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition(teamColor, board))) return true;
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> moves = getAllMoves(teamColor, getBoard());
        for (ChessMove move : moves) {
            if (!simulateMove(move)) return false;
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessMove> allMoves = getAllMoves(teamColor, getBoard());
        ChessBoard simBoard = cloneBoard(getBoard());

        for (ChessMove move : allMoves) {
            movePiece(simBoard, move);

            if (!isInCheck(teamColor, simBoard)) {
                return false;
            }

            // Reset the simulated board to its original state
            simBoard = cloneBoard(getBoard());
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Collection<ChessMove> getAllMoves(TeamColor teamColor, ChessBoard board) {
        Collection<ChessMove> allMoves = new ArrayList<>();

        // iterate over whole board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // if piece's color matches teamColor, add move to list
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    allMoves.addAll(moves);
                }
            }
        }
        return allMoves;
    }

    private ChessBoard cloneBoard(ChessBoard board) {
        ChessBoard clonedBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    clonedBoard.addPiece(position, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return clonedBoard;
    }

    private void movePiece(ChessBoard board, ChessMove move) {
        // move piece (copy piece to new spot, set old spot to null)
        if (move.getPromotionPiece() != null) { // promotion needed?
            TeamColor pawnColor = board.getPiece(move.getStartPosition()).getTeamColor();
            ChessPiece promotedPawn = new ChessPiece(pawnColor, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPawn);
            board.removePiece(move.getStartPosition());
        } else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.removePiece(move.getStartPosition());
        }
    }

    private boolean simulateMove(ChessMove move) {
        ChessBoard simBoard = cloneBoard(getBoard());
        TeamColor pieceColor = simBoard.getPiece(move.getStartPosition()).getTeamColor();

        movePiece(simBoard, move);

        return isInCheck(pieceColor, simBoard);
    }

    private TeamColor oppositeColor(TeamColor color) {
        if (color == TeamColor.WHITE) return TeamColor.BLACK;
        else if (color == TeamColor.BLACK) return TeamColor.WHITE;
        else return null;
    }

    public ChessPosition kingPosition(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }

        // piece not found
        return null;
    }

}
