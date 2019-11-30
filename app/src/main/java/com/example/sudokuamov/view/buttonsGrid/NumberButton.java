package com.example.sudokuamov.view.buttonsGrid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.sudokuamov.game.GameEngine;

public class NumberButton extends AppCompatButton implements View.OnClickListener
{
    private int number;

    public NumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        GameEngine.getInstance().setNumber(number);
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
