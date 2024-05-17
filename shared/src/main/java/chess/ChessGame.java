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
        ChessPiece pieceToMove = board.getPiece(startPosition);
        TeamColor currentTeam = pieceToMove.getTeamColor();

        // invalid move (no piece there, or not your turn)
        if (pieceToMove == null || pieceToMove.getTeamColor() != getTeamTurn()) return null;

        // get list of all standard moves, regardless of King's safety
        Collection<ChessMove> allMoves = pieceToMove.pieceMoves(board, startPosition);

        Collection<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove move : allMoves){  // Check all possible moves -> do they put the current team in check?
            if (!isInCheck(pieceToMove.getTeamColor())){  // if not, add them to the list of valid moves
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

        // get list of valid moves for piece
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        // if the given move isn't on the list of valid moves (or the list returns null), throw exception
        if (validMoves == null || !validMoves.contains(move)) throw new InvalidMoveException();

        // move piece (copy piece to new spot, set old spot to null)
        if (move.getPromotionPiece() != null) { // promotion needed?
            ChessPiece promotedPawn = new ChessPiece(getTeamTurn(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPawn);
            board.removePiece(move.getStartPosition());
        }
        else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.removePiece(move.getStartPosition());
        }

        // next turn
        changeTurn();
    }

    private void changeTurn() {
        if (turn == TeamColor.WHITE) turn = TeamColor.BLACK;
        if (turn == TeamColor.BLACK) turn = TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.findKing(new ChessPiece(teamColor, ChessPiece.PieceType.KING));

        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece opponent = board.getPiece(position);

                // check all positions for opponents
                if (opponent != null && opponent.getTeamColor() != teamColor) {
                    // opponent found, checking all of their possible moves
                    Collection<ChessMove> moves = opponent.pieceMoves(board, position);
                    for (ChessMove move : moves){
                        if (move.getEndPosition() == kingPosition) return true;
                    }
                }
            }
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


        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {


        throw new RuntimeException("Not implemented");
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
}
