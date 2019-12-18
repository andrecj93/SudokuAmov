package com.example.sudokuamov.game;

import android.content.Context;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameCell;
import com.example.sudokuamov.game.helpers.Levels;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private static GameEngine instance;
    private GameGrid grid = null;
    private Levels levels;
    private int selectedPosX = -1, selectedPosY = -1;
    private static Object mutex = new Object();
    private List<GameCell> gameBoard;

    public GameEngine() {
        gameBoard = new ArrayList<>();
    }

    public static void resetGame() {
        instance = null;
    }
/*
    public static GameEngine getInstance() {
        if (instance == null)
            instance = new GameEngine();

        return instance;
    }*/


    /*Local variable result seems unnecessary. But it’s there to improve the performance of our
     code. In cases where the instance is already initialized (most of the time), the volatile
      field is only accessed once (due to “return result;” instead of “return instance;”).
      This can improve the method’s overall performance by as much as 25 percent.*/

    public static GameEngine getInstance() {
        GameEngine result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new GameEngine();
            }
        }
        return result;
    }



    public void createSudokuGrid(Context context){
        synchronized (mutex) {
            int[][] sudokuSolution = new int[Configurations.GRID9][Configurations.GRID9];
            int[][] Sudoku = SudokuGenerator.getInstance().generateGrid();

            //Stored solution before removing values
            for (int i = 0; i < Sudoku.length; i++)
                for (int j = 0; j < Sudoku[i].length; j++)
                    sudokuSolution[i][j] = Sudoku[i][j];

            //Remove values to show the grid to the user
            Sudoku = SudokuGenerator.getInstance().removeValues(Sudoku, levels);

            for (int x = 0; x < Configurations.GRID9; x++) {
                for (int y = 0; y < Configurations.GRID9; y++) {
                    if (Sudoku[x][y] == sudokuSolution[x][y])
                        gameBoard.add(new GameCell(Sudoku[x][y], sudokuSolution[x][y], null, false));
                    else
                        gameBoard.add(new GameCell(0, sudokuSolution[x][y], null, true));
                }
            }

            grid = new GameGrid(context, this);
            grid.setSudokuCellsByIntArray();
        }
    }

    public void createSudokuGrid(Context context, List<GameCell> gameBoard) {

        this.gameBoard = gameBoard;

        grid = new GameGrid(context, this);
        grid.setSudokuCellsByIntArray();
    }

    public void redrawGame(Context context) {
        grid = new GameGrid(context, this);
        grid.setSudokuCellsByIntArray();
    }


    public GameCell getGameCell(int x, int y) {
        int pos = (Configurations.GRID9 * x) + y;

        return gameBoard.get(pos);
    }

    public List<GameCell> getGameBoard() {
        return gameBoard;
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

    public boolean getEndOffGame() {
        for (GameCell gc : gameBoard) {
            if (gc.getValue() != gc.getSolution())
                return false;
        }

        return true;
    }

}
