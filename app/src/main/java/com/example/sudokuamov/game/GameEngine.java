package com.example.sudokuamov.game;

import android.content.Context;

import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.view.sudokuGrid.GameGrid;

public class GameEngine {
    private static GameEngine instance;
    private GameGrid grid = null;
    private Levels levels;

    private int selectedPosX = -1, selectedPosY = -1;

    public GameEngine() {

    }

    public void resetGame() {
        instance = null;
    }

    public static GameEngine getInstance() {
        if (instance == null)
            instance = new GameEngine();

        return instance;
    }

    public void createSudokuGrid(Context context){
        int[][] Sudoku = SudokuGenerator.getInstance().generateGrid();

        Sudoku = SudokuGenerator.getInstance().removeValues(Sudoku, levels);

        grid = new GameGrid(context);
        grid.setGrid(Sudoku);
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
}
