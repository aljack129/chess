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
        checkDirections(myPosition, myPosition, board, moves, null);
        return moves;
    }

    private final int[][] bishop_directions = {{-1,1}, {-1,-1}, {1,1}, {1,-1}};
    private final int[][] rook_directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
    private final int[][] queen_king_directions = {{-1,1}, {-1,-1}, {1,1}, {1,-1}, {0,1}, {0,-1}, {1,0}, {-1,0}};
    private final int[][] knight_directions = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};

    // Takes in a ChessPiece object. @return boolean if the piece inputted can be captured by this current piece
    private boolean canCapture(ChessPiece piece){
        if (piece == null || piece.getTeamColor() == color){
            return false;
        }
        else{
            return true;
        }
    }

    /*
       recursive helper function to help the pieceMoves function
       Input the start, the current spot we're looking at, the board we're on, and the lists of moves
       returns nothing, modifies list of moves as needed.
     */
    private void checkDirections(ChessPosition start, ChessPosition spot, ChessBoard board, Collection<ChessMove> moves, int[] currentDir) {
        //if we're at a valid spot on the board
        if (board.validSpot(spot)) {
            int [][] directions = {{}};
            //If we're at the start, or we're starting our recursion on an empty space
            if(spot == start || board.getPiece(spot) == null){
                //if we recursively are on a space that is empty, add it to our list of moves
                if (board.getPiece(spot) == null) {
                    moves.add(new ChessMove(start, spot, null));
                }

                //if it's one of the recursive ones, set directions
                if (type == ChessPiece.PieceType.BISHOP) {
                    directions = bishop_directions;
                }
                else if(type == ChessPiece.PieceType.ROOK){
                    directions = rook_directions;
                }
                else if(type == ChessPiece.PieceType.QUEEN){
                    directions = queen_king_directions;
                }

                //king movements
                else if (type == ChessPiece.PieceType.KING){
                    directions = queen_king_directions;
                    for (int[] dir : directions){
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                        if (board.validSpot(newSpot) && board.getPiece(newSpot) != null) {
                            if (canCapture(board.getPiece(newSpot))){
                                moves.add(new ChessMove(start, newSpot, null));
                            }
                        }
                        else if (board.validSpot(newSpot)){
                            moves.add(new ChessMove(start, newSpot, null));
                        }
                    }
                    return;
                }

                //knight movements
                else if(type == ChessPiece.PieceType.KNIGHT){
                    directions = knight_directions;
                    for (int[] dir : directions){
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                        if (board.validSpot(newSpot) && board.getPiece(newSpot) != null) {
                            if (canCapture(board.getPiece(newSpot))){
                                moves.add(new ChessMove(start, newSpot, null));
                            }
                        }
                        else if (board.validSpot(newSpot)){
                            moves.add(new ChessMove(start, newSpot, null));
                        }
                    }
                    return;
                }

                //Pawn movements
                else if(type == ChessPiece.PieceType.PAWN){
                    PieceType[] promotionPieces = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP};
                    //if the pawn is black
                    if(color == ChessGame.TeamColor.BLACK){
                        //Check in front
                        ChessPosition newSpot = new ChessPosition(spot.getRow() - 1, spot.getColumn());
                        if(board.validSpot(newSpot) && board.getPiece(newSpot) == null){
                            //check for promotion
                            if (newSpot.getRow() == 1){
                                for (ChessPiece.PieceType t : promotionPieces){
                                    moves.add(new ChessMove(start, newSpot, t));
                                }
                            }
                            //if first move, it can move two forward if it's clear
                            else if (spot.getRow() == 7){
                                moves.add(new ChessMove(start, newSpot, null));
                                ChessPosition firstMove = new ChessPosition(spot.getRow() - 2, spot.getColumn());
                                if(board.getPiece(firstMove) == null) {
                                    moves.add(new ChessMove(start, firstMove, null));
                                }
                            }
                            else{
                                moves.add(new ChessMove(start, newSpot, null));
                            }
                        }

                        //check to see if we can capture diagonally
                        int[][] pawn_directions = {{-1,-1}, {-1,1}};
                        for (int[] dir : pawn_directions){
                            newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                            if (board.validSpot(newSpot) && board.getPiece(newSpot) != null && canCapture(board.getPiece(newSpot))) {
                                //check for promotion
                                if (newSpot.getRow() == 1){
                                    for (ChessPiece.PieceType t : promotionPieces){
                                        moves.add(new ChessMove(start, newSpot, t));
                                    }
                                }
                                else{
                                    moves.add(new ChessMove(start, newSpot, null));
                                }

                            }
                        }
                        return;
                    }
                    //if the pawn is white
                    else{
                        //check in front
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + 1, spot.getColumn());
                        if(board.validSpot(newSpot) && board.getPiece(newSpot) == null){
                            //check for promotion
                            if (newSpot.getRow() == 8){
                                for (ChessPiece.PieceType t : promotionPieces){
                                    moves.add(new ChessMove(start, newSpot, t));
                                }
                            }
                            //if it's its first turn, it can move two
                            else if (spot.getRow() == 2) {
                                moves.add(new ChessMove(start, newSpot, null));
                                ChessPosition firstMove = new ChessPosition(spot.getRow() + 2, spot.getColumn());
                                if (board.validSpot(firstMove) && board.getPiece(firstMove) == null) {
                                    moves.add(new ChessMove(start, firstMove, null));
                                }
                            }
                            else{
                                moves.add(new ChessMove(start, newSpot, null));
                            }
                        }
                        //check diagonally, see if it can capture
                        int[][] pawn_directions = {{1,-1}, {1,1}};
                        for (int[] dir : pawn_directions){
                            newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                            if (board.validSpot(newSpot) && board.getPiece(newSpot) != null && canCapture(board.getPiece(newSpot))) {
                                //Check for promotion
                                if (newSpot.getRow() == 8){
                                    for (ChessPiece.PieceType t : promotionPieces){
                                        moves.add(new ChessMove(start, newSpot, t));
                                    }
                                }
                                else{
                                    moves.add(new ChessMove(start, newSpot, null));
                                }

                            }
                        }
                        return;
                    }
                }

                //for the recursive ones
                //if we are starting at start, look in all the directions
                if (currentDir == null ) {
                    for (int[] dir : directions) {
                        ChessPosition newSpot = new ChessPosition(spot.getRow() + dir[0], spot.getColumn() + dir[1]);
                        checkDirections(start, newSpot, board, moves, dir);
                    }
                    return;
                }
                //if we've already started moving, keep going in that direction, don't turn
                else{
                    ChessPosition newSpot = new ChessPosition(spot.getRow() + currentDir[0], spot.getColumn() + currentDir[1]);
                    checkDirections(start, newSpot, board, moves, currentDir);
                }
            }
            //If we've recursed to a spot with a piece on it, check if we can capture, and add it to the list if we can
            else if (board.getPiece(spot) != null && canCapture(board.getPiece(spot))) {
                moves.add(new ChessMove(start, spot, null));
            }
            return;

        }
        //if we're not at a valid spot on the board
        else{
            return;
        }
    }

}
