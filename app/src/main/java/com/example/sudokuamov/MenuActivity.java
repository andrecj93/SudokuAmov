package com.example.sudokuamov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setButtonActions();
    }

    private void setButtonActions() {
        Button mClickButton1 = findViewById(R.id.buttonSinglePlayer);
        mClickButton1.setOnClickListener(this);
        Button mClickButton2 = findViewById(R.id.buttonTwoPlayer);
        mClickButton2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSinglePlayer: {
                Intent intent = new Intent(this, SingleplayerActivity.class);
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
