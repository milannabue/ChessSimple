package com.example.chess.model;
///the bishop can move diagonal in any direction for any distance
/// @author Hidden Village
public class Bishop extends Piece {

    public Bishop(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition == null || position == null) {
            return false; // Handle null positions appropriately
        }

        int rowDiff = Math.abs(newPosition.getRow() - position.getRow());
        int colDiff = Math.abs(newPosition.getColumn() - position.getColumn());
        if (rowDiff == colDiff) { // Must move diagonally
            int rowStep = (newPosition.getRow() > position.getRow()) ? 1 : -1;
            int colStep = (newPosition.getColumn() > position.getColumn()) ? 1 : -1;

            for (int i = 1; i < rowDiff; i++) {
                Piece pieceInPath = board[position.getRow() + i * rowStep][position.getColumn() + i * colStep];
                if (pieceInPath != null) {
                    return false; // Path is blocked
                }
            }

            Piece targetPiece = board[newPosition.getRow()][newPosition.getColumn()];
            if (targetPiece != null && targetPiece.getColor() == this.getColor()) {
                return false; // Prevent capturing friendly pieces
            }

            return true;
        }
        return false; // Invalid move for a bishop
    }
}
