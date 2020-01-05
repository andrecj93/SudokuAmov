package com.example.sudokuamov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokuamov.activities.helpers.HelperMethods;

import java.util.Locale;

public class AfterGameActivity extends AppCompatActivity implements View.OnClickListener {

    String userWinner, userPhotoThumbnail;
    int points = 0;
    Button continueBtn;
    TextView tvPoints, tvUserName;

    String userName, userPhoto, userPhotoThumb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_game);

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(this);

        tvPoints = findViewById(R.id.txtPoints);
        tvUserName = findViewById(R.id.txtWinner);

        Intent intent = getIntent();
        userWinner = intent.getStringExtra("nickName");
        userPhotoThumbnail = intent.getStringExtra("userPhotoThumbPath");
        points = intent.getIntExtra("winnerPoints", 0);

        String strPoints = String.format(Locale.getDefault(), "%s %d", getString(R.string.points), points);
        tvUserName.setText(userWinner);
        tvPoints.setText(strPoints);


        userName = intent.getStringExtra("defaultUserName");
        userPhoto = intent.getStringExtra("defaultUserPhoto");
        userPhotoThumb = intent.getStringExtra("defaultUserPhotoThumbnail");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueBtn) {

            final Intent intent = HelperMethods.makeIntentForUserNameAndPhoto(new String[]{userName, userPhoto, userPhotoThumb},
                    this, MenuActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
