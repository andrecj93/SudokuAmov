package com.example.sudokuamov.game;

import android.content.Context;
import android.widget.Toast;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameCell;
import com.example.sudokuamov.view.sudokuGrid.SudokuCell;

public class GameGrid {

    private SudokuCell[][] sudokuCells = new SudokuCell[Configurations.GRID9][Configurations.GRID9];
    private Context context;
    private GameEngine gameEngine = null;

    public GameGrid(Context context, GameEngine gameEngine)
    {
        this.context = context;
        this.gameEngine = gameEngine;

        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                sudokuCells[x][y] = new SudokuCell(context);
            }
        }
    }

    public void setSudokuCellsByIntArray() {
        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                GameCell gameCell = gameEngine.getGameCell(x, y);

                sudokuCells[x][y].setInitValue(gameCell.getValue());

                if (!gameCell.isChangeable())
                    sudokuCells[x][y].setNotModifiable();
            }
        }
    }

    public SudokuCell[][] getSudokuCells(){
        return sudokuCells;
    }

    public SudokuCell getItem(int x, int y)
    {
        return sudokuCells[x][y];
    }

    public SudokuCell getItem(int position)
    {
        int x = position % 9;
        int y = position / 9;

        return sudokuCells[x][y];
    }

    public void setItem(int x, int y, int number)
    {
        sudokuCells[x][y].setValue(number);

        gameEngine.getGameCell(x, y).setValue(number);


        this.checkGame();
    }

    public int[][] getSudokuCellsInteger(){
        int[][] sudGrid = new int[Configurations.GRID9][Configurations.GRID9];

        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                sudGrid[x][y] = getItem(x,y).getValue();
            }
        }

        return sudGrid;
    }

    public boolean isAllCellsFilled(){
        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                if (sudokuCells[x][y].getValue() == 0)
                    return false;
            }
        }
        return true;
    }

    public void checkGame(){
        //Check end game success

        //If game is not successful and all numbers are filled
        if (isAllCellsFilled()) {
            if (gameEngine.getEndOffGame())
                Toast.makeText(context, "Well Done! That is the correct solution.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Try again! That is not a correct solution.", Toast.LENGTH_LONG).show();
        }
    }
}
