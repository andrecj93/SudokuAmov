package com.example.sudokuamov.view;

import android.widget.TextView;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;


public class PlayerView {
    private TextView usernameView;
    private TextView pointsView;
    private TextView timeView;

    public PlayerView(TextView username, TextView points, TextView time) {
        this.usernameView = username;
        this.pointsView = points;
        this.timeView = time;
    }


    public void RefreshPlayerView() {
        GameEngine gameEngine = GameEngine.getInstance();
        Profile profile = gameEngine.getActivePalyer();


        this.usernameView.setText("Player : " + profile.getUsername());
        this.pointsView.setText("Points : " + profile.getPoints());
        this.timeView.setText("Time Left : " + gameEngine.getCountDown() + " sec");
    }
}
