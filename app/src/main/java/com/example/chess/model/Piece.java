package com.example.chess.model;
/// an abstract class that can define any piece
/// @author Hidden Village
public abstract class Piece {
    protected Position position;
    protected PieceColor color;

    public Piece(PieceColor color, Position position) {
        this.color = color;
        this.position = position;
    }

    public PieceColor getColor() { return color; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public abstract boolean isValidMove(Position newPosition, Piece[][] board);
}
