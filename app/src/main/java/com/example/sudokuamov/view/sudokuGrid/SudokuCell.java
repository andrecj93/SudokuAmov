package com.example.sudokuamov.view.sudokuGrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class SudokuCell extends BaseSudokuCell {
    private Paint mPaint;
    private Rect bounds;
    private Context context;

    public SudokuCell(Context context) {
        super(context);
        this.context = context;
        mPaint = new Paint();
        bounds = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLines(canvas);

        drawNumber(canvas);
    }

    private void drawNumber(Canvas canvas) {
        if (isRed())
            mPaint.setColor(Color.RED);
        else
            mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(60);
        mPaint.setStyle(Paint.Style.FILL);

        //Rect bounds = new Rect();
        mPaint.getTextBounds(String.valueOf(getValue()), 0, String.valueOf(getValue()).length(), bounds);

        if (getValue() != 0) {
            canvas.drawText(String.valueOf(getValue()), (getWidth() - bounds.width()) / 2, (getHeight() + bounds.height()) / 2, mPaint);
        }
    }


    private void drawLines(Canvas canvas) {
        int strokePlayerMove = 7;
        final int width = getWidth();
        final int height = getHeight();
        int colorNotModifiableCell = Color.DKGRAY;
        int colorModifiableCell = Color.BLUE;
        int colorRegions = Color.rgb(0, 0, 0);
        /*
         * First paint for the black  and then paint the numbers to fill with blue
         * */
        mPaint.setStyle(Paint.Style.STROKE);
        if (getValue() != 0) {
            if (!getIsModifiable())
            {
                mPaint.setStrokeWidth(3);
                mPaint.setColor(colorNotModifiableCell);
            }
            else {
                mPaint.setStrokeWidth(strokePlayerMove);
                mPaint.setColor(colorModifiableCell);
            }
        } else {
            mPaint.setColor(colorModifiableCell);
            mPaint.setStrokeWidth(strokePlayerMove);
        }

        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);


        //Painting the regions
        mPaint.setColor(colorRegions);
        mPaint.setStrokeWidth(11);
        if (getCellPosX() % 3 == 0) {
            //Paints the left side of the cells
            canvas.drawRect(0, 0, 0, height, mPaint);
        } else {
            if (getCellPosX() == 8) {
                //Paint the last right side of the cell
                canvas.drawRect(width, height, width, 0, mPaint);
            }
        }

        if (getCellPosY() % 3 == 0) {
            //Paint the bottom side
            canvas.drawRect(0, 0, width, 0, mPaint);
        } else{
            if (getCellPosY() == 8)
                //Paint the last bottom side
                canvas.drawRect(width, height, 0, height, mPaint);
        }

        //Painting the selected cell
        if (this.isSelected())
        {
            mPaint.setColor(Color.LTGRAY);
            mPaint.setStyle(Paint.Style.FILL);


            canvas.drawRect(0, 0,width, height, mPaint);
        }
    }
}
