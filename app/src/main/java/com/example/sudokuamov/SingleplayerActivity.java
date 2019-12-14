package com.example.sudokuamov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.Levels;

public class SingleplayerActivity extends Activity {
    //Singleton to run for the entire game
    private GameEngine game;
    String mode;
    int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        game.resetGame();

        Intent intent = getIntent();

        mode = intent.getStringExtra("Mode");//TODO CHANGE THIS TO ENUM


        difficulty = intent.getIntExtra("Difficulty", 0);


        Log.d("difficulty", "=" + difficulty);
        initBoard(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initBoard(false);
    }


    private void initBoard(Boolean start) {
        game = GameEngine.getInstance();
        //Set difficulty of the game

        if (start) {
            game.setLevels(Levels.fromInteger(difficulty));
            game.createSudokuGrid(this);
        }


        printSudoku(game.getGrid().getSudokuCellsInteger());
    }

    private void printSudoku(int[][] sudokuGrid){
        for (int y = 0; y < Configurations.GRID9; y++)
        {
            for (int x = 0; x < Configurations.GRID9; x++)
            {
                System.out.print(sudokuGrid[x][y] + "|");
            }
            System.out.println();
        }
    }
}
