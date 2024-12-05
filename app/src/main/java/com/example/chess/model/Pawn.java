package com.example.chess.model;
///pawns can move twice on their first move, once every other move, and take only diagonally
/// @author Hidden Village
public class Pawn extends Piece {
    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition == null || position == null) {
            return false; // Handle null positions appropriately
        }

        int forwardDirection = color == PieceColor.WHITE ? -1 : 1;
        int rowDiff = (newPosition.getRow() - position.getRow()) * forwardDirection;
        int colDiff = newPosition.getColumn() - position.getColumn();

        // Normal move forward by 1
        if (colDiff == 0 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] == null) {
            return true;
        }

        // Initial double move
        boolean isStartingPosition = (color == PieceColor.WHITE && position.getRow() == 6) ||
                (color == PieceColor.BLACK && position.getRow() == 1);
        if (colDiff == 0 && rowDiff == 2 && isStartingPosition &&
                board[newPosition.getRow()][newPosition.getColumn()] == null) {
            int middleRow = position.getRow() + forwardDirection;
            if (board[middleRow][position.getColumn()] == null) {
                return true;
            }
        }

        // Diagonal capture move
        if (Math.abs(colDiff) == 1 && rowDiff == 1) {
            Piece targetPiece = board[newPosition.getRow()][newPosition.getColumn()];
            if (targetPiece != null && targetPiece.getColor() != this.getColor()) {
                return true; // Valid capture move
            }
        }

        return false; // Invalid move for a pawn
    }
}
