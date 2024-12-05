package com.example.chess.model;
///Queens can move in straight or diagonal lines in any direction for any distance
/// @author Hidden Village
public class Queen extends Piece {
    public Queen(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition == null || position == null) {
            return false; // Handle null positions appropriately
        }

        if (newPosition.equals(this.position)) {
            return false; // Cannot move to the same position
        }

        int rowDiff = Math.abs(newPosition.getRow() - this.position.getRow());
        int colDiff = Math.abs(newPosition.getColumn() - this.position.getColumn());

        boolean straightLine = this.position.getRow() == newPosition.getRow() || this.position.getColumn() == newPosition.getColumn();
        boolean diagonal = rowDiff == colDiff;

        if (!straightLine && !diagonal) {
            return false; // Move must be in a straight line or diagonal
        }

        int rowDirection = Integer.compare(newPosition.getRow(), this.position.getRow());
        int colDirection = Integer.compare(newPosition.getColumn(), this.position.getColumn());

        int currentRow = this.position.getRow() + rowDirection;
        int currentCol = this.position.getColumn() + colDirection;
        while (currentRow != newPosition.getRow() || currentCol != newPosition.getColumn()) {
            if (board[currentRow][currentCol] != null) {
                return false; // Path is blocked
            }
            currentRow += rowDirection;
            currentCol += colDirection;
        }

        Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
        if (destinationPiece != null && destinationPiece.getColor() == this.getColor()) {
            return false; // Prevent capturing friendly pieces
        }

        return true; // Move is valid
    }
}
