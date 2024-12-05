package com.example.chess.model;
/// each player in a game will have a player class instantiated for them. This is kept track of by Leaderboard
/// @author Hidden Village
public class Player {
    private String name;
    private PieceColor color;
    private int score; // Added score attribute

    public Player(String name) {
        this.name = name;
        this.score = 0; // Initialize score to 0
    }

    public String getName() {
        return name;
    }

    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return name.equals(player.name);
    }
}
