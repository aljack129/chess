package model;

import chess.ChessGame;

public record JoinGameInput(int gameID, ChessGame.TeamColor color) {
}
