package com.example.sudokuamov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sudokuamov.game.helpers.Levels;

import androidx.appcompat.app.AppCompatActivity;

public class LevelsActivity extends AppCompatActivity implements View.OnClickListener {

    String mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        Intent intent = getIntent();
        mode = intent.getStringExtra("Mode");

        setButtonActions();

    }

    private void setButtonActions() {
        Button easyButton = findViewById(R.id.EasyButton);
        easyButton.setOnClickListener(this);


        Button mediumButton = findViewById(R.id.MediumButton);
        mediumButton.setOnClickListener(this);

        Button hardButton = findViewById(R.id.HardButton);
        hardButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.EasyButton: {
                Intent intent = new Intent(this, SingleplayerActivity.class);
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.EASY));
                intent.putExtra("Mode", mode);
                startActivity(intent);
                break;
            }
            case R.id.MediumButton: {
                Intent intent = new Intent(this, SingleplayerActivity.class);
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.MEDIUM));
                intent.putExtra("Mode", mode);
                startActivity(intent);
                break;
            }
            case R.id.HardButton: {
                Intent intent = new Intent(this, SingleplayerActivity.class);
                intent.putExtra("Difficulty", Levels.intToEnum(Levels.HARD));
                intent.putExtra("Mode", mode);
                startActivity(intent);
                break;
            }
            default:

                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
