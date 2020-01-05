package com.example.sudokuamov.view.buttonsGrid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.sudokuamov.game.GameEngine;

import androidx.appcompat.widget.AppCompatButton;

public class NumberButton extends AppCompatButton implements View.OnClickListener {
    private int number;

    public NumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (number >= 0)
            GameEngine.getInstance().setNumber(number, false);
        else
            GameEngine.getInstance().GetPosssibleNumber();
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
