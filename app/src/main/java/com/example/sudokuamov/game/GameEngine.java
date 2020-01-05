package com.example.sudokuamov.game;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameMode;
import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.sockets.DataExchange;
import com.example.sudokuamov.sockets.GamePlay;
import com.example.sudokuamov.sockets.SocketConnector;
import com.example.sudokuamov.view.GameGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameEngine {
    private static final Object mutex = new Object();
    private static final Object mutexGameboard = new Object();
    private static GameEngine instance;
    MutableLiveData<Boolean> changedPayer = new MutableLiveData<>();
    MutableLiveData<Boolean> changedGrid = new MutableLiveData<>();
    private GameGrid grid = null;
    private Levels levels;
    private int selectedPosX = -1, selectedPosY = -1;
    private List<GameCell> gameBoard;
    private GameMode gameMode;
    private List<Profile> players;
    private Profile myProfile;
    private Timer timer;
    private int countDown;
    private int ProfileActive;


    public GameEngine() {

        gameBoard = new ArrayList<>();
        players = new ArrayList<>();
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

        if (gameMode.toString().equals(GameMode.MULTIPLAYER_MULTIDEVICE.toString())) {
            if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                DataExchange dataExchange = new DataExchange(DataExchange.Operation.setPlayerList, null, null, this.getAllPlayers(), this.getProfileActive(), null, 0);

                String list = dataExchange.getJSON();

                SocketConnector.getInstance().sendInfoToAllClients(list);
            }
        }

        new LooperThread().start();
    }

    public Profile getMyProfile() {
        return myProfile;
    }

    public void setMyProfile(Profile myProfile) {
        this.myProfile = myProfile;
    }

    public void addPlayerToList(Profile profile) {
        this.players.add(profile);
    }

    public void removeAllPlayers() {
        this.players.clear();
    }

    public Profile getThisPlayers(String ip) {
        for (Profile profile : players) {
            if (ip.equals(profile.getIp()))
                return profile;
        }

        return null;
    }

    public int getProfileActive() {
        return ProfileActive;
    }

    public void setProfileActive(int i) {
        if (ProfileActive != i) {
            ProfileActive = i;

            changedPayer.postValue(true);

            new LooperThread().start();
        }
    }

    public int getCountDown() {
        return countDown;
    }

    public void setCountDown(int countDown) {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        this.countDown = countDown;

        timer.schedule(getTimerTask(), 1 * 1000);


        if (gameMode.toString().equals(GameMode.MULTIPLAYER_MULTIDEVICE.toString())) {

            if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {

                DataExchange dataExchange = new DataExchange(DataExchange.Operation.SetTime, null, null, null, 0, null, countDown);

                SocketConnector.getInstance().sendInfoToAllClients(dataExchange.getJSON());
            }
        }
        changedPayer.postValue(true);
    }

    public Profile getActivePalyer() {
        return players.get(ProfileActive);
    }

    public List<Profile> getAllPlayers() {
        return players;
    }

    public void resetGame() {
        if (timer != null)
            timer.cancel();

        instance = null;
    }

    public void setTimer(int i) {
        this.countDown = i;

        changedPayer.postValue(true);
    }

    public void createCleanSudokuGrid(GameGrid gameGrid) {
        synchronized (mutex) {
            synchronized (mutexGameboard) {
                for (int x = 0; x < Configurations.GRID9; x++) {
                    for (int y = 0; y < Configurations.GRID9; y++) {
                        gameBoard.add(new GameCell(0, false));
                    }
                }
            }
            grid = gameGrid;

            SocketConnector socketConnector = SocketConnector.getInstance();

            DataExchange dataExchange = new DataExchange(DataExchange.Operation.setPlayerName, null, null, null, 0, this.getMyProfile().getUsername(), 0);

            socketConnector.sendClientInfo(dataExchange.getJSON());
        }
    }

    public void setGameBoard(List<GameCell> gameBoard) {
        synchronized (mutexGameboard) {

            for (int i = 0; i < this.gameBoard.size(); i++) {
                this.gameBoard.get(i).setValue(gameBoard.get(i).getValue());
                this.gameBoard.get(i).setChangeable(gameBoard.get(i).isChangeable());
            }

        }

        changedGrid.postValue(true);
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

            gameBoard.clear();

            synchronized (mutexGameboard) {
                for (int x = 0; x < Configurations.GRID9; x++) {
                    for (int y = 0; y < Configurations.GRID9; y++) {
                        if (Sudoku[x][y] == sudokuSolution[x][y])
                            gameBoard.add(new GameCell(Sudoku[x][y], false));
                        else
                            gameBoard.add(new GameCell(0, true));
                    }
                }
            }

            grid = gameGrid;


            if (gameMode.toString().equals(GameMode.MULTIPLAYER_MULTIDEVICE.toString())) {

                if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                    SocketConnector socketConnector = SocketConnector.getInstance();
                    synchronized (mutexGameboard) {
                        DataExchange dataExchange = new DataExchange(DataExchange.Operation.SetGameBoar, gameBoard, null, null, 0, null, 0);

                        socketConnector.sendInfoToAllClients(dataExchange.getJSON());
                    }

                }
            }


            changedGrid.postValue(true);
        }
    }

    public void redrawGame(GameGrid gameGrid) {
        grid = gameGrid;

        changedGrid.postValue(true);
    }

    public GameCell getGameCell(int x, int y) {
        synchronized (mutexGameboard) {
            int pos = (Configurations.GRID9 * y) + x;

            return gameBoard.get(pos);
        }
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

    public GameGrid getGrid() {
        return grid;
    }

    public void setGrid(GameGrid gameGrid) {
        this.grid = gameGrid;
    }

    public Levels getLevels() {
        return levels;
    }

    public void setLevels(Levels levels) {
        this.levels = levels;
    }


    public void setRemoteNumber(int x, int y, int number) {
        setSelectedPositions(x, y);
        setNumber(number);
    }


    public void setSelectedPositions(int x, int y) {
        this.selectedPosX = x;
        this.selectedPosY = y;
    }

    public void setNumber(int number) {
        if (selectedPosX < 0 || selectedPosY < 0) {
            return;
        }


        if (gameMode == GameMode.MULTIPLAYER_MULTIDEVICE) { //Client ACTIONS
            if (SocketConnector.getAmIserver() == SocketConnector.CLIENT) {
                DataExchange dataExchange = new DataExchange(DataExchange.Operation.CheckNewGamePlay, null, new GamePlay(number, selectedPosX, selectedPosY), null, 0, null, 0);

                SocketConnector.getInstance().sendClientInfo(dataExchange.getJSON());
                return;
            }
        }


        if (grid.setItem(selectedPosX, selectedPosY, number)) {
            if (gameMode == GameMode.MULTIPLAYER_MULTIDEVICE) {//SERVER ACTIONS
                if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                    DataExchange dataExchange = new DataExchange(DataExchange.Operation.SetCellValue, this.gameBoard, null, this.players, this.getProfileActive(), null, 0);

                    SocketConnector.getInstance().sendInfoToAllClients(dataExchange.getJSON());
                }
            }

            grid.checkGame();
        } else {
            if (gameMode == GameMode.MULTIPLAYER_MULTIDEVICE) {//SERVER ACTIONS
                if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                    DataExchange dataExchange = new DataExchange(DataExchange.Operation.SetCellRed, null, new GamePlay(number, selectedPosX, selectedPosY), null, 0, null, 0);

                    SocketConnector.getInstance().sendInfoToAllClients(dataExchange.getJSON());
                }
            }
        }
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
        if (mode.equals(GameMode.SINGLEPLAYER.toString())) {
            gameMode = GameMode.SINGLEPLAYER;
            ProfileActive = 0;
            if (timer != null) timer.cancel();
            changedPayer.postValue(true);
        } else if (mode.equals(GameMode.MULTIPLAYER_SAMEDEVICE.toString())) {
            gameMode = GameMode.MULTIPLAYER_SAMEDEVICE;
            ProfileActive = 0;
            setCountDown(30);

        } else {
            gameMode = GameMode.MULTIPLAYER_MULTIDEVICE;
            if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                ProfileActive = 0;
                setCountDown(30);
            }
        }

    }

    public Profile getWinnerProfile() {
        int length = getGameBoardArray().length;
        Profile winnerProfile = null;
        int points = 0;

        if (gameMode != GameMode.SINGLEPLAYER) {
            for (Profile profile : players) {
                if (profile.getPoints() > points) {
                    points = profile.getPoints();
                    winnerProfile = profile;
                } else if (profile.getPoints() == points) {
                    //Tie, do something
                }
            }
        } else
            winnerProfile = players.get(0);

        return winnerProfile;
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


    class LooperThread extends Thread {

        public void run() {
            Looper.prepare();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    grid.makeAnimation();
                }
            }, 100);
            Looper.loop();
        }
    }

}

