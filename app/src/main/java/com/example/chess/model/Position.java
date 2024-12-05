package com.example.chess.model;
///holds the position of a piece in terms of the 2D array that is the board grid
/// @author Hidden Village
public class Position {
    private int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
}
