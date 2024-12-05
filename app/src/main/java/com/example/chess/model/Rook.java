package com.example.chess.model;
///Rooks can move in straight lines in any direction for any distance
/// @author Hidden Village
public class Rook extends Piece {
    public Rook(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition == null || position == null) {
            return false; // Handle null positions appropriately
        }

        if (position.equals(newPosition)) {
            return false; // Cannot move to the same position
        }

        // Check for horizontal or vertical move
        if (position.getRow() == newPosition.getRow()) {
            int columnStart = Math.min(position.getColumn(), newPosition.getColumn()) + 1;
            int columnEnd = Math.max(position.getColumn(), newPosition.getColumn());
            for (int column = columnStart; column < columnEnd; column++) {
                if (board[position.getRow()][column] != null) {
                    return false; // Path is blocked
                }
            }
        } else if (position.getColumn() == newPosition.getColumn()) {
            int rowStart = Math.min(position.getRow(), newPosition.getRow()) + 1;
            int rowEnd = Math.max(position.getRow(), newPosition.getRow());
            for (int row = rowStart; row < rowEnd; row++) {
                if (board[row][position.getColumn()] != null) {
                    return false; // Path is blocked
                }
            }
        } else {
            return false; // Not a valid Rook move (not straight)
        }

        Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
        if (destinationPiece != null && destinationPiece.getColor() == this.getColor()) {
            return false; // Prevent capturing friendly pieces
        }

        return true; // Valid move
    }
}
