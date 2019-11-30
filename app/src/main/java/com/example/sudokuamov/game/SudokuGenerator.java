package com.example.sudokuamov.game;

import com.example.sudokuamov.game.helpers.Configurations;
import com.example.sudokuamov.game.helpers.Levels;

import java.util.ArrayList;
import java.util.Random;

public class SudokuGenerator {
    private static SudokuGenerator instance;
    private ArrayList<ArrayList<Integer>> Available = new ArrayList<>();
    private Random random = new Random();

    private SudokuGenerator() {
    }

    public static SudokuGenerator getInstance() {
        if (instance == null)
            instance = new SudokuGenerator();

        return instance;
    }

    //Gerar uma grelha com 9*9
    public int[][] generateGrid() {
        int[][] SudokuGrid = new int[Configurations.GRID9][Configurations.GRID9];

        int currentPos = 0;

        clearGrid(SudokuGrid);

        while (currentPos < Configurations.GRID9x9) {
            //If not zero, good to go, and there is available space to fill in number
            if (Available.get(currentPos).size() != 0) {
                //Randomize a position to choose the number in the grid
                //Get a random number from zero to the size of Available.get(currentPos)
                int i = random.nextInt(Available.get(currentPos).size());

                int number = Available.get(currentPos).get(i);

                if (!checkConflict(SudokuGrid, currentPos, number)) {
                    int xPos = currentPos % 9;
                    int yPos = currentPos / 9;

                    //Add the number to the grid
                    SudokuGrid[xPos][yPos] = number;
                    //Remove the number from available array list;
                    Available.get(currentPos).remove(i);
                    //Step forward
                    currentPos++;
                } else {
                    //If there is a conflict just remove the number from the available array list
                    Available.get(currentPos).remove(i);
                }

            } else {
                for (int i = 1; i <= Configurations.GRID9; i++) {
                    //Adding all number to the available list
                    Available.get(currentPos).add(i);
                }
                //start back
                currentPos--;
            }
        }

        return SudokuGrid;
    }


    public int[][] removeValues(int[][] sudokuGrid, Levels levels) {
        int i = 0;

        int diff = getLevel(levels);

        int[][] tmpGrid = sudokuGrid;

        int maxHiddenNumberPerRegion = 7;
        int countHiddenPerRegion = 0;

        //TODO GARANTIR QUE APENAS SAO REMOVIDOS ENTRE 2 a 7 valores em cada região/sub região

        //Cycle to remove X elements based on levels
        while (i < diff) {
            int x = random.nextInt(Configurations.GRID9);
            int y = random.nextInt(Configurations.GRID9);

            if (sudokuGrid[x][y] != 0) {
                sudokuGrid[x][y] = 0;
                i++;
            }
        }

        return sudokuGrid;
    }

    public void clearGrid(int[][] sudokuGrid) {
        Available.clear();
        for (int y = 0; y < Configurations.GRID9; y++) {
            for (int x = 0; x < Configurations.GRID9; x++) {
                sudokuGrid[x][y] = -1;
            }
        }

        for (int x = 0; x < Configurations.GRID9x9; x++) {
            Available.add(new ArrayList<Integer>());
            for (int i = 1; i <= Configurations.GRID9; i++) {
                Available.get(x).add(i);
            }
        }
    }

    //Check conflicted number in rows or cols
    public boolean checkConflict(int[][] sudokuGrid, int currentPosition, final int number) {
        int xPos = currentPosition % Configurations.GRID9;
        int yPos = currentPosition / Configurations.GRID9;

        return checkHorizontalConflict(sudokuGrid, xPos, yPos, number) || checkVerticalConflict(sudokuGrid, xPos, yPos, number) || checkRegionConflict(sudokuGrid, xPos, yPos, number);
    }


    /**
     * @return Return true if there is a conflict
     */
    public boolean checkHorizontalConflict(final int[][] sudokuGrid, final int xPos, final int yPos, final int number) {
        for (int x = xPos - 1; x >= 0; x--) {
            if (number == sudokuGrid[x][yPos])
                return true;
        }

        return false;
    }

    public boolean checkVerticalConflict(final int[][] sudokuGrid, final int xPos, final int yPos, final int number) {
        for (int y = yPos - 1; y >= 0; y--) {
            if (number == sudokuGrid[xPos][y])
                return true;
        }

        return false;
    }

    public boolean checkRegionConflict(final int[][] sudokuGrid, final int xPos, final int yPos, final int number) {
        int xRegion = xPos / 3;
        int yRegion = yPos / 3;

        for (int x = xRegion * 3; x < xRegion * 3 + 3; x++) {
            for (int y = yRegion * 3; y < yRegion * 3 + 3; y++) {
                if ((x != xPos || y != yPos) && number == sudokuGrid[x][y])
                    return true;
            }
        }

        return false;
    }

    public int getLevel(Levels levels){
        switch (levels) {
            case EASY:
                return Configurations.EASY;
            case MEDIUM:
                return Configurations.MEDIUM;
            default:
                return Configurations.HARD;
        }
    }

}
