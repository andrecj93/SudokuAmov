package com.example.sudokuamov.view.sudokuGrid;

import android.content.Context;
import android.view.View;

public class BaseSudokuCell extends View {

    private int value;
    private boolean isModifiable = true;
    private int cellPosX, cellPosY;

    public BaseSudokuCell(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setValue(int value){
        if (isModifiable)
            this.value = value;

        invalidate();
    }

    public int getValue(){
        return value;
    }

    public void setInitValue(int value){
        this.value = value;
        invalidate();
    }

    public void setNotModifiable() {
        isModifiable = false;
    }

    public boolean getIsModifiable(){
        return isModifiable;
    }

    public void setCoord(int position) {
        int x = position % 9;
        int y = position / 9;
        this.cellPosX = x;
        this.cellPosY = y;
    }

    public int getCellPosX() {
        return cellPosX;
    }

    public int getCellPosY() {
        return cellPosY;
    }
}