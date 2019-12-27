package com.example.sudokuamov;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    public static String showAlertDialog(Context context, String title, String message, String positiveButton, String negativeButton) {
        final String[] valueFromTxt = {""};
        final EditText taskEditText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(taskEditText)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        valueFromTxt[0] = String.valueOf(taskEditText.getText());

                    }
                })
                .setNegativeButton(negativeButton, null)
                .create();
        dialog.show();
        return valueFromTxt[0];

    }

    @Override
    public void onClick(View view) {
        final Intent intent = HelperMethods.makeIntentForUserNameAndPhoto(new String[]{userName, userPhoto, userPhotoThumb},
                this, LevelsActivity.class);
        switch (view.getId()) {
            case R.id.buttonSinglePlayer: {
                intent.putExtra("Mode", "singleplayer");
                startActivity(intent);
                break;
            }
            case R.id.buttonTwoPlayer: {
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("1v1 Your Friend")
                        .setMessage("Your friend's name")
                        .setView(taskEditText)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String value = String.valueOf(taskEditText.getText());
                                intent.putExtra("myFriendsName", value);
                                intent.putExtra("Mode", "multiplayer");
                                startActivity(intent);

                                //finish(); corre mal quando fazes back
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

                break;
            }
            case R.id.buttonNetworkGame: {
                intent.putExtra("Mode", "networkgame");

                //TODO

                //Maybe change this after and make a new intent for a different for a list of active active servers with the option to create a new server.
                startActivity(intent);
                break;
            }
            default:

                break;
        }
    }
}
