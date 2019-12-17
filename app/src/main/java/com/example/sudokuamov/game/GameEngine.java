package com.example.sudokuamov.game;

import android.content.Context;

import com.example.sudokuamov.game.helpers.Levels;

public class GameEngine {
    private static GameEngine instance;
    private GameGrid grid = null;
    private Levels levels;
    private int[][] sudokuSolution;
    private int selectedPosX = -1, selectedPosY = -1;

    public GameEngine() {
    }

    public static void resetGame() {
        instance = null;
    }

    public static GameEngine getInstance() {
        if (instance == null)
            instance = new GameEngine();

        return instance;
    }

    public void createSudokuGrid(Context context){
        int[][] Sudoku = SudokuGenerator.getInstance().generateGrid();

        //Stored solution before removing values
        this.sudokuSolution = Sudoku;

        //Remove values to show the grid to the user
        Sudoku = SudokuGenerator.getInstance().removeValues(Sudoku, levels);

        grid = new GameGrid(context);
        grid.setSudokuCellsByIntArray(Sudoku);
    }

    public GameGrid getGrid(){
        return grid;
    }

    public Levels getLevels() {
        return levels;
    }

    public void setLevels(Levels levels) {
        this.levels = levels;
    }

    public void setSelectedPositions(int x, int y) {
        this.selectedPosX = x;
        this.selectedPosY = y;
    }

    public void setNumber(int number)
    {
        if (selectedPosX!=-1 && selectedPosY!=-1)
        {
            grid.setItem(selectedPosX,selectedPosY,number);
        }

        grid.checkGame();
    }

    public void setGrid(GameGrid gameGrid) {
        this.grid = gameGrid;
    }
}
