package com.example.sudokuamov.game.helpers;

public enum GameMode {
    SINGLEPLAYER,
    MULTIPLAYER_SAMEDEVICE,
    MULTIPLAYER_MULTIDEVICE;

    public static String myToString(GameMode x) {
        switch (x) {
            case MULTIPLAYER_SAMEDEVICE:
                return "multiplayer";
            case MULTIPLAYER_MULTIDEVICE:
                return "multiplayer";
            default:
                return "multiplayer";
        }
    }
}
