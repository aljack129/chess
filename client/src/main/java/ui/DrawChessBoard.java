package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

import chess.*;
import com.google.gson.Gson;

public class DrawChessBoard {

    //Board dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    //private static final String EMPTY = "   ";


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        ChessBoard board = new Gson().fromJson(args[1], ChessBoard.class);
        ChessGame.TeamColor color;
        if(args[2].equals("BLACK")){
            color = ChessGame.TeamColor.BLACK;
        } else{
            color = ChessGame.TeamColor.WHITE;
        }
        drawHeadFoot(out, color);
        drawRows(out, color, board);
        drawHeadFoot(out, color);


        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeadFoot(PrintStream out, ChessGame.TeamColor color) {
        String[] headers = {};
        if (color == ChessGame.TeamColor.BLACK) {
            headers = new String[]{"   ", "h", "g", "f", "e", "d", "c", "b", "a", EMPTY};
        } else if (color == ChessGame.TeamColor.WHITE) {
            headers = new String[]{"   ", "a", "b", "c", "d", "e", "f", "g", "h", EMPTY};
        }

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            out.print(EMPTY);

            out.print(headers[boardCol]);

            out.print(EMPTY);
        }
        out.println();
    }

    private static void drawRows(PrintStream out, ChessGame.TeamColor color, ChessBoard board) {
        if (color == ChessGame.TeamColor.BLACK) {
            for (int boardRow = 1; boardRow <= 8; boardRow++) {
                drawRow(out, color, boardRow, board);
            }
        } else {
            for (int boardRow = 1; boardRow <= 8; boardRow++) {
                drawRow(out, color, boardRow, board);
            }
        }
    }

    private static void drawRow(PrintStream out, ChessGame.TeamColor color, Integer boardRow, ChessBoard board) {
        String rowNum = " " + boardRow.toString() + " ";
        drawLineOfRow(out, boardRow, color, rowNum, board);
    }

    private static void drawLineOfRow(PrintStream out, int boardRow, ChessGame.TeamColor color, String rowNum, ChessBoard board) {
        setBlack(out);
        out.print(EMPTY);
        out.print(rowNum);
        out.print(EMPTY);

        ChessGame.TeamColor currColor;
        if (color == ChessGame.TeamColor.BLACK) {
            if (boardRow % 2 == 1) {
                currColor = ChessGame.TeamColor.WHITE;
            } else {
                currColor = ChessGame.TeamColor.BLACK;
            }
        } else {
            if (boardRow % 2 == 0) {
                currColor = ChessGame.TeamColor.WHITE;
            } else {
                currColor = ChessGame.TeamColor.BLACK;
            }
        }

        for (int colNum = 1; colNum <= BOARD_SIZE_IN_SQUARES - 2; colNum++) {
            if (currColor == ChessGame.TeamColor.BLACK) {
                out.print(SET_BG_COLOR_BLACK);
                out.print(SET_TEXT_COLOR_BLUE);
            } else {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_BLUE);
            }

            out.print(EMPTY);
            ChessPiece piece = board.getPiece(new ChessPosition(boardRow, colNum));
            if (rowNum != EMPTY && piece != null) {
                out.print(piece.toString());
            } else {
                out.print(EMPTY);
            }
            out.print(EMPTY);

            currColor = changeColor(currColor);
        }

        setBlack(out);
        out.print(EMPTY);
        out.print(rowNum);
        out.print(EMPTY);

        out.println();
    }

    private static ChessGame.TeamColor changeColor(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.BLACK) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return ChessGame.TeamColor.BLACK;
        }
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
}
