package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;

    public ChessPosition(int rowInit, int colInit) {
        row = rowInit;
        col = colInit;
    }

    @Override
    public String toString(){
        return String.format("[%d, %d]", row, col);
    }

    @Override
    public boolean equals(Object spot){
        if (this == spot) return true;
        if (spot == null || this.getClass() != spot.getClass()) return false;
        ChessPosition that = (ChessPosition) spot;
        return row == that.getRow() && col == that.getColumn();
    }

    @Override
    public int hashCode(){
        return 23 * Objects.hash(row, col);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }
}
