package com.example.sudokuamov.view;

import android.widget.TextView;


public class PlayerView {
    private TextView usernameView;
    private TextView pointsView;
    private TextView timeView;

    private int timer = 30;
    private String name;
    private int points;


    public PlayerView(TextView username, TextView points, TextView time) {
        this.usernameView = username;
        this.pointsView = points;
        this.timeView = time;
    }


    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int pints) {
        this.points = pints;
    }


    public void RefreshPlayerView() {
        this.usernameView.setText("Player : " + name);
        this.pointsView.setText("Points : " + points);
        this.timeView.setText("Time Left : " + timer + " sec");
    }
}
