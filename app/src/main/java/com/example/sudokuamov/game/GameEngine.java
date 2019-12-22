package com.example.sudokuamov.game;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameMode;
import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.view.GameGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameEngine {
    private static GameEngine instance;
    private GameGrid grid = null;
    private Levels levels;
    private int selectedPosX = -1, selectedPosY = -1;
    private static final Object mutex = new Object();
    private List<GameCell> gameBoard;
    private GameMode gameMode;
    private List<Profile> players;
    private Timer timer;
    private int countDown;

    public GameEngine() {
        gameBoard = new ArrayList<>();
        countDown = 30;
    }


    private TimerTask getTimerTask() {
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                GameEngine gameEngine = GameEngine.getInstance();
                gameEngine.setCountDown(gameEngine.getCountDown() - 1);
            }
        };

        return timerTask;
    }

    public int getCountDown() {
        return countDown;
    }

    public void setCountDown(int countDown) {
        this.countDown = countDown;
        grid.setTime(countDown);
        timer.schedule(getTimerTask(), 1 * 1000);
    }

    public static void resetGame() {
        instance = null;
    }

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


    public void createSudokuGrid(GameGrid gameGrid) {
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

            grid = gameGrid;

            grid.setSudokuCellsByIntArray();
        }
    }
/*
    public void createSudokuGrid(Context context, List<GameCell> gameBoard, GameGrid gameGrid  ) {

        this.gameBoard = gameBoard;
        grid = gameGrid;

        grid.setSudokuCellsByIntArray();
    }*/

    public void redrawGame(GameGrid gameGrid) {
        grid = gameGrid;
        grid.setSudokuCellsByIntArray();
    }


    public GameCell getGameCell(int x, int y) {
        int pos = (Configurations.GRID9 * x) + y;

        return gameBoard.get(pos);
    }

    public List<GameCell> getGameBoard() {
        return gameBoard;
    }

    private int[][] getGameBoardArray() {
        int[][] gameArray = new int[Configurations.GRID9][Configurations.GRID9];
        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                gameArray[x][y] = getGameCell(x, y).getValue();
            }
        }
        return gameArray;
    }

    public boolean checkNewNumberFill(int x, int y, int number) {
        return SudokuSolver.isSafe(this.getGameBoardArray(), x, y, number);
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

    public void GetPosssibleNumber() {
        if (selectedPosX != -1 && selectedPosY != -1) {
            grid.getHelp(selectedPosX, selectedPosY);
        }

    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(String mode) {
        switch (mode) {
            case "networkgame":
                gameMode = GameMode.MULTIPLAYER_MULTIDEVICE;
                timer = new Timer();
                countDown = 30;
                timer.schedule(getTimerTask(), 1 * 1000);
                break;
            case "multiplayer":
                gameMode = GameMode.MULTIPLAYER_SAMEDEVICE;
                timer = new Timer();
                countDown = 30;
                timer.schedule(getTimerTask(), 1 * 1000);
                break;
            default:
                gameMode = GameMode.SINGLEPLAYER;
                timer = new Timer();
                countDown = 30;
                timer.schedule(getTimerTask(), 1 * 1000);
        }
    }

    public void printSolution() {
        int[][] arr = new int[9][9];
        System.out.println("---Sudoku Solution---");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                arr[i][j] = getGameCell(j, i).getSolution();
                System.out.print(arr[i][j] + "|");
            }
            System.out.println();
        }

    }

    public void setGrid(GameGrid gameGrid) {
        this.grid = gameGrid;
    }

    public boolean getEndOffGame() {
        /*if (!SudokuChecker.getInstance().checkSudoku(getGrid().getSudokuCellsInteger()))
            return false;*/

        /*for (GameCell gc : gameBoard) {
            if (gc.getValue() != gc.getSolution())
                return false;
        }*/
        int length = getGameBoardArray().length;
        return SudokuSolver.solveSudoku(getGameBoardArray(), length);
    }
}
