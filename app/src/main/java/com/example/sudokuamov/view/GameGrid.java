package com.example.sudokuamov.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.sudokuamov.AfterGameActivity;
import com.example.sudokuamov.activities.helpers.HelperMethods;
import com.example.sudokuamov.game.GameCell;
import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameInfoHistory;
import com.example.sudokuamov.view.sudokuGrid.SudokuCell;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

public class GameGrid {

    private static final Object mutex = new Object();
    final PlayerView playerView;
    private final View sudokuGridView;
    private SudokuCell[][] sudokuCells = new SudokuCell[Configurations.GRID9][Configurations.GRID9];
    private Context context;
    private GameEngine gameEngine = null;


    public GameGrid(Context context, GameEngine gameEngine, PlayerView playerView, View view) {
        this.context = context;
        this.gameEngine = gameEngine;
        this.playerView = playerView;
        this.sudokuGridView = view;

        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                sudokuCells[x][y] = new SudokuCell(context);
            }
        }
    }

    public final PlayerView getPlayerView() {
        synchronized (mutex) {
            return playerView;
        }
    }

    public void setSudokuCellsByIntArray() {
        synchronized (mutex) {
            GameEngine g = GameEngine.getInstance();

            for (int x = 0; x < Configurations.GRID9; x++) {
                for (int y = 0; y < Configurations.GRID9; y++) {
                    GameCell gameCell = g.getGameCell(x, y);

                    sudokuCells[x][y].setInitValue(gameCell.getValue());

                    if (!gameCell.isChangeable())
                        sudokuCells[x][y].setNotModifiable();
                }
            }
        }
    }


    public SudokuCell[][] getSudokuCells() {
        return sudokuCells;
    }

    public SudokuCell getItem(int x, int y) {
        return sudokuCells[x][y];
    }

    public void getHelp(int x, int y) {
        for (int i = 1; i < 10; i++)
            if (gameEngine.checkNewNumberFill(x, y, i)) {
                Toast.makeText(context, "Um Numero possivel é [" + i + "]", Toast.LENGTH_LONG).show();
                break;
            }
    }

    public void makeAnimation() {
        synchronized (mutex) {
            sudokuGridView.animate().rotationBy(360);
        }
    }

    public SudokuCell getItem(int position) {
        int x = position % 9;
        int y = position / 9;

        return sudokuCells[x][y];
    }

    public boolean setItem(final int x, final int y, final int number) {
        if (sudokuCells[x][y].isRed()) {
            Toast.makeText(context, "Wait 3 sec After a Fail.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (number == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sudokuCells[x][y].setValue(number);
                }
            }, 0);

            gameEngine.getGameCell(x, y).setValue(0);

            return false;
        }


        if (gameEngine.checkNewNumberFill(x, y, number)) {

            gameEngine.getGameCell(x, y).setValue(number);


            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sudokuCells[x][y].setValue(number);
                }
            }, 0);


            gameEngine.setActivePlayerPoints();
            return true;
        } else {
            if (sudokuCells[x][y].getIsModifiable())
                setCellRed(x, y, number);

            return false;
        }
    }


    public void setCellRed(final int x, final int y, final int number) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                sudokuCells[x][y].setValue(number);

                sudokuCells[x][y].setRed();
            }
        }, 0);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                sudokuCells[x][y].setValue(0);
            }
        }, 3000);
    }

    public int[][] getSudokuCellsInteger() {
        int[][] sudGrid = new int[Configurations.GRID9][Configurations.GRID9];

        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                sudGrid[x][y] = getItem(x, y).getValue();
            }
        }


        return sudGrid;
    }


    public boolean isAllCellsFilled() {
        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int y = 0; y < Configurations.GRID9; y++) {
                if (gameEngine.getGameCell(x, y).getValue() <= 0)
                    //if (sudokuCells[x][y].getValue() <= 0)
                    return false;
            }
        }
        return true;
    }

    public void checkGame() {
        //Check end game success

        //If game is not successful and all numbers are filled
        if (isAllCellsFilled()) {
            Profile checkWinner = gameEngine.getWinnerProfile();
            if (checkWinner != null) {
                Intent intent = new Intent(HelperMethods.makeIntentForUserNameAndPhoto(
                        new String[]{
                                checkWinner.getUsername(),
                                checkWinner.getUserPhotoPath(),
                                checkWinner.getUserPhotoThumbnailPath()
                        }, context, AfterGameActivity.class));
                intent.putExtra("winnerPoints", checkWinner.getPoints());

                intent.putExtra("defaultUserName", gameEngine.getThisIsMe().getUsername());
                intent.putExtra("defaultUserPhoto", gameEngine.getThisIsMe().getUserPhotoPath());
                intent.putExtra("defaultUserPhotoThumbnail", gameEngine.getThisIsMe().getUserPhotoThumbnailPath());

                saveGameInfoInHistory();

                context.startActivity(intent);
                //startActivity(intent);
                //finish();
                Toast.makeText(context, "Well Done! That is the correct solution.", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(context, "Try again! That is not a correct solution.", Toast.LENGTH_LONG).show();
        }
    }


    private void saveGameInfoInHistory() {
        GameInfoHistory gameInfoHistory = new GameInfoHistory(
                gameEngine.getWinnerProfile(),
                gameEngine.getAllPlayers(),
                gameEngine.getGameMode().name(),
                gameEngine.getLevels().name());

        String pathToSaveHistory = context.getExternalFilesDir(null) + "/" + "gameHistory.json";

        Gson gson = new Gson();
        List<GameInfoHistory> historyList = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToSaveHistory));
            Type type = new TypeToken<List<GameInfoHistory>>() {
            }.getType();

            historyList = gson.fromJson(bufferedReader, type);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        historyList.add(gameInfoHistory);


        //Write new json class with the new history
        try (Writer writer = new FileWriter(pathToSaveHistory)) {
            Gson gson_ = new GsonBuilder().setPrettyPrinting().create();
            gson_.toJson(historyList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
