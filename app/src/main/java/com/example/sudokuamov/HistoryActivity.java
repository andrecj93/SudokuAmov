package com.example.sudokuamov;

import android.os.Bundle;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        loadHistoryFromJson();

        TableLayout gameHistory = (TableLayout) findViewById(R.id.historyTable);


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
