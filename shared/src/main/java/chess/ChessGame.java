package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
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
        if(board.validSpot(startPosition) && board.getPiece(startPosition) != null){
            Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> valids = new ArrayList();

            ChessGame testGame = new ChessGame();

            for (ChessMove move : moves) {
                ChessBoard testBoard = new ChessBoard(board);
                testGame.setBoard(testBoard);
                testBoard.movePiece(move);
                if (!testGame.isInCheck(board.getPiece(startPosition).getTeamColor())) {
                    valids.add(move);
                }
            }
            return valids;

        }
        else{
            return null;
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
        else if(board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("No piece there.");
        }
        else if(board.getPiece(move.getStartPosition()).getTeamColor() != turn){
            throw new InvalidMoveException("Not this color's turn");
        }
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if(valid.contains(move)){
            if(isInCheck(board.getPiece(move.getStartPosition()).getTeamColor())){
                ChessGame testGame = new ChessGame();
                testGame.setBoard(new ChessBoard(board));
                testGame.makeMove(move);
                if(testGame.isInCheck(board.getPiece(move.getStartPosition()).getTeamColor())){
                    throw new InvalidMoveException("Does not take you out of check");
                }
                else{
                    board.movePiece(move);
                    changeTurn();
                }
            }
            else{
                board.movePiece(move);
                changeTurn();
            }
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
                    if (board.getPiece(spot) != null &&
                            board.getPiece(spot).getPieceType() == ChessPiece.PieceType.KING &&
                            board.getPiece(spot).getTeamColor() == teamColor) {
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

        Collection<ChessMove> kingmoves = board.getPiece(kingSpot).pieceMoves(board, kingSpot);
        for (ChessMove move : kingmoves){
            ChessPiece here = board.getPiece(move.getEndPosition());
            if(here != null){
                if(here.getPieceType() == ChessPiece.PieceType.KING){
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
                board.validSpot(pawn1) &&
                        board.getPiece(pawn1) != null &&
                        board.getPiece(pawn1).getPieceType() == ChessPiece.PieceType.PAWN &&
                        board.getPiece(pawn1).getTeamColor() != teamColor ||
                board.validSpot(pawn2) &&
                        board.getPiece(pawn2) != null &&
                        board.getPiece(pawn2).getPieceType() == ChessPiece.PieceType.PAWN &&
                        board.getPiece(pawn2).getTeamColor() != teamColor
        ){
            return true;
        }

        return false;
    }

    private Collection<ChessPosition> findTeam(TeamColor teamColor){
        Collection<ChessPosition> myTeam = new ArrayList<ChessPosition>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition spot = new ChessPosition(i,j);
                if(board.validSpot(spot) && board.getPiece(spot) != null && board.getPiece(spot).getTeamColor() == teamColor){
                    myTeam.add(spot);
                }
            }
        }
        return myTeam;
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

        Collection<ChessPosition> myTeam = findTeam(teamColor);

        for(ChessPosition spot: myTeam){
            Collection<ChessMove> moves = validMoves(spot);
            if(moves != null && moves.size() != 0){
                return false;
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
        if(isInCheck(teamColor)){
            return false;
        }

        Collection<ChessPosition> myTeam = findTeam(teamColor);

        for(ChessPosition spot: myTeam){
            Collection<ChessMove> moves = validMoves(spot);
            if(moves != null && moves.size() != 0){
                return false;
            }
        }

        return true;
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

    private void changeTurn(){
        if (turn == ChessGame.TeamColor.BLACK){
            turn = ChessGame.TeamColor.WHITE;
        }
        else{
            turn = ChessGame.TeamColor.BLACK;
        }
    }
}
