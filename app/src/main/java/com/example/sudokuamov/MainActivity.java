package com.example.sudokuamov;

import android.app.Activity;

import android.os.Bundle;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.Levels;

public class MainActivity extends Activity {
    //Singleton to run for the entire game
    private GameEngine game = GameEngine.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set difficulty of the game
        game.setLevels(Levels.EASY);
        //Generates a new grid and creates a new GameGrid object which contains the grid view and the sudoku cells
        game.createSudokuGrid(this);

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
