package com.example.sudokuamov.game;

import com.example.sudokuamov.game.helpers.Configurations;

public class SudokuChecker {
    //Singleton
    private static SudokuChecker instance;

    private SudokuChecker() {
    }

    public static SudokuChecker getInstance() {
        if (instance == null)
            instance = new SudokuChecker();

        return instance;
    }

    public boolean checkSudoku(int[][] sudokuGrid) {
        return (checkHorizontal(sudokuGrid) || checkVertical(sudokuGrid) || checkRegions(sudokuGrid));
    }


    private boolean checkHorizontal(int[][] sudokuGrid) {
        for (int y = 0; y < Configurations.GRID9; y++) {
            for (int xPos = 0; xPos < Configurations.GRID9; xPos++) {
                if (sudokuGrid[xPos][y] == 0) {
                    return false;
                }

                for (int x = xPos + 1; x < Configurations.GRID9; x++) {
                    //Checking if there is the same number on the same line or a zero (default empty value)
                    if (sudokuGrid[xPos][y] == sudokuGrid[x][y] || sudokuGrid[x][y] == 0)
                        return false;
                }
            }
        }
        return true;
    }

    private boolean checkVertical(int[][] sudokuGrid) {
        for (int x = 0; x < Configurations.GRID9; x++) {
            for (int yPos = 0; yPos < Configurations.GRID9; yPos++) {
                if (sudokuGrid[x][yPos] == 0) {
                    return false;
                }

                for (int y = yPos + 1; y < Configurations.GRID9; y++) {
                    //Checking if there is the same number on the same line or a zero (default empty value)
                    if (sudokuGrid[x][yPos] == sudokuGrid[x][y] || sudokuGrid[x][y] == 0)
                        return false;
                }
            }
        }
        return true;
    }

    private boolean checkRegions(int[][] sudokuGrid) {
        for (int xRegion = 0; xRegion < 3; xRegion++) {
            for (int yRegion = 0; yRegion < 3; yRegion++) {
                if (checkSingleRegion(sudokuGrid, xRegion, yRegion)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkSingleRegion(int[][] sudokuGrid, int xRegion, int yRegion) {
        for (int xPos = xRegion * 3; xPos < xRegion * 3 + 3; xPos++) {
            for (int yPos = yRegion; yPos < yRegion * 3 + 3; yPos++) {
                for (int x = xPos; x < xRegion * 3 + 3; x++) {
                    for (int y = yPos; y < yRegion * 3 + 3; y++) {
                        if (((x != xPos || y != yPos) && sudokuGrid[xPos][yPos] == sudokuGrid[x][y]) || sudokuGrid[x][y] == 0) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
