package com.example.sudokuamov;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokuamov.activities.ProfileActivity;
import com.example.sudokuamov.activities.helpers.HelperMethods;
import com.example.sudokuamov.game.helpers.Levels;

import java.io.File;

public class LevelsActivity extends AppCompatActivity implements View.OnClickListener {

    String mode = "";
    String userName, userPhoto, userPhotoThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        Intent intent = getIntent();
        mode = intent.getStringExtra("Mode");

        setButtonActions();

        setUserPhotoAndName();

    }

    private void setButtonActions() {
        Button veryEasyButton = findViewById(R.id.VeryEasyButton);
        veryEasyButton.setOnClickListener(this);

        Button easyButton = findViewById(R.id.EasyButton);
        easyButton.setOnClickListener(this);

        Button mediumButton = findViewById(R.id.MediumButton);
        mediumButton.setOnClickListener(this);

        Button hardButton = findViewById(R.id.HardButton);
        hardButton.setOnClickListener(this);

        Button veryHardButton = findViewById(R.id.VeryHardButton);
        veryHardButton.setOnClickListener(this);
    }

    private void setUserPhotoAndName() {
        ImageView imgUser = findViewById(R.id.imageViewUser);
        TextView txtUser = findViewById(R.id.textViewUser);

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

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelsActivity.this, ProfileActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userReallyWantsPicture", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = HelperMethods.makeIntentForUserNameAndPhoto(new String[]{userName, userPhoto, userPhotoThumb},
                this, SingleplayerActivity.class);
        intent.putExtra("Mode", mode);

        switch (view.getId()) {
            case R.id.VeryEasyButton: {
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.VERYEASY));
                startActivity(intent);
                break;
            }
            case R.id.EasyButton: {
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.EASY));
                startActivity(intent);
                break;
            }
            case R.id.MediumButton: {
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.MEDIUM));
                startActivity(intent);
                break;
            }
            case R.id.HardButton: {
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.HARD));
                startActivity(intent);
                break;
            }

            case R.id.VeryHardButton: {
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.VERYHARD));
                startActivity(intent);
                break;
            }

            default:
                break;
        }

        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
