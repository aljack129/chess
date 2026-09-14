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

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType pieceType) {
        color = pieceColor;
        type = pieceType;
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
        return color;
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
        Collection<ChessMove> moves = new ArrayList();
        checkDirections(myPosition, myPosition, board, moves);
        return moves;
    }

    private final int[][] bishop_directions = {{-1,1}, {-1,-1}, {1,1}, {1,-1}};
    private final int[][] rook_directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
    private final int[][] queen_king_directions = {{-1,1}, {-1,-1}, {1,1}, {1,-1}, {0,1}, {0,-1}, {1,0}, {-1,0}};
    private final int[][] knight_directions = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};

    private boolean canCapture(ChessPiece piece){
        if (piece.getTeamColor() == color){
            return false;
        }
        else{
            return true;
        }
    }

    private void checkDirections(ChessPosition start, ChessPosition spot, ChessBoard board, Collection<ChessMove> moves) {
        if (board.validSpot(spot)) {
            int [][] directions = {{}};
            if(spot == start || board.getPiece(spot) == null){
                moves.add(new ChessMove(start, spot, type));

                if (type == ChessPiece.PieceType.BISHOP) {
                    directions = bishop_directions;
                }
                else if(type == ChessPiece.PieceType.ROOK){
                    directions = rook_directions;
                }
                else if(type == ChessPiece.PieceType.QUEEN){
                    directions = queen_king_directions;
                }
                else if (type == ChessPiece.PieceType.KING){
                    directions = queen_king_directions;
                    for (int[] dir : directions){
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                        if (board.getPiece(newSpot) != null) {
                            if (canCapture(board.getPiece(newSpot))){
                                moves.add(new ChessMove(start, newSpot, type));
                            }
                        }
                        else{
                            moves.add(new ChessMove(start, newSpot, type));
                        }
                    }
                    return;
                }
                else if(type == ChessPiece.PieceType.KNIGHT){
                    directions = knight_directions;
                    for (int[] dir : directions){
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                        if (board.getPiece(newSpot) != null) {
                            if (canCapture(board.getPiece(newSpot))){
                                moves.add(new ChessMove(start, newSpot, type));
                            }
                        }
                        else{
                            moves.add(new ChessMove(start, newSpot, type));
                        }
                    }
                    return;
                }
                else if(type == ChessPiece.PieceType.PAWN){
                    if(color == ChessGame.TeamColor.BLACK){
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + 0, spot.getColumn() -1);
                        if(board.getPiece(newSpot) == null){
                            moves.add(new ChessMove(start, newSpot, type));
                        }
                        int[][] pawn_directions = {{-1,-1}, {1,-1}};
                        for (int[] dir : pawn_directions){
                            newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                            if (board.getPiece(newSpot) != null) {
                                if (canCapture(board.getPiece(newSpot))){
                                    moves.add(new ChessMove(start, newSpot, type));
                                }
                            }
                        }
                        return;
                    }
                    else{
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + 0, spot.getColumn() + 1);
                        if(board.getPiece(newSpot) == null){
                            moves.add(new ChessMove(start, newSpot, type));
                        }
                        int[][] pawn_directions = {{-1,1}, {1,1}};
                        for (int[] dir : pawn_directions){
                            newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                            if (board.getPiece(newSpot) != null) {
                                if (canCapture(board.getPiece(newSpot))){
                                    moves.add(new ChessMove(start, newSpot, type));
                                }
                            }
                        }
                        return;
                    }
                }

                for (int[] dir : directions){
                    ChessPosition newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                    checkDirections(start, newSpot, board, moves);
                }
                return;
            }
            else if (board.getPiece(spot) != null) {
                if (canCapture(board.getPiece(spot))){
                    moves.add(new ChessMove(start, spot, type));
                }
            }

        }
        else{
            return;
        }
    }

}
