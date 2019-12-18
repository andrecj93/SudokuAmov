package com.example.sudokuamov.view.sudokuGrid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.helpers.Configurations;

public class SudokuGridView extends GridView
{
    private final Context context;


    public SudokuGridView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        SudokuGridViewAdapter gridViewAdapter = new SudokuGridViewAdapter(context);

        setAdapter(gridViewAdapter);

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int x = position % 9;
                int y = position / 9;

                //Toast.makeText(context, "Selected item - x: " + x + " y: "+ y, Toast.LENGTH_SHORT).show();

                GameEngine.getInstance().setSelectedPositions(x,y);
                GameEngine.getInstance().getGrid().getItem(x,y).setSelected(true);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Passing the width on both side because its a square
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    //Adapter for the grid view
    class SudokuGridViewAdapter extends BaseAdapter {
        private Context context;

        public SudokuGridViewAdapter(Context context){
            this.context = context;
            //setBackgroundResource(R.drawable.cell_shape);
        }

        @Override
        public int getCount() {
            return Configurations.GRID9x9;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            GameEngine.getInstance().getGrid().getItem(position).setCoord(position);
            return GameEngine.getInstance().getGrid().getItem(position);
        }
    }

}
