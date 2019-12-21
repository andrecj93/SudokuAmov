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

import java.io.File;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    String userName, userPhoto, userPhotoThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setButtonActions();

        setUserPhotoAndName();
    }

    private void setButtonActions() {
        Button mClickButton1 = findViewById(R.id.buttonSinglePlayer);
        mClickButton1.setOnClickListener(this);
        Button mClickButton2 = findViewById(R.id.buttonTwoPlayer);
        mClickButton2.setOnClickListener(this);
    }

    private void setUserPhotoAndName() {
        ImageView imgUser = findViewById(R.id.imageViewUser);
        TextView txtUser = findViewById(R.id.textViewWelcomeUser);

        Intent intent = getIntent();
        userName = intent.getStringExtra("nickName");
        userPhoto = intent.getStringExtra("userPhotoPath");
        userPhotoThumb = intent.getStringExtra("userPhotoThumbPath");

        if (userName.equals(""))
            userName = "user1";


        String helloPhrase = String.format("%s, %s!", getString(R.string.hello), userName);
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
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userReallyWantsPicture", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSinglePlayer: {
                Intent intent = HelperMethods.makeIntentForUserNameAndPhoto(new String[]{userName, userPhoto, userPhotoThumb},
                        this, LevelsActivity.class);
                //new Intent(this, LevelsActivity.class);
                intent.putExtra("Mode", "singleplayer");

                startActivity(intent);
                break;
            }
            case R.id.buttonTwoPlayer: {
                // do something for button 2 click
                break;
            }
            default:

                break;
        }
    }
}
