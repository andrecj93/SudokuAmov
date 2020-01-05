package com.example.sudokuamov.game.helpers;

import com.example.sudokuamov.game.Profile;

import java.util.List;

public class GameInfoHistory {
    private Profile winnerProfile;
    private List<Profile> playerList;
    private String gameMode;
    private String level;


    public GameInfoHistory(Profile winnerProfile, List<Profile> profiles, String gameMode, String level) {
        this.winnerProfile = winnerProfile;
        this.playerList = profiles;
        this.gameMode = gameMode;
        this.level = level;
    }

    public Profile getWinnerProfile() {
        return winnerProfile;
    }

    public List<Profile> getPlayerList() {
        return playerList;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getLevel() {
        return level;
    }
}
