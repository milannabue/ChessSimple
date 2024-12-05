package com.example.chess.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
///keeps track of players created temporarily with a Players set
/// @author Hidden Village
public class Leaderboard {
    private Set<Player> players;

    public Leaderboard() {
        this.players = new HashSet<>();
    }

    public synchronized boolean checkLeaderboard(String name) {
        return players.stream().anyMatch(player -> player.getName().equals(name));
    }

    public synchronized void addPlayer(String name) {
        if (getPlayerByName(name) != null) {
            players.add(new Player(name));
        }
    }

    public synchronized void addOrUpdatePlayer(Player player) {
        players.remove(player); // Remove if exists (to update)
        players.add(player); // Add the updated/new player
    }

    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
