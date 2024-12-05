package com.example.chess.controller;

import com.example.chess.model.*;
import com.example.chess.ChessGameActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
///responsible for communications between the chess game and other parts of the framework
/// @author Hidden Village
public class ChessGameController {
    private ChessGame game;
    private final Map<Class<? extends Piece>, String> pieceUnicodeMap = new HashMap<Class<? extends Piece>, String>() {
        {
            put(Pawn.class, "\u265F");
            put(Rook.class, "\u265C");
            put(Knight.class, "\u265E");
            put(Bishop.class, "\u265D");
            put(Queen.class, "\u265B");
            put(King.class, "\u265A");
        }
    };

    // Constructor for new players
    public ChessGameController(Player player1, Player player2, Ranking ranking) {
        this.game = new ChessGame(player1, player2, ranking);
    }

    public ChessGame getGame() {
        return game;
    }

    public String getPieceUnicodeSymbol(Class<? extends Piece> pieceClass) {
        return pieceUnicodeMap.get(pieceClass);
    }

    // Main method to handle move requests
    public boolean handleMove(Position source, Position destination) {
        return game.handleMove(source, destination);
    }

    public boolean isGameOver() {
        return game.isCheckmate(game.getCurrentPlayerColor());
    }

    public boolean isInCheck() {
        return game.isInCheck(game.getCurrentPlayerColor());
    }

    public PieceColor getCurrentPlayerColor() {
        return game.getCurrentPlayerColor();
    }

    public List<Position> getLegalMoves(Position position) {
        return game.getLegalMovesForPieceAt(position);
    }
}
