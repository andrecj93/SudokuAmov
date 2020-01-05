package com.example.sudokuamov.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sudokuamov.R;
import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;

import java.io.File;
import java.util.Locale;


public class PlayerView {
    private TextView usernameView;
    private TextView pointsView;
    private TextView timeView;
    private ImageView imageView;
    private Context mContext;


    public PlayerView(TextView username, TextView points, TextView time, ImageView imgView, Context context) {
        this.usernameView = username;
        this.pointsView = points;
        this.timeView = time;
        this.imageView = imgView;
        this.mContext = context;
    }


    public void RefreshPlayerView() {
        GameEngine gameEngine = GameEngine.getInstance();
        Profile profile = gameEngine.getActivePalyer();
        Resources res = mContext.getResources();
        String strPlayer = String.format(Locale.getDefault(), "%s: %s",
                res.getString(R.string.player),
                profile.getUsername());
        String strPoints = String.format(Locale.getDefault(), "%s: %d",
                res.getString(R.string.points),
                profile.getPoints());
        String strTime = String.format(Locale.getDefault(), "%s: %d %s",
                res.getString(R.string.time_left_str),
                gameEngine.getCountDown(),
                res.getString(R.string.seconds_min));
        this.usernameView.setText(strPlayer);
        this.pointsView.setText(strPoints);
        this.timeView.setText(strTime);

        File f = new File(profile.getUserPhotoThumbnailPath());
        if (f.exists()) {
            Bitmap photo = BitmapFactory.decodeFile(profile.getUserPhotoThumbnailPath());
            this.imageView.setImageBitmap(photo);
        } else {
            this.imageView.setImageResource(R.drawable.friend_icon);
        }
    }
}
