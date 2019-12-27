package com.example.sudokuamov.game;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameMode;
import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.view.GameGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class GameEngine {
    private static GameEngine instance;
    private GameGrid grid = null;
    private Levels levels;

    private int selectedPosX = -1, selectedPosY = -1;

    private static final Object mutex = new Object();

    private List<GameCell> gameBoard;
    private GameMode gameMode;

    private List<Profile> players;

    MutableLiveData<Boolean> changedPayer = new MutableLiveData<>();
    MutableLiveData<Boolean> changedGrid = new MutableLiveData<>();

    private Timer timer;
    private int countDown;
    private int ProfileActive;


    public GameEngine() {
        gameBoard = new ArrayList<>();
        players = new ArrayList<>();
    }


    private TimerTask getTimerTask() {
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                GameEngine gameEngine = GameEngine.getInstance();
                int countDown = gameEngine.getCountDown();

                if (countDown > 0)
                    gameEngine.setCountDown(countDown - 1);
                else
                    gameEngine.changePayerTurn();

            }
        };

        return timerTask;
    }


    public void changePayerTurn() {
        if (ProfileActive + 1 < players.size())
            ProfileActive++;
        else
            ProfileActive = 0;

        setCountDown(30);
    }

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



    public void addPlayerToList(Profile profile) {
        this.players.add(profile);
    }

    public int getCountDown() {
        return countDown;
    }

    public Profile getActivePalyer() {
        return players.get(ProfileActive);
    }

    public void resetGame(LifecycleOwner owner) {
        changedPayer.removeObservers(owner);

        if (timer != null)
            timer.cancel();

        instance = null;
    }

    /*Local variable result seems unnecessary. But it’s there to improve the performance of our
     code. In cases where the instance is already initialized (most of the time), the volatile
      field is only accessed once (due to “return result;” instead of “return instance;”).
      This can improve the method’s overall performance by as much as 25 percent.*/

    public void setCountDown(int countDown) {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        this.countDown = countDown;

        timer.schedule(getTimerTask(), 1 * 1000);

        changedPayer.postValue(true);
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

            //grid.setSudokuCellsByIntArray();//TODO CHANGE THIS TO OBVERVERTRIGGER

            changedGrid.postValue(true);
        }
    }

    public void redrawGame(GameGrid gameGrid) {
        grid = gameGrid;
        grid.setSudokuCellsByIntArray();//TODO CHANGE THIS TO OBVERVERTRIGGER

        changedGrid.postValue(true);
    }

    public GameCell getGameCell(int x, int y) {
        int pos = (Configurations.GRID9 * x) + y;

        return gameBoard.get(pos);
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

    public String getGameMode() {
        return GameMode.myToString(gameMode);
    }


    public void setGameMode(String mode) {
        switch (mode) {
            case "networkgame":
                gameMode = GameMode.MULTIPLAYER_MULTIDEVICE;
                countDown = 30;
                ProfileActive = 0;
                timer.schedule(getTimerTask(), 1 * 1000);
                break;
            case "multiplayer":
                gameMode = GameMode.MULTIPLAYER_SAMEDEVICE;

                ProfileActive = 0;

                setCountDown(30);
                break;
            default:
                gameMode = GameMode.SINGLEPLAYER;

                //players.add(new Profile("Pedro", null, null));
                ProfileActive = 0;

                changedPayer.postValue(true);
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

    public void setActivePlayerPoints() {
        getActivePalyer().setPoints(0);

        if (gameMode != GameMode.SINGLEPLAYER)
            setCountDown(20);
    }

    public void setObserver(LifecycleOwner lifecycleOwner, Observer<Boolean> observer) {
        changedPayer.observe(lifecycleOwner, observer);
    }

    public void removeObserver(LifecycleOwner lifecycleOwner) {
        changedPayer.removeObservers(lifecycleOwner);
    }


    public void setObserverGrid(LifecycleOwner lifecycleOwner, Observer<Boolean> observer) {
        changedGrid.observe(lifecycleOwner, observer);
    }

    public void removeObserverGrid(LifecycleOwner lifecycleOwner) {
        changedGrid.removeObservers(lifecycleOwner);
    }

}
