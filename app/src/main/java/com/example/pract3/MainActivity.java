package com.example.pract3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    SharedPreferences themeSettings;
    SharedPreferences.Editor settingsEditor;
    SharedPreferences statistics;
    public ImageButton changeTheme;
    public String turnPlayer = "X";
    private Button[][] buttons = new Button[3][3];

    private int countWin = 0;
    private int countLose = 0;
    private int countDraw = 0;
    private int countPressedButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        statistics = getSharedPreferences("STATISTICS", MODE_PRIVATE);

        boolean isNightMode = themeSettings.getBoolean("MODE_NIGHT_ON", false);
        AppCompatDelegate.setDefaultNightMode(isNightMode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        changeTheme = findViewById(R.id.themeChangeButton);
        Button startByPlayerToPlayer = findViewById(R.id.playWithPlayerButton);
        Button startByPlayerToBot = findViewById(R.id.playWithBotButton);
        updateButtonIcon(isNightMode);

        loadStatistics();

        buttons[0][0] = findViewById(R.id.button1);
        buttons[0][1] = findViewById(R.id.button2);
        buttons[0][2] = findViewById(R.id.button3);
        buttons[1][0] = findViewById(R.id.button4);
        buttons[1][1] = findViewById(R.id.button5);
        buttons[1][2] = findViewById(R.id.button6);
        buttons[2][0] = findViewById(R.id.button7);
        buttons[2][1] = findViewById(R.id.button8);
        buttons[2][2] = findViewById(R.id.button9);

        updateStat();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonGamePlayersClick((Button) v);
                    }
                });
            }
        }

        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNightMode = themeSettings.getBoolean("MODE_NIGHT_ON", false);
                settingsEditor = themeSettings.edit();
                settingsEditor.putBoolean("MODE_NIGHT_ON", !isNightMode);
                settingsEditor.apply();

                AppCompatDelegate.setDefaultNightMode(isNightMode ?
                        AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);

                updateButtonIcon(!isNightMode);

                Toast.makeText(MainActivity.this,
                        isNightMode ? "Дневной режим активирован!" : "Ночной режим активирован!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        startByPlayerToBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        buttons[i][j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onButtonGameBotClick((Button) v);
                            }
                        });
                    }
                }
            }
        });

        startByPlayerToPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        buttons[i][j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onButtonGamePlayersClick((Button) v);
                            }
                        });
                    }
                }
            }
        });
    }

    private void onButtonGamePlayersClick(Button button) {
        if (!button.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Клетка уже занята", Toast.LENGTH_SHORT).show();
            return;
        }

        button.setText(turnPlayer);
        countPressedButton++;

        String result = checkForWinner(turnPlayer, countPressedButton);
        if (result.equals("Victory")) {
            if (turnPlayer.equals("X")) {
                countWin++;
            } else {
                countLose++;
            }
            Toast.makeText(MainActivity.this, turnPlayer + " выиграл!", Toast.LENGTH_SHORT).show();
            saveStatistics();
            resetGame();
            updateStat();
        } else if (result.equals("Draw")) {
            countDraw++;
            Toast.makeText(MainActivity.this, "Ничья!", Toast.LENGTH_SHORT).show();
            saveStatistics();
            resetGame();
            updateStat();
        } else {
            turnPlayer = turnPlayer.equals("X") ? "O" : "X";
        }
    }

    private void onButtonGameBotClick(Button button)
    {
        if (!button.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Клетка уже занята", Toast.LENGTH_SHORT).show();
            return;
        }
        button.setText(turnPlayer);
        countPressedButton++;
        String result = checkForWinner(turnPlayer, countPressedButton);
        if (result.equals("Victory")) {
            countWin++;
            Toast.makeText(MainActivity.this, turnPlayer + " выиграл!", Toast.LENGTH_SHORT).show();
            saveStatistics();
            resetGame();
            updateStat();
        } else if (result.equals("Draw")) {
            countDraw++;
            Toast.makeText(MainActivity.this, "Ничья!", Toast.LENGTH_SHORT).show();
            saveStatistics();
            resetGame();
            updateStat();
        }
        botStep();
    }

    private void updateButtonIcon(boolean isNightMode) {
        changeTheme.setImageResource(isNightMode ? R.drawable.sun : R.drawable.moon);
    }

    private void resetGame() {
        countPressedButton = 0;
        turnPlayer = "X";

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
    }

    private String checkForWinner(String player, int pressedButtonCount) {
        if (buttons[0][0].getText().toString().equals(player) &&
                buttons[0][1].getText().toString().equals(player) &&
                buttons[0][2].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[1][0].getText().toString().equals(player) &&
                buttons[1][1].getText().toString().equals(player) &&
                buttons[1][2].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[2][0].getText().toString().equals(player) &&
                buttons[2][1].getText().toString().equals(player) &&
                buttons[2][2].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[0][0].getText().toString().equals(player) &&
                buttons[1][0].getText().toString().equals(player) &&
                buttons[2][0].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[0][1].getText().toString().equals(player) &&
                buttons[1][1].getText().toString().equals(player) &&
                buttons[2][1].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[0][2].getText().toString().equals(player) &&
                buttons[1][2].getText().toString().equals(player) &&
                buttons[2][2].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[0][0].getText().toString().equals(player) &&
                buttons[1][1].getText().toString().equals(player) &&
                buttons[2][2].getText().toString().equals(player)) {
            return "Victory";
        }
        if (buttons[0][2].getText().toString().equals(player) &&
                buttons[1][1].getText().toString().equals(player) &&
                buttons[2][0].getText().toString().equals(player)) {
            return "Victory";
        }

        if (pressedButtonCount == 9) {
            return "Draw";
        }
        return "";
    }

    private void botStep() {
        int a, b;
        do {
            a = (int) (Math.random() * 3);
            b = (int) (Math.random() * 3);
        } while (!buttons[a][b].getText().equals(""));

        buttons[a][b].setText("O");
        countPressedButton++;

        String result = checkForWinner("O", countPressedButton);
        if (result.equals("Victory")) {
            countLose++;
            Toast.makeText(MainActivity.this, "O" + " выиграл!", Toast.LENGTH_SHORT).show();
            saveStatistics();
            resetGame();
            updateStat();
        } else if (result.equals("Draw")) {
            countDraw++;
            Toast.makeText(MainActivity.this, "Ничья!", Toast.LENGTH_SHORT).show();
            saveStatistics();
            resetGame();
            updateStat();

        }
    }

    private void saveStatistics() {
        SharedPreferences.Editor editor = statistics.edit();
        editor.putInt("countWin", countWin);
        editor.putInt("countLose", countLose);
        editor.putInt("countDraw", countDraw);
        editor.apply();
    }

    private void loadStatistics() {
        countWin = statistics.getInt("countWin", 0);
        countLose = statistics.getInt("countLose", 0);
        countDraw = statistics.getInt("countDraw", 0);
    }

    private void updateStat()
    {
        TextView wins = findViewById(R.id.textWinValue);
        wins.setText(String.valueOf(countWin));
        TextView loses = findViewById(R.id.textLoseValue);
        loses.setText(String.valueOf(countLose));
        TextView draws = findViewById(R.id.textDrawValue);
        draws.setText(String.valueOf(countDraw));
    }
}