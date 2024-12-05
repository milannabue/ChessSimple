package com.example.chess.model;
///king will move only one square in any direction
/// @author Hidden Village
public class King extends Piece {
    public King(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition == null || position == null) {
            return false; // Handle null positions appropriately
        }

        int rowDiff = Math.abs(position.getRow() - newPosition.getRow());
        int colDiff = Math.abs(position.getColumn() - newPosition.getColumn());

        boolean isOneSquareMove = rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0);

        if (!isOneSquareMove) {
            return false;
        }

        Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
        if (destinationPiece != null && destinationPiece.getColor() == this.getColor()) {
            return false; // Prevent capturing friendly pieces
        }

        return true; // Move is valid
    }
}
