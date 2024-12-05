package com.example.chess;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/// handles the win screen after a game is completed
/// @author Hidden Village
public class GameOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Retrieve the winning message and enum from the intent
        Intent intent = getIntent();
        String winningMessage = intent.getStringExtra("winningMessage");

        TextView winningMessageTextView = findViewById(R.id.winningMessageTextView);
        winningMessageTextView.setText(winningMessage);

        // Set up button actions
        Button newGameNewPlayersButton = findViewById(R.id.newGameButton);
        Button leaderboardButton = findViewById(R.id.leaderboardButton);
        Button mainMenuButton = findViewById(R.id.mainMenuButton);

        newGameNewPlayersButton.setOnClickListener(v -> {
            // Start a new game with new players
            Intent newGameIntent = new Intent(GameOverActivity.this, ChessGameActivity.class);
            newGameIntent.putExtra("samePlayers", false);
            startActivity(newGameIntent);
            finish();
        });

        leaderboardButton.setOnClickListener(v -> {
            // Display the leaderboard
            Intent leaderboardIntent = new Intent(GameOverActivity.this, LeaderboardActivity.class);
            startActivity(leaderboardIntent);
        });

        mainMenuButton.setOnClickListener(v -> {
            // Return to the main menu
            Intent mainMenuIntent = new Intent(GameOverActivity.this, MainActivity.class);
            startActivity(mainMenuIntent);
            finish();
        });
    }


}
