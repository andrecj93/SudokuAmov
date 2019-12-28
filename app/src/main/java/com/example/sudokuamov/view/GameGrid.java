package com.example.sudokuamov.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.sudokuamov.AfterGameActivity;
import com.example.sudokuamov.activities.helpers.HelperMethods;
import com.example.sudokuamov.game.GameCell;
import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.view.sudokuGrid.SudokuCell;

import java.util.Timer;
import java.util.TimerTask;

public class GameGrid {

    private SudokuCell[][] sudokuCells = new SudokuCell[Configurations.GRID9][Configurations.GRID9];
    private Context context;
    private GameEngine gameEngine = null;
    private static final Object mutex = new Object();
    final PlayerView playerView;
    private final View sudokuGridView;


    public GameGrid(Context context, GameEngine gameEngine, PlayerView playerView, View view)
    {
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

    public SudokuCell[][] getSudokuCells(){
        return sudokuCells;
    }

    public SudokuCell getItem(int x, int y)
    {
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
        /*ObjectAnimator animation = ObjectAnimator.ofFloat(sudokuGridView, "translationX", 100f);
        animation.setDuration(50);
        animation.start();*/
        sudokuGridView.animate().rotationBy(360);
    }

    public SudokuCell getItem(int position)
    {
        int x = position % 9;
        int y = position / 9;

        return sudokuCells[x][y];
    }


    public void setItem(final int x, final int y, final int number)
    {
        if (sudokuCells[x][y].isRed()) {
            Toast.makeText(context, "Wait 3 sec After a Fail.", Toast.LENGTH_LONG).show();
            return;
        }

        sudokuCells[x][y].setValue(number);

        if (number == 0) {
            gameEngine.getGameCell(x, y).setValue(0);
            return;
        }


        if (gameEngine.checkNewNumberFill(x, y, number)) {
            gameEngine.getGameCell(x, y).setValue(number);
            gameEngine.setActivePlayerPoints();
        } else {

            sudokuCells[x][y].setRed();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    sudokuCells[x][y].setValue(0);
                }
            };
            new Timer().schedule(task, 3000);
        }

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
                if (sudokuCells[x][y].getValue() <= 0)
                    return false;
            }
        }
        return true;
    }

    public void checkGame(){
        //Check end game success

        //If game is not successful and all numbers are filled
        if (isAllCellsFilled()) {
            Profile checkWinner = gameEngine.getEndOffGame();
            if (checkWinner != null) {
                Intent intent = new Intent(HelperMethods.makeIntentForUserNameAndPhoto(
                        new String[]{
                                checkWinner.getUsername(),
                                checkWinner.getUserPhotoPath(),
                                checkWinner.getUserPhotoThumbnailPath()
                        }, context, AfterGameActivity.class));
                intent.putExtra("winnerPoints", checkWinner.getPoints());

                //startActivity(intent);
                //finish();
                //TODO - FINISH THIS, NEED TO PUT THIS ON THE SINGLE PLAYER ACTIVITY
                Toast.makeText(context, "Well Done! That is the correct solution.", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(context, "Try again! That is not a correct solution.", Toast.LENGTH_LONG).show();
        }
    }

}
