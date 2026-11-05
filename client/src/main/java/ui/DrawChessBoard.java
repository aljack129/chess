package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

import chess.*;

public class DrawChessBoard {

    //Board dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_CHUNKS = 3;


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeadFoot(out, ChessGame.TeamColor.BLACK);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeadFoot(PrintStream out, ChessGame.TeamColor color) {
        String[] headers = {};
        if (color == ChessGame.TeamColor.BLACK) {
            headers = new String[]{"   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "};
        } else if (color == ChessGame.TeamColor.WHITE) {
            headers = new String[]{"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
        }

        setBlack(out);

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            out.print(EMPTY);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);

            out.print(headers[boardCol]);

            setBlack(out);

            out.print(EMPTY);
        }
        out.println();
    }

    private static void drawSideBorder(PrintStream out, int boardRow) {

    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
