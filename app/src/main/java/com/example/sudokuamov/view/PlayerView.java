package com.example.sudokuamov.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sudokuamov.R;
import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;

import java.io.File;


public class PlayerView {
    private TextView usernameView;
    private TextView pointsView;
    private TextView timeView;
    private ImageView imageView;

    public PlayerView(TextView username, TextView points, TextView time, ImageView imgView) {
        this.usernameView = username;
        this.pointsView = points;
        this.timeView = time;
        this.imageView = imgView;
    }


    public void RefreshPlayerView() {
        GameEngine gameEngine = GameEngine.getInstance();
        Profile profile = gameEngine.getActivePalyer();

        this.usernameView.setText("Player : " + profile.getUsername());
        this.pointsView.setText("Points : " + profile.getPoints());
        this.timeView.setText("Time Left : " + gameEngine.getCountDown() + " sec");

        File f = new File(profile.getUserPhotoThumbnailPath());
        if (f.exists()) {
            Bitmap photo = BitmapFactory.decodeFile(profile.getUserPhotoThumbnailPath());
            this.imageView.setImageBitmap(photo);
        } else {
            this.imageView.setImageResource(R.drawable.friend_icon);
        }
    }
}
