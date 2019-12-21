package com.example.sudokuamov.game.helpers;

public enum Levels {
    VERYEASY,
    EASY,
    MEDIUM,
    HARD,
    VERYHARD;


    public static Levels fromInteger(int x) {
        switch (x) {
            case 0:
                return VERYEASY;
            case 1:
                return EASY;
            case 2:
                return MEDIUM;
            case 3:
                return HARD;
            default:
                return VERYHARD;
        }
    }

    public static int intToEnum(Levels x) {
        switch (x) {
            case VERYEASY:
                return 0;
            case EASY:
                return 1;
            case MEDIUM:
                return 2;
            case HARD:
                return 3;
            default:
                return 4;
        }
    }
}

