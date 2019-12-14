package com.example.sudokuamov.game.helpers;

public enum Levels {
    EASY,
    MEDIUM,
    HARD;


    public static Levels fromInteger(int x) {
        switch (x) {
            case 0:
                return EASY;
            case 1:
                return MEDIUM;
            default:
                return HARD;
        }
    }

    public static int intToEnum(Levels x) {
        switch (x) {
            case EASY:
                return 0;
            case MEDIUM:
                return 1;
            default:
                return 2;
        }
    }
}

