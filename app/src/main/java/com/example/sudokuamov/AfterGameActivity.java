package com.example.sudokuamov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class AfterGameActivity extends AppCompatActivity implements View.OnClickListener {

    String userWinner, points, userPhotoThumbnail;
    Button continueBtn;
    TextView tvPoints, tvUserName;

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
        points = intent.getStringExtra("winnerPoints");

        tvPoints.setText(userWinner);
        String strPoints = String.format(Locale.getDefault(), "%d %s", R.string.points, points);
        tvUserName.setText(strPoints);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueBtn) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }
    }
}
