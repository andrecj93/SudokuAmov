package com.example.sudokuamov;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokuamov.game.Profile;
import com.example.sudokuamov.game.helpers.GameInfoHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    List<GameInfoHistory> gameInfoHistoryList = null;
    TableLayout gameHistoryTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        loadHistoryFromJson();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        gameHistoryTable = findViewById(R.id.historyTable);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        if (gameInfoHistoryList == null) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView txtNoResults = new TextView(this);
            txtNoResults.setLayoutParams(rowParams);
            txtNoResults.setGravity(Gravity.CENTER);
            txtNoResults.setBackgroundColor(Color.GRAY);
            txtNoResults.setText(R.string.no_games_were_found);

            tableRow.addView(txtNoResults);
            gameHistoryTable.addView(tableRow);
        } else if (!gameInfoHistoryList.isEmpty()) {
            for (GameInfoHistory info : gameInfoHistoryList) {
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(tableParams);


                //Column 1 - Modo de jogo
                //Here you are creating a new text view.
                TextView textViewMode = new TextView(this);
                textViewMode.setLayoutParams(rowParams);
                textViewMode.setGravity(Gravity.CENTER);
                textViewMode.setBackgroundColor(Color.GRAY);
                textViewMode.setText(info.getGameMode());
                tableRow.addView(textViewMode);

                //Column 2 - Dificuldade
                TextView textViewDificuldade = new TextView(this);
                textViewDificuldade.setLayoutParams(rowParams);
                textViewDificuldade.setGravity(Gravity.CENTER);
                textViewDificuldade.setBackgroundColor(Color.GRAY);
                textViewDificuldade.setText(info.getLevel());
                tableRow.addView(textViewDificuldade);

                //Column 3 - Jogadores
                TextView textViewJogadores = new TextView(this);
                textViewJogadores.setLayoutParams(rowParams);
                textViewJogadores.setGravity(Gravity.CENTER);

                StringBuilder jogadores = new StringBuilder();
                int i = 0;
                for (Profile jogador : info.getPlayerList()) {
                    ++i;
                    jogadores.append(jogador.getUsername());
                    if (i != info.getPlayerList().size())
                        jogadores.append(" | ");
                }
                textViewJogadores.setBackgroundColor(Color.GRAY);
                textViewJogadores.setText(jogadores.toString());
                tableRow.addView(textViewJogadores);

                //Column 4 - Vencedor
                TextView textViewWinner = new TextView(this);
                textViewWinner.setLayoutParams(rowParams);
                textViewWinner.setGravity(Gravity.CENTER);
                textViewWinner.setBackgroundColor(Color.GRAY);
                textViewWinner.setText(info.getWinnerProfile().getUsername());
                tableRow.addView(textViewWinner);

                gameHistoryTable.addView(tableRow);
            }
        }

    }

    private void loadHistoryFromJson() {
        Gson gson = new Gson();
        String pathHistory = getExternalFilesDir(null) + "/" + "gameHistory.json";


        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathHistory));
            Type type = new TypeToken<List<GameInfoHistory>>() {
            }.getType();

            gameInfoHistoryList = gson.fromJson(bufferedReader, type);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


}
