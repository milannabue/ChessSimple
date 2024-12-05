package com.example.chess.model;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/// ranking is responsible for file reading/writing of the leaderboard and keeps track/updates of player scores
/// @author Hidden Village
public class Ranking {
    private static final String CSV_FILE_NAME = "rankings.csv";
    private Set<Player> players;
    private Context context;

    public Ranking(Context context) {
        this.context = context;
        players = new HashSet<>();
        loadRankings();
    }

    public void addOrUpdatePlayer(Player player) {
        for (Player plr : players) {
            if (plr.getName().compareTo(player.getName()) == 0) {
                player.setScore(plr.getScore()); // retrieve the saved player's score and update the new player's score, if there exist a saved player with that name
                saveRankings();
                return;
            }
        }

        players.add(player); // Add the updated/new player
        saveRankings();
    }

    public void saveNewScore(Player player) {
        for (Player plr : players) {
            if (plr.getName().compareTo(player.getName()) == 0) {
                plr.setScore(player.getScore());
                saveRankings();
                return;
            }
        }

        System.out.println("ERR locating player, score did not save: " + player.getName());
    }

    public Set<Player> getPlayers() {
        return players;
    }

    private void saveRankings() {
        File file = new File(context.getFilesDir(), CSV_FILE_NAME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Player player : players) {
                writer.write(player.getName() + "," + player.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRankings() {
        File file = new File(context.getFilesDir(), CSV_FILE_NAME);
        if (!file.exists()) return; // Skip loading if file doesn't exist
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    Player player = new Player(name);
                    player.setScore(score);
                    players.add(player);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearRankings() {
        File file = new File(context.getFilesDir(), CSV_FILE_NAME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("");// doing nothing to clear file contents
        } catch (IOException e) {
            e.printStackTrace();
        }
        players.clear();
    }

    public List<Player> getTopPlayers(int limit) {
        return players.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
