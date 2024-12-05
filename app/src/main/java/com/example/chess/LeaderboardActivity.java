package com.example.chess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.model.Player;
import com.example.chess.model.Ranking;


import java.util.List;
///handles the leaderboard screen and pulling from rankings
/// @author Hidden Village
public class LeaderboardActivity extends AppCompatActivity {
    private Ranking ranking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        ranking = new Ranking(this); // Initialize the Ranking object

        //Handling buttons
        Button clearLeaderboardButton = findViewById(R.id.clearLeaderboardButton);
        Button leaderboardBackButton = findViewById(R.id.leaderboardBackButton);

        clearLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ranking.clearRankings();
                displayLeaderboard(findViewById(R.id.leaderboardTextView)); // Refresh leaderboard
            }
        });

        leaderboardBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        TextView leaderboardTextView = findViewById(R.id.leaderboardTextView);
        displayLeaderboard(leaderboardTextView);
    }

    private void displayLeaderboard(TextView leaderboardTextView) {
        List<Player> topPlayers = ranking.getTopPlayers(10); // Get top 10 players as an example
        StringBuilder leaderboardText = new StringBuilder();
        for (Player player : topPlayers) {
            leaderboardText.append(player.getName()).append(": ").append(player.getScore()).append("\n");
        }
        leaderboardTextView.setText(leaderboardText.toString());
    }
}
