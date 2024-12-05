package com.example.chess.model;

import com.example.chess.ChessGameActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
///stores information about the current chess game, including the board, players, and rankings of those players
/// @author Hidden Village
public class ChessGame {
    private ChessBoard board;
    private boolean whiteTurn = true; // White starts the game
    private int fiftyMoveCounter = 0;
    private List<String> positionHistory = new ArrayList<>();
    private boolean drawAgreed = false;
    private Player player1;
    private Player player2;
    private Ranking ranking;

    public ChessGame(Player player1, Player player2, Ranking ranking) { // initialise new game with given players
        this.board = new ChessBoard();
        this.player1 = player1;
        this.player2 = player2;
        this.ranking = ranking;
    }


    public ChessBoard getBoard() {
        return this.board;
    }

    public void resetGame() {
        this.board = new ChessBoard();
        this.whiteTurn = true;
        this.fiftyMoveCounter = 0;
        this.positionHistory.clear();
        this.drawAgreed = false;
    }

    public PieceColor getCurrentPlayerColor() {
        return whiteTurn ? PieceColor.WHITE : PieceColor.BLACK;
    }

    public boolean isInCheck(PieceColor kingColor) {
        Position kingPosition = findKingPosition(kingColor);
        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() != kingColor) {
                    if (piece.isValidMove(kingPosition, board.getBoard())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Position findKingPosition(PieceColor color) {
        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        // Trigger game over logic when the king is not found
        handleGameOver(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);
        return null;  // Return null to indicate the king was not found
    }

    private void handleGameOver(PieceColor winningColor) {
        String winningMessage;

        if (winningColor == null) {
            // It's a draw
            winningMessage = "Draw!";
            // Debug: Print draw details
            System.out.println("Game over. Result: Draw.");
        } else if (winningColor == PieceColor.WHITE || winningColor == PieceColor.BLACK) {
            // Someone has won
            if (winningColor == PieceColor.WHITE) {
                winningMessage = "White wins.";
            } else {
                winningMessage = "Black wins.";
            }
            // Debug: Print win details
            System.out.println("Game over. Winner: " + winningColor);
        } else {
            // Unexpected scenario
            winningMessage = "Unexpected game result!";
            System.out.println("Game over. Unexpected scenario.");
        }

        ChessGameActivity.getInstance().onGameOver(winningMessage, winningColor);
    }


    public boolean isCheckmate(PieceColor kingColor) {
        if (!isInCheck(kingColor)) {
            return false;
        }

        Position kingPosition = findKingPosition(kingColor);
        King king = (King) board.getPiece(kingPosition.getRow(), kingPosition.getColumn());

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }
                Position newPosition = new Position(kingPosition.getRow() + rowOffset, kingPosition.getColumn() + colOffset);

                if (isPositionOnBoard(newPosition) && king.isValidMove(newPosition, board.getBoard())
                        && !wouldBeInCheckAfterMove(kingColor, kingPosition, newPosition)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isStalemate(PieceColor color) {
        if (isInCheck(color)) {
            System.out.println("Player is in check, not a stalemate.");
            return false;
        }

        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() == color) {
                    List<Position> legalMoves = getLegalMovesForPieceAt(new Position(row, col));
                    if (!legalMoves.isEmpty()) {
                        System.out.println("Found legal moves for piece at (" + row + ", " + col + "), not a stalemate.");
                        return false;
                    }
                }
            }
        }
        System.out.println("No legal moves available for player, stalemate.");
        return true;
    }


    private boolean wouldBeInCheckAfterMove(PieceColor kingColor, Position from, Position to) {
        Piece temp = board.getPiece(to.getRow(), to.getColumn());
        board.setPiece(to.getRow(), to.getColumn(), board.getPiece(from.getRow(), from.getColumn()));
        board.setPiece(from.getRow(), from.getColumn(), null);

        boolean inCheck = isInCheck(kingColor);

        board.setPiece(from.getRow(), from.getColumn(), board.getPiece(to.getRow(), to.getColumn()));
        board.setPiece(to.getRow(), to.getColumn(), temp);

        return inCheck;
    }

    public boolean isDraw() {
        System.out.println("Checking for draw. Conditions - drawAgreed: " + drawAgreed +
                ", Stalemate: " + isStalemate(getCurrentPlayerColor()) +
                ", Threefold Repetition: " + isThreefoldRepetition() +
                ", Fifty-Move Rule: " + isFiftyMoveRule());

        return drawAgreed || isStalemate(getCurrentPlayerColor()) || isThreefoldRepetition() || isFiftyMoveRule();
    }

    private boolean isThreefoldRepetition() {
        System.out.println("Checking for Threefold Repetition");
        Set<String> uniquePositions = new HashSet<>(positionHistory);
        for (String position : uniquePositions) {
            long count = positionHistory.stream().filter(p -> p.equals(position)).count();
            System.out.println("Position: " + position + ", Count: " + count);
            if (count >= 3) {
                System.out.println("Threefold repetition detected for position: " + position);
                return true;
            }
        }
        return false;
    }


    private boolean isFiftyMoveRule() {
        return fiftyMoveCounter >= 50;
    }

    public List<Position> getLegalMovesForPieceAt(Position position) {
        Piece selectedPiece = board.getPiece(position.getRow(), position.getColumn());
        if (selectedPiece == null)
            return new ArrayList<>();

        List<Position> legalMoves = new ArrayList<>();
        switch (selectedPiece.getClass().getSimpleName()) {
            case "Pawn":
                addPawnMoves(position, selectedPiece.getColor(), legalMoves);
                break;
            case "Rook":
                addLineMoves(position, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}, legalMoves);
                break;
            case "Knight":
                addSingleMoves(position, new int[][]{{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}}, legalMoves);
                break;
            case "Bishop":
                addLineMoves(position, new int[][]{{1, 1}, {-1, -1}, {1, -1}, {-1, 1}}, legalMoves);
                break;
            case "Queen":
                addLineMoves(position, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}}, legalMoves);
                break;
            case "King":
                addSingleMoves(position, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}}, legalMoves);
                break;
        }
        return legalMoves;
    }

    private void addLineMoves(Position position, int[][] directions, List<Position> legalMoves) {
        for (int[] d : directions) {
            Position newPos = new Position(position.getRow() + d[0], position.getColumn() + d[1]);
            while (isPositionOnBoard(newPos)) {
                if (board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
                    legalMoves.add(newPos);
                    newPos = new Position(newPos.getRow() + d[0], newPos.getColumn() + d[1]);
                } else {
                    if (board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != board
                            .getPiece(position.getRow(), position.getColumn()).getColor()) {
                        legalMoves.add(newPos);
                    }
                    break;
                }
            }
        }
    }

    private void addSingleMoves(Position position, int[][] moves, List<Position> legalMoves) {
        for (int[] move : moves) {
            Position newPos = new Position(position.getRow() + move[0], position.getColumn() + move[1]);
            if (isPositionOnBoard(newPos) && (board.getPiece(newPos.getRow(), newPos.getColumn()) == null ||
                    board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != board
                            .getPiece(position.getRow(), position.getColumn()).getColor())) {
                legalMoves.add(newPos);
            }
        }
    }

    private void addPawnMoves(Position position, PieceColor color, List<Position> legalMoves) {
        int direction = color == PieceColor.WHITE ? -1 : 1;
        Position newPos = new Position(position.getRow() + direction, position.getColumn());
        if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
            legalMoves.add(newPos);
        }

        if ((color == PieceColor.WHITE && position.getRow() == 6)
                || (color == PieceColor.BLACK && position.getRow() == 1)) {
            newPos = new Position(position.getRow() + 2 * direction, position.getColumn());
            Position intermediatePos = new Position(position.getRow() + direction, position.getColumn());
            if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null
                    && board.getPiece(intermediatePos.getRow(), intermediatePos.getColumn()) == null) {
                legalMoves.add(newPos);
            }
        }

        int[] captureCols = {position.getColumn() - 1, position.getColumn() + 1};
        for (int col : captureCols) {
            newPos = new Position(position.getRow() + direction, col);
            if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) != null &&
                    board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != color) {
                legalMoves.add(newPos);
            }
        }
    }

    public void checkGameOver() {
        // Debug: Print current game state
        System.out.println("Checking game over. Current state: " +
                "White in checkmate: " + isCheckmate(PieceColor.WHITE) +
                ", Black in checkmate: " + isCheckmate(PieceColor.BLACK) +
                ", Stalemate: " + isStalemate(getCurrentPlayerColor()) +
                ", Draw: " + isDraw());

        // Check if it's checkmate for the White player
        if (isCheckmate(PieceColor.WHITE)) {
            updateRanking(player2, 1); // Black wins: Award 3 points to Black's ranking
            updateRanking(player1, 0); // White loses: Deduct 3 points from White's ranking
            handleGameOver(PieceColor.BLACK); // Handle end-game scenario with Black as the winner
        }
        // Check if it's checkmate for the Black player
        else if (isCheckmate(PieceColor.BLACK)) {
            updateRanking(player1, 1); // White wins: Award 3 points to White's ranking
            updateRanking(player2, 0); // Black loses: Deduct 3 points from Black's ranking
            handleGameOver(PieceColor.WHITE); // Handle end-game scenario with White as the winner
        }
        // Check for stalemate or draw (e.g., insufficient material, threefold repetition)
        else if (isStalemate(getCurrentPlayerColor()) || isDraw()) {
            updateRanking(player1, 1); // Draw: Award 1 point to White's ranking
            updateRanking(player2, 1); // Draw: Award 1 point to Black's ranking
            System.out.println("Triggering draw due to stalemate or draw condition");
            handleGameOver(null); // Handle end-game scenario as a draw (no winner)
        }
    }


    private void updateRanking(Player player, int scoreChange) { // update instantiated ranking, affecting file
        player.setScore(player.getScore() + scoreChange);
        ranking.saveNewScore(player);
    }

    /// called after every move to check for validity and perform movement action///
    public boolean handleMove(Position source, Position destination) {
        Piece movingPiece = board.getPiece(source.getRow(), source.getColumn());
        if (movingPiece == null || movingPiece.getColor() != (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
            return false;
        }

        // Check if the move is valid
        if (movingPiece.isValidMove(destination, board.getBoard())) {
            // Move the piece
            board.movePiece(source, destination, false);

            // if move puts player into check, move piece back
            if (isInCheck(movingPiece.getColor())) {
                board.movePiece(destination, source, true);
                System.out.println("tried to move yourself into check");
                return false;
            }

            // Update turn
            whiteTurn = !whiteTurn;
            checkGameOver();
            return true;
        }
        return false;
    }


    public boolean isPositionOnBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < 8 &&
                position.getColumn() >= 0 && position.getColumn() < 8;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }



}
