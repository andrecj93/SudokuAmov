package com.example.sudokuamov;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameMode;
import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.view.GameGrid;
import com.example.sudokuamov.view.PlayerView;

import java.io.File;

public class SingleplayerActivity extends AppCompatActivity {
    //Singleton to run for the entire game
    private GameEngine game;
    private GameGrid gameGrid;

    //The user data from the intent
    private String userName, userPhoto, userPhotoThumb;

    String mode;
    int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        game = GameEngine.getInstance();

        //activity is being created for the first time
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            //Single player or two players from menu activity
            mode = intent.getStringExtra("Mode");//TODO CHANGE THIS TO ENUM
            //Intent for difficulty passed from the level activity
            difficulty = intent.getIntExtra("Difficulty", 0);
            setUserPhotoAndName();
            initBoard(true);

        } else {
            userName = game.getActivePalyer().getUsername();
            userPhoto = game.getActivePalyer().getUserPhotoPath();
            userPhotoThumb = game.getActivePalyer().getUserPhotoThumbnailPath();

            initBoard(false);
        }

        disableViews();
    }

    @Override
    public void onBackPressed() {
        game.resetGame(this);
        super.onBackPressed();
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        //game = GameEngine.getInstance();

        //initBoard(false);
    }*/

    private void disableViews() {
        if (game.getGameMode().equals(GameMode.SINGLEPLAYER))
            findViewById(R.id.time).setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("nickName", userName);
        outState.putString("userPhotoPath", userPhoto);
        outState.putString("userPhotoThumbPath", userPhotoThumb);

        super.onSaveInstanceState(outState);
    }

    private void initBoard(boolean start) {
        TextView playerNameDisplay = findViewById(R.id.username);
        TextView timerDisplay = findViewById(R.id.time);
        TextView pointsDisplay = findViewById(R.id.points);

        final PlayerView playerView = new PlayerView(playerNameDisplay, pointsDisplay, timerDisplay);

        gameGrid = new GameGrid(this, game, playerView);


        if (start) {
            game.setLevels(Levels.fromInteger(difficulty));
            game.createSudokuGrid(gameGrid);
            game.setGameMode(mode);
            setupPlayers();

            setObserver();
            setObserverGrid();
        } else {
            game.redrawGame(gameGrid);
        }


    }

    private void setObserver() {
        game.setObserver(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                gameGrid.getPlayerView().RefreshPlayerView();
            }
        });
    }

    private void setObserverGrid() {
        game.setObserverGrid(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                gameGrid.setSudokuCellsByIntArray();
            }
        });
    }


    private void setupPlayers() {
        game.addPlayerToList(new Profile(userName, userPhoto, userPhotoThumb));
        if (mode.equals("multiplayer")) {
            //here we are also getting the new user for that came from the intent
            String userFriend = getIntent().getStringExtra("myFriendsName") == null ?
                    "My Friend" :
                    getIntent().getStringExtra("myFriendsName");

            assert userFriend != null;

            if (!userFriend.isEmpty())
                game.addPlayerToList(new Profile(userFriend, null, null));
            else
                game.addPlayerToList(new Profile("My Friend", null, null));
        }
    }

    private void setUserPhotoAndName() {
        ImageView imgUser = findViewById(R.id.imageView);
        TextView txtUser = findViewById(R.id.username);

        Intent intent = getIntent();
        userName = intent.getStringExtra("nickName");
        userPhoto = intent.getStringExtra("userPhotoPath");
        userPhotoThumb = intent.getStringExtra("userPhotoThumbPath");

        if (userName.equals(""))
            userName = "user1";

        String helloPhrase = String.format("%s", userName);
        txtUser.setText(helloPhrase);

        if (userPhotoThumb == null || userPhotoThumb.equals(""))
            userPhotoThumb = getExternalFilesDir(null) + "/userPhoto_thumb.jpg";

        File f = new File(userPhotoThumb);
        if (f.exists()) {
            Bitmap photo = BitmapFactory.decodeFile(userPhotoThumb);
            imgUser.setImageBitmap(photo);
        } else {
            imgUser.setImageResource(R.drawable.userphoto);
        }
    }

    private void printSudoku(int[][] sudokuGrid){
        System.out.println("----Sudoku Grid----");
        for (int y = 0; y < Configurations.GRID9; y++)
        {
            for (int x = 0; x < Configurations.GRID9; x++)
            {
                System.out.print(sudokuGrid[x][y] + "|");
            }
            System.out.println();
        }
    }

}
