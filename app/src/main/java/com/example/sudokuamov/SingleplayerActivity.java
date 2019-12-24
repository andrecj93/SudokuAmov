package com.example.sudokuamov;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.view.GameGrid;
import com.example.sudokuamov.view.PlayerView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class SingleplayerActivity extends AppCompatActivity {
    //Singleton to run for the entire game
    private GameEngine game;
    private GameGrid gameGrid;
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

            initBoard(true);
        } else {

            initBoard(false);

        }
    }

    @Override
    public void onBackPressed() {
        game.resetGame(this);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        game = GameEngine.getInstance();

        initBoard(false);
    }



    private void initBoard(boolean start) {
        TextView playerNameDisplay = findViewById(R.id.username);
        TextView timerDisplay = findViewById(R.id.time);
        TextView pointsDisplay = findViewById(R.id.points);


        final PlayerView playerView = new PlayerView(playerNameDisplay, pointsDisplay, timerDisplay);

        gameGrid = new GameGrid(this, game, playerView);

        setObserver();

        if (start) {
            game.setLevels(Levels.fromInteger(difficulty));

            game.createSudokuGrid(gameGrid);

            game.setGameMode(mode);
        } else {
            game.redrawGame(gameGrid);
        }
    }

    private void setObserver() {
        game.setObserver(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                gameGrid.getPlayerView().RefreshPlayerView();
            }
        });
    }

    /*private void setObserverGrid(){
        game.setObserver(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                gameGrid.getPlayerView().RefreshPlayerView();
            }
        });
    }*/

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
