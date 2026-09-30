package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = ChessGame.TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
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
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.validSpot(startPosition)){
            return board.getPiece(startPosition).pieceMoves(board, startPosition);
        }
        else{
            return new ArrayList();
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(!board.validSpot(move.getStartPosition()) || !board.validSpot(move.getEndPosition())){
            throw new InvalidMoveException("Not a valid move.");
        }
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if(valid.contains(move)){
            board.movePiece(move);
        }
        else{
            throw new InvalidMoveException("Not a valid move.");
        }

    }

    private ChessPosition findKing(TeamColor teamColor){
        ChessPosition kingSpot = new ChessPosition(1,1);
        boolean found = false;
        for(int i = 1; i <= 8; i++){
            if(!found) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition spot = new ChessPosition(i, j);
                    if (board.getPiece(spot).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(spot).getTeamColor() == teamColor) {
                        kingSpot = spot;
                        found = true;
                        break;
                    }
                }
            }
            else{
                break;
            }
        }

        return kingSpot;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpot = findKing(teamColor);

        ChessPiece omni = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
        Collection<ChessMove> omnimoves = omni.pieceMoves(board, kingSpot);
        for (ChessMove move : omnimoves){
            ChessPiece here = board.getPiece(move.getEndPosition());
            if(here != null){
                ChessPiece.PieceType[] types = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP};
                for(ChessPiece.PieceType t : types){
                    if(here.getPieceType() == t){
                        return true;
                    }
                }
            }
        }

        ChessPiece knight = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        Collection<ChessMove> knightmoves = knight.pieceMoves(board,kingSpot);
        for (ChessMove move : knightmoves){
            ChessPiece here = board.getPiece(move.getEndPosition());
            if(here != null){
                if(here.getPieceType() == ChessPiece.PieceType.KNIGHT){
                    return true;
                }
            }
        }
        int direction = 0;
        if(teamColor == ChessGame.TeamColor.BLACK){
            direction = -1;
        }
        else{
            direction = 1;
        }


        ChessPosition pawn1 = new ChessPosition(kingSpot.getRow() + direction, kingSpot.getColumn() - 1);
        ChessPosition pawn2 = new ChessPosition(kingSpot.getRow() + direction, kingSpot.getColumn() + 1);
        if(
                board.validSpot(pawn1) && board.getPiece(pawn1).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(pawn1).getTeamColor() != teamColor ||
                board.validSpot(pawn2) && board.getPiece(pawn2).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(pawn2).getTeamColor() != teamColor
        ){
            return true;
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
        if(!isInCheck(teamColor)){
            return false;
        }

        ChessPosition kingSpot = findKing(teamColor);
        Collection<ChessMove> kingMoves = board.getPiece(kingSpot).pieceMoves(board, kingSpot);

        ChessGame testGame = new ChessGame();
        testGame.setBoard(board);
        for(ChessMove move : kingMoves){
            try {
                testGame.makeMove(move);
                if(!testGame.isInCheck(teamColor)){
                    return false;
                }
                testGame.makeMove(new ChessMove(move.getEndPosition(), move.getStartPosition(), null));
            }
            catch(InvalidMoveException e){}
        }

        Collection<ChessPosition> myTeam = new ArrayList<ChessPosition>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition spot = new ChessPosition(i,j);
                if(board.validSpot(spot) && board.getPiece(spot) != null && board.getPiece(spot).getTeamColor() == teamColor){
                    myTeam.add(spot);
                }
            }
        }

        for(ChessPosition spot: myTeam){
            Collection<ChessMove> moves = board.getPiece(spot).pieceMoves(board, spot);
            for(ChessMove move : moves){
                try {
                    testGame.makeMove(move);
                    if(!testGame.isInCheck(teamColor)){
                        return false;
                    }
                    testGame.makeMove(new ChessMove(move.getEndPosition(), move.getStartPosition(), null));
                }
                catch(InvalidMoveException e){}
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param newBoard the new board to use
     */
    public void setBoard(ChessBoard newBoard) {
        board = newBoard;
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
