package model;

import chess.ChessGame;

public record GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {
    public GameData addUser(ChessGame.TeamColor color, UserData user) {
        if (color == ChessGame.TeamColor.BLACK) {
            return new GameData(gameID, gameName, whiteUsername, user.username(), game);
        } else {
            return new GameData(gameID, gameName, user.username(), blackUsername, game);
        }
    }
}
