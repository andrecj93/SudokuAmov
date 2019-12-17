package com.example.sudokuamov.game;

import android.content.Context;
import android.widget.Toast;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.view.sudokuGrid.SudokuCell;

public class GameGrid {
    private SudokuCell[][] sudokuCells = new SudokuCell[Configurations.GRID9][Configurations.GRID9];
    private Context context;

    public GameGrid(Context context)
    {
        this.context = context;

        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                sudokuCells[x][y] = new SudokuCell(context);
            }
        }
    }

    public void setSudokuCellsByIntArray(int[][] grid) {
        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                sudokuCells[x][y].setInitValue(grid[x][y]);
                //TODO CHANGE THIS BECAUSE WHEN THE ACTIVITY IS RESTORED THE ARRAY COMES WITH THE VALUES ALREADY CHANGED AND THEY ARE NOT ZERO ...
                //TODO SO WE MIGHT NEED TO TRACK THE USERS MOVEMENT (HISTORY) AND THEN CHECK IF THEY WERE CHANGED. AND IF SO, DONT SET THOSE ONES TO NOT MODIFIABLE
                if (grid[x][y] != 0)
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
        if (SudokuChecker.getInstance().checkSudoku(getSudokuCellsInteger())){
            Toast.makeText(context, "You solved the sudoku", Toast.LENGTH_LONG).show();
        }
        else{
            //If game is not successful and all numbers are filled
            if (isAllCellsFilled())
            {
                Toast.makeText(context, "Try again! That is not a correct solution.", Toast.LENGTH_LONG).show();
                //SudokuGenerator.getInstance().clearGrid(getSudokuCellsInteger());
            }

        }
    }
}
