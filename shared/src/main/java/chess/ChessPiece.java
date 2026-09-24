package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString(){
        char firstChar = type.name().charAt(0);
        String represent = String.valueOf(firstChar);
        if (color == ChessGame.TeamColor.BLACK){
            represent = represent.toLowerCase();
        }
        return represent;
    }


    @Override
    public boolean equals(Object piece){
        if (this == piece) return true;
        if (piece == null || this.getClass() != piece.getClass()) return false;
        ChessPiece that = (ChessPiece) piece;
        return type.equals(that.getPieceType()) && color.equals(that.getTeamColor());
    }



    @Override
    public int hashCode(){
        return 23 * Objects.hash(type, color);
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
        findMoves(myPosition, myPosition, board, moves, null);
        return moves;
    }

    private void findMoves(ChessPosition start, ChessPosition spot, ChessBoard board, Collection<ChessMove> moves, int[] currentDir){
        //if still on board
        if(board.validSpot(spot)){
            // if starting, or on empty spot
            if(start == spot || board.getPiece(spot) == null){
                int[][] directions = {{}};
                //if the piece is sliding
                if(board.getPiece(spot) == null){
                    moves.add(new ChessMove(start, spot, null));
                }

                if(type == ChessPiece.PieceType.ROOK || type == ChessPiece.PieceType.QUEEN || type == ChessPiece.PieceType.BISHOP){

                    if(type == ChessPiece.PieceType.ROOK){
                        directions = new int[][] {{1,0}, {-1,0}, {0,1}, {0,-1}};
                    }
                    else if(type == ChessPiece.PieceType.BISHOP){
                        directions = new int[][] {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
                    }
                    else if(type == ChessPiece.PieceType.QUEEN){
                        directions = new int[][] {{1,1}, {-1,1}, {1,-1}, {-1,-1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
                    }

                    if(currentDir == null){
                        for(int[] dir : directions){
                            ChessPosition newSpot = new ChessPosition(start.getRow() + dir[0], start.getColumn() + dir[1]);
                            findMoves(start, newSpot, board, moves, dir);
                        }
                    }
                    else{
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + currentDir[0], spot.getColumn() + currentDir[1]);
                        findMoves(start, newSpot, board, moves, currentDir);
                    }

                }
                //if piece is checking only one space in given directions and is not a pawn
                else if (type == ChessPiece.PieceType.KING || type == ChessPiece.PieceType.KNIGHT){

                    if(type == ChessPiece.PieceType.KNIGHT){
                        directions = new int[][] {{1,2}, {-1,2}, {1,-2}, {-1,-2}, {2,1}, {-2,1}, {2,-1}, {-2,-1}};
                    }
                    else if(type == ChessPiece.PieceType.KING){
                        directions = new int[][] {{1,1}, {-1,1}, {1,-1}, {-1,-1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
                    }

                    for(int[] dir : directions){
                        ChessPosition newSpot = new ChessPosition(start.getRow() + dir[0], start.getColumn() + dir[1]);
                        if(board.validSpot(newSpot) && (board.getPiece(newSpot) == null || canCapture(board.getPiece(newSpot)))){
                            moves.add(new ChessMove(start, newSpot, null));
                        }
                    }
                    return;
                }
                //if piece is a pawn
                else{
                    int dir;
                    int promoLine;
                    int init;
                    directions = new int[][] {{1,1}, {1,-1}};
                    ChessPiece.PieceType[] promos = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};

                    if(color == ChessGame.TeamColor.BLACK){
                        dir = -1;
                        promoLine = 1;
                        init = 7;
                    }
                    else{
                        dir = 1;
                        promoLine = 8;
                        init = 2;
                    }

                    //moving straight forward
                    ChessPosition newSpot = new ChessPosition(spot.getRow() + 1 * dir, spot.getColumn());
                    if(board.validSpot(newSpot) && board.getPiece(newSpot) == null){
                        if(newSpot.getRow() == promoLine){
                            for (ChessPiece.PieceType p : promos){
                                moves.add(new ChessMove(start, newSpot, p));
                            }
                        }
                        else if(start.getRow() == init){
                            moves.add(new ChessMove(start, newSpot, null));
                            newSpot = new ChessPosition(spot.getRow() + 2 * dir, spot.getColumn());
                            if(board.validSpot(newSpot) && board.getPiece(newSpot) == null){
                                moves.add(new ChessMove(start, newSpot, null));
                            }
                        }
                        else {
                            moves.add(new ChessMove(start, newSpot, null));
                        }
                    }

                    //moving diagonally
                    for(int[] d : directions){
                        newSpot = new ChessPosition(spot.getRow() + d[0] * dir, spot.getColumn() + d[1]);
                        if(board.validSpot(newSpot) && board.getPiece(newSpot) != null && canCapture(board.getPiece(newSpot))){
                            if(newSpot.getRow() == promoLine){
                                for (ChessPiece.PieceType p : promos){
                                    moves.add(new ChessMove(start, newSpot, p));
                                }
                            }
                            else {
                                moves.add(new ChessMove(start, newSpot, null));
                            }
                        }
                    }
                    return;
                }
            }
            //if spot has a piece in it and is not at start
            else{
                if(canCapture(board.getPiece(spot))){
                    moves.add(new ChessMove(start, spot, null));
                }
                return;
            }
        }
        //if recursed off of board
        else{
            return;
        }
    }

    private boolean canCapture(ChessPiece o){
        if(o.getTeamColor() == color){
            return false;
        }
        else{
            return true;
        }
    }
}
