package com.example.sudokuamov;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;
import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.GameMode;
import com.example.sudokuamov.game.helpers.Levels;
import com.example.sudokuamov.sockets.SocketConnector;
import com.example.sudokuamov.view.GameGrid;
import com.example.sudokuamov.view.PlayerView;

import java.io.File;

public class GameActivity extends AppCompatActivity {
    String mode;
    int difficulty = 0;
    //Singleton to run for the entire game
    private GameEngine game;
    private GameGrid gameGrid;
    private ImageView imgUser;
    //The user data from the intent
    private String userName, userPhoto, userPhotoThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        game = GameEngine.getInstance();

        imgUser = findViewById(R.id.imageView);

        //activity is being created for the first time
        if (savedInstanceState == null) {
            setUserPhotoAndName();
            Intent intent = getIntent();
            //Single player or two players from menu activity
            mode = intent.getStringExtra("Mode");
            //Intent for difficulty passed from the level activity
            difficulty = intent.getIntExtra("Difficulty", 0);
            initBoard(true);
        } else {

            initBoard(false);
        }

        //disableViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void disableViews() {
        if (game.getGameMode().equals(GameMode.SINGLEPLAYER))
            findViewById(R.id.time).setVisibility(View.GONE);
    }

    private void initBoard(boolean start) {
        TextView playerNameDisplay = findViewById(R.id.username);
        TextView timerDisplay = findViewById(R.id.time);
        TextView pointsDisplay = findViewById(R.id.points);

        final PlayerView playerView = new PlayerView(playerNameDisplay, pointsDisplay, timerDisplay, imgUser, this);

        gameGrid = new GameGrid(this, game, playerView, findViewById(R.id.sudokuGridView));

        if (start) {
            game.setLevels(Levels.fromInteger(difficulty));

            game.setGameMode(mode, false);

            setupPlayers();

            setObserver();
            setObserverGrid();

            if (mode.equals(GameMode.MULTIPLAYER_MULTIDEVICE.toString())) {
                if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                    game.createSudokuGrid(gameGrid);
                } else {
                    game.createCleanSudokuGrid(gameGrid);
                }

                SocketConnector.getInstance().startReceivingData();
            } else {
                game.createSudokuGrid(gameGrid);
            }

        } else {
            mode = game.getGameMode().toString();

            /*userName = game.getActivePalyer().getUsername();
            userPhotoThumb = game.getActivePalyer().getUserPhotoThumbnailPath();
            userPhoto = game.getActivePalyer().getUserPhotoPath();*/

            setObserver();
            setObserverGrid();

            game.redrawGame(gameGrid);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        game.removeObserver(this);
        game.removeObserverGrid(this);
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
        Profile profile = new Profile(userName, userPhoto, userPhotoThumb, "SERVER");

        game.addPlayerToList(profile);
        game.setMyProfile(profile);


        if (mode.equals(GameMode.MULTIPLAYER_SAMEDEVICE.toString())) {
            //here we are also getting the new user for that came from the intent
            String userFriend = getIntent().getStringExtra("myFriendsName") == null ?
                    "My Friend" :
                    getIntent().getStringExtra("myFriendsName");

            assert userFriend != null;

            if (!userFriend.isEmpty())
                game.addPlayerToList(new Profile(userFriend));
            else
                game.addPlayerToList(new Profile("My Friend"));
        }

        if (mode.equals(GameMode.MULTIPLAYER_MULTIDEVICE.toString())) {
            final SocketConnector socketConnector = SocketConnector.getInstance();

            socketConnector.startReceivingData();

            socketConnector.actualizaPlayerList(game);

            if (SocketConnector.getAmIserver() == SocketConnector.SERVER) {
                game.setMyProfile(profile);
            } else {
                profile.setIp(SocketConnector.getLocalIpAddress());

                game.setMyProfile(profile);
            }
        }
    }

    private void setUserPhotoAndName() {
        TextView txtUser = findViewById(R.id.username);

        Intent intent = getIntent();
        userName = intent.getStringExtra("nickName");
        userPhoto = intent.getStringExtra("userPhotoPath");
        userPhotoThumb = intent.getStringExtra("userPhotoThumbPath");
        game.setThisIsMe(new Profile(userName, userPhoto, userPhotoThumb, null));

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

    private void printSudoku(int[][] sudokuGrid) {
        System.out.println("----Sudoku Grid----");
        for (int y = 0; y < Configurations.GRID9; y++) {
            for (int x = 0; x < Configurations.GRID9; x++) {
                System.out.print(sudokuGrid[x][y] + "|");
            }
            System.out.println();
        }
    }

}
