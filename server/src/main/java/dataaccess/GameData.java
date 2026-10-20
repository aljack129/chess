package dataaccess;

import chess.ChessGame;

public record GameData(int gameID, String name, UserData whiteUsername, UserData blackUsername, ChessGame game) {
    public GameData addUser(ChessGame.TeamColor color, UserData user) {
        if (color == ChessGame.TeamColor.BLACK) {
            return new GameData(gameID, name, whiteUsername, user, game);
        } else {
            return new GameData(gameID, name, user, blackUsername, game);
        }
    }
}
