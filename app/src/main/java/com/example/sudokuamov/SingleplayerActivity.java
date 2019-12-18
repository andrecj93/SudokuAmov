package com.example.sudokuamov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.Levels;
import com.google.gson.Gson;

import androidx.annotation.NonNull;

public class SingleplayerActivity extends Activity {
    //Singleton to run for the entire game
    private GameEngine game;
    String mode, jsonGameObject;
    int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);


        game = GameEngine.getInstance();


        //activity is being created for the first time
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            //Single player or two players from menu activity
            mode = intent.getStringExtra("Mode");//TODO CHANGE THIS TO ENUM
            //Intent for difficulty passed from the level activity
            difficulty = intent.getIntExtra("Difficulty", 0);

            //Should get a new instance
            initBoard(true);
        } else {
            game.redrawGame(this);
            printSudoku(game.getGrid().getSudokuCellsInteger());

        }

        /*else {
            //Activity was already created and we should get the values we need from the saved instance state

            jsonGameObject = savedInstanceState.getString("gameObject");
            mode = savedInstanceState.getString("Mode");
            difficulty = savedInstanceState.getInt("Difficulty", 0);


            Type listType = new TypeToken<ArrayList<GameCell>>(){}.getType();
            List<GameCell> gameCells = new Gson().fromJson(jsonGameObject, listType);


            game.createSudokuGrid(this, gameCells);
        }*/

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("gameObject", new Gson().toJson(game.getGameBoard()));
        outState.putString("Mode", this.mode);
        outState.putInt("Difficulty", this.difficulty);


        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GameEngine.resetGame();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initBoard(false);
    }


    private void initBoard(boolean start) {
        game = GameEngine.getInstance();

        if (start) {
            game.setLevels(Levels.fromInteger(difficulty));
            game.createSudokuGrid(this);
        }

        printSudoku(game.getGrid().getSudokuCellsInteger());
        game.printSolution();
    }

    private void printSudoku(int[][] sudokuGrid){
        System.out.println("----Sudoku Grid----");
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
