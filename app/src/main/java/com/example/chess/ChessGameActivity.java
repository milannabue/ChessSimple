package com.example.chess;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.controller.ChessGameController;
import com.example.chess.model.Bishop;
import com.example.chess.model.ChessBoard;
import com.example.chess.model.ChessGame;
import com.example.chess.model.King;
import com.example.chess.model.Knight;
import com.example.chess.model.Leaderboard;
import com.example.chess.model.Pawn;
import com.example.chess.model.Piece;
import com.example.chess.model.PieceColor;
import com.example.chess.model.Player;
import com.example.chess.model.Position;
import com.example.chess.model.Queen;
import com.example.chess.model.Ranking;
import com.example.chess.model.Rook;

import java.util.List;
import java.util.Random;
///responsible for the main chess game, including keeping track of the board, pieces, and players during a game
/// @author Hidden Village
public class ChessGameActivity extends AppCompatActivity {
    private ChessGameController chessController;
    private Button pauseButton;
    private Button exitButton;
    private TextView timerTextView;
    private boolean isPaused = false;
    private boolean isTimerStarted = false;
    private long startTime = 0L;
    private long pauseTime = 0L;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        }
    };
    private Button[][] boardButtons;
    ChessBoard board;

    private TextView statusTextView;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Player player1, player2;
    private Leaderboard leaderboard;
    private Ranking ranking;
    private static ChessGameActivity instance;
    private boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_chess_game);

        // Initialize buttons
        boardButtons = new Button[8][8];
        statusTextView = findViewById(R.id.statusTextView);
        pauseButton = findViewById(R.id.pause_button);
        exitButton = findViewById(R.id.gameplay_back_button);
        timerTextView = findViewById(R.id.timer);

        initializeBoardButtons(); // Initialize the buttons
        setupBoardButtons(); // Set up board with alternating colors

        leaderboard = new Leaderboard();
        ranking = new Ranking(this);

        pauseButton.setOnClickListener(v -> {
            isPaused = !isPaused;
            if (isPaused) {
                pauseButton.setText("▷");
                timerHandler.removeCallbacks(timerRunnable);
                pauseTime = System.currentTimeMillis() - startTime;
                setBoardButtonsEnabled(false); // Disable board buttons when paused
            } else {
                pauseButton.setText("||");
                startTime = System.currentTimeMillis() - pauseTime;
                timerHandler.postDelayed(timerRunnable, 0);
                setBoardButtonsEnabled(true); // Enable board buttons when resumed
            }
        });

        exitButton.setOnClickListener(v -> {
            new AlertDialog.Builder(ChessGameActivity.this, R.style.alertDialogue)
                    .setTitle("Exit Game")
                    .setMessage("Are you sure you want to exit and discard the current game?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> finish())
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        askForPlayerNames(); // Ask for 2 player names
    }

    public void askForPlayerNames() {
        promptPlayerName("white"); // Start by asking for Player 1's name
    }

    private void promptPlayerName(String plrType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialogue);
        builder.setTitle("Enter the name of " + plrType);

        final EditText input = new EditText(this);
        input.setHint(plrType + "'s name");

        input.setHintTextColor(Color.GRAY);
        input.setTextColor(Color.WHITE);

        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String playerName = input.getText().toString().trim();

            if ((plrType.equals("white") && player2 != null && player2.getName().equals(playerName)) ||
                    (plrType.equals("black") && player1 != null && player1.getName().equals(playerName))) {
                Toast.makeText(this, "This name is already taken by another player. Please choose a different name.", Toast.LENGTH_SHORT).show();
                promptPlayerName(plrType); // Ask again for the same player's name
            } else {
                Player player = new Player(playerName);
                leaderboard.addPlayer(playerName);
                ranking.addOrUpdatePlayer(player);

                if (plrType.compareTo("white") == 0) {
                    player1 = player;
                    promptPlayerName("black"); // Ask for Player 2's name
                } else {
                    player2 = player;
                    assignColorsToPlayers();
                    initializeGame(); // Initialize the game with players and ranking
                    startGame(); // Start the game once both names are set
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void initializeGame() {
        chessController = new ChessGameController(player1, player2, ranking);
    }

    private void startGame() {
        chessController.getGame().resetGame();
        refreshBoard();
        updateGameState();
        startTimer();  // Start the timer after both players' names are input and the game starts
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        isTimerStarted = true;
    }

    private void updateGameState() {
        if (chessController.isGameOver()) {
            handleGameOver(chessController.getCurrentPlayerColor() == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);
        } else if (chessController.isInCheck()) {
            statusTextView.setText(chessController.getCurrentPlayerColor() + " is in check!");
        } else {
            statusTextView.setText(chessController.getCurrentPlayerColor() + "'s turn.");
        }
    }

    private void highlightLegalMoves(Position position) {
        Piece selectedPiece = chessController.getGame().getBoard().getPiece(position.getRow(), position.getColumn());
        if (selectedPiece != null && selectedPiece.getColor() == chessController.getCurrentPlayerColor()) {
            List<Position> legalMoves = chessController.getLegalMoves(position);
            for (Position move : legalMoves) {
                boardButtons[move.getRow()][move.getColumn()].getBackground().setAlpha(100);
            }
        }
    }

    private void clearHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardButtons[row][col].getBackground().setAlpha(40);
            }
        }
    }

    public void handleGameOver(PieceColor winningColor) {
        isGameOver = true;
        String winningMessage;

        // Debug: Print game over trigger details
        System.out.println("handleGameOver called. Winning color: " + winningColor);

        if (winningColor == null) {
            // It's a draw
            winningMessage = "Draw!";
            System.out.println("Game result: Draw");

            if (player1 != null) {
                player1.setScore(player1.getScore() + 1);
            }
            if (player2 != null) {
                player2.setScore(player2.getScore() + 1);
            }

            // Update leaderboard and ranking if players are not null
            if (player1 != null) ranking.saveNewScore(player1);
            if (player2 != null) ranking.saveNewScore(player2);
            if (player1 != null) leaderboard.addOrUpdatePlayer(player1);
            if (player2 != null) leaderboard.addOrUpdatePlayer(player2);

            // Handle draw scenario
            ChessGameActivity.getInstance().onGameOver(winningMessage, null);
        } else if (winningColor == PieceColor.WHITE || winningColor == PieceColor.BLACK) {
            // Someone has won
            if (winningColor == PieceColor.WHITE) {
                winningMessage = "White wins.";
            } else {
                winningMessage = "Black wins.";
            }

            // Debug: Print winner details
            System.out.println("Game result: " + winningMessage);

            // Update leaderboard and ranking if players are not null
            if (player1 != null) ranking.saveNewScore(player1);
            if (player2 != null) ranking.saveNewScore(player2);
            if (player1 != null) leaderboard.addOrUpdatePlayer(player1);
            if (player2 != null) leaderboard.addOrUpdatePlayer(player2);

            // Handle win scenario
            ChessGameActivity.getInstance().onGameOver(winningMessage, winningColor);
        } else {
            // Unexpected scenario
            winningMessage = "Unexpected game result!";
            System.out.println("Game result: Unexpected scenario");
            ChessGameActivity.getInstance().onGameOver(winningMessage, null);
        }
    }


    private boolean isCastlingMove(Position start, Position end) {
        Piece movingPiece = chessController.getGame().getBoard().getPiece(start.getRow(), start.getColumn());
        return movingPiece instanceof King && Math.abs(start.getColumn() - end.getColumn()) == 2;
    }

    private boolean handleCastling(Position start, Position end) {
        int row = start.getRow();
        int colOffset = (end.getColumn() - start.getColumn()) > 0 ? 1 : -1;
        Position rookStart = new Position(row, colOffset > 0 ? 7 : 0);
        Position rookEnd = new Position(row, start.getColumn() + colOffset);

        Piece king = chessController.getGame().getBoard().getPiece(start.getRow(), start.getColumn());
        Piece rook = chessController.getGame().getBoard().getPiece(rookStart.getRow(), rookStart.getColumn());

        if (rook instanceof Rook && rook.getColor() == king.getColor() &&
                isCastlingPathClear(start, end, rookStart)) {
            chessController.getGame().getBoard().movePiece(start, end, true);
            chessController.getGame().getBoard().movePiece(rookStart, rookEnd, true);
            return true;
        }
        return false;
    }

    private boolean isCastlingPathClear(Position kingStart, Position kingEnd, Position rookStart) {
        int row = kingStart.getRow();
        int startCol = Math.min(kingStart.getColumn(), kingEnd.getColumn());
        int endCol = Math.max(kingStart.getColumn(), rookStart.getColumn());

        for (int col = startCol; col <= endCol; col++) {
            Position pos = new Position(row, col);
            Piece piece = chessController.getGame().getBoard().getPiece(pos.getRow(), pos.getColumn());
            if (piece != null && piece != chessController.getGame().getBoard().getPiece(kingStart.getRow(), kingStart.getColumn())
                    && piece != chessController.getGame().getBoard().getPiece(rookStart.getRow(), rookStart.getColumn())) {
                return false;
            }
        }
        return true;
    }

    public void onGameOver(String winningMessage, PieceColor winningColor) {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("winningMessage", winningMessage);
        if (winningColor != null) {
            intent.putExtra("winningColor", winningColor.name());
        } else {
            intent.putExtra("winningColor", "Draw"); // Use a string to indicate a draw
        }
        startActivity(intent);
        finish();
    }

    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = chessController.getGame().getBoard().getPiece(row, col);
                if (piece != null) {
                    String symbol = chessController.getPieceUnicodeSymbol(piece.getClass());
                    boardButtons[row][col].setText(symbol);
                    boardButtons[row][col].setTextColor(piece.getColor() == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
                } else {
                    boardButtons[row][col].setText("");
                }
            }
        }
    }

    public static ChessGameActivity getInstance() {
        return instance;
    }

    private void initializeBoardButtons() {
        GridLayout boardLayout = findViewById(R.id.boardLayout);
        boardLayout.setRowCount(8);
        boardLayout.setColumnCount(8);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Button button = new Button(this);
                button.setTextSize(35);
                button.setBackgroundResource(R.drawable.board_square);
                boardButtons[row][col] = button;
                int finalRow = row;
                int finalCol = col;
                boardButtons[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleSquareClick(finalRow, finalCol);
                    }
                });
                boardLayout.addView(boardButtons[row][col]);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        // If no piece is currently selected, select the piece at the clicked position
        if (selectedRow == -1 && selectedCol == -1) {
            Piece selectedPiece = chessController.getGame().getBoard().getPiece(row, col);
            if (selectedPiece != null && selectedPiece.getColor() == chessController.getCurrentPlayerColor()) {
                selectedRow = row;
                selectedCol = col;
                highlightLegalMoves(new Position(row, col));
            }
        } else {
            // If a piece is already selected, attempt to move it to the clicked position
            Position source = new Position(selectedRow, selectedCol);
            Position destination = new Position(row, col);

            // Handle special moves like castling and en passant
            if (isCastlingMove(source, destination)) {
                if (handleCastling(source, destination)) {
                    refreshBoard();
                    updateGameState();
                } else {
                    Toast.makeText(this, "Invalid castling move. Try again.", Toast.LENGTH_SHORT).show();
                }
            } else if (chessController.handleMove(source, destination)) {
                refreshBoard();
                updateGameState();
                // Check if a pawn promotion happened and show the promotion dialog
                Piece movedPiece = chessController.getGame().getBoard().getPiece(destination.getRow(), destination.getColumn());
                if (movedPiece instanceof Pawn && (destination.getRow() == 0 || destination.getRow() == 7)) {
                    showPromotionDialog(destination, movedPiece.getColor());
                }
            } else {
                Toast.makeText(this, "Invalid move. Try again.", Toast.LENGTH_SHORT).show();
            }

            // Clear selection and highlights
            clearHighlights();
            selectedRow = -1;
            selectedCol = -1;
        }
    }



    private void assignColorsToPlayers() {
        player1.setColor(PieceColor.WHITE);
        player2.setColor(PieceColor.BLACK);

        Toast.makeText(this, player1.getName() + " is " + player1.getColor() + " and " + player2.getName() + " is " + player2.getColor(), Toast.LENGTH_LONG).show();
    }

    private void setupBoardButtons() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Alternate the colors of the buttons to create a chessboard pattern
                boardButtons[row][col].getBackground().setAlpha(40);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(5, 5, 5, 5);
                boardButtons[row][col].setLayoutParams(params);
            }
        }
    }

    private void setBoardButtonsEnabled(boolean enabled) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardButtons[row][col].setEnabled(enabled);
            }
        }
    }

    private void showPromotionDialog(Position position, PieceColor color) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.selectionDialogue);
        builder.setTitle("Promote Pawn");

        String[] options = {"     Queen", "     Rook", "     Bishop", "     Knight"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE); // Set the text color to white
                textView.setTextSize(16);
                return view;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            switch (which) {
                case 0:
                    promotePawnTo(new Queen(color, position));
                    break;
                case 1:
                    promotePawnTo(new Rook(color, position));
                    break;
                case 2:
                    promotePawnTo(new Bishop(color, position));
                    break;
                case 3:
                    promotePawnTo(new Knight(color, position));
                    break;
            }
            refreshBoard();
            updateGameState();
        });

        builder.show();
    }

    private void promotePawnTo(Piece piece) {
        chessController.getGame().getBoard().setPiece(piece.getPosition().getRow(), piece.getPosition().getColumn(), piece);
    }


}



