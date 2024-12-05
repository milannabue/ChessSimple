package com.example.chess.model;
///knight will move in L shapes in any direction
/// @author Hidden Village
public class Knight extends Piece {

    public Knight(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition == null || position == null) {
            return false; // Handle null positions appropriately
        }

        int rowDiff = Math.abs(newPosition.getRow() - position.getRow());
        int colDiff = Math.abs(newPosition.getColumn() - position.getColumn());

        // Knight moves in an L-shape
        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
            if (destinationPiece != null && destinationPiece.getColor() == this.getColor()) {
                return false; // Prevent capturing friendly pieces
            }
            return true;
        }
        return false;
    }
}
