package com.example.sudokuamov.sockets;

import com.example.sudokuamov.game.GameCell;
import com.example.sudokuamov.game.Profile;
import com.google.gson.Gson;

import java.util.List;

public class DataExchange {

    Operation operation;
    List<GameCell> gameBoard;
    GamePlay gamePlay;
    List<Profile> profileList;
    String playerName;
    int profileActive;
    int newTime;

    public DataExchange(Operation operation, List<GameCell> gameBoard, GamePlay gamePlay, List<Profile> profileList, int profileActive, String playerName, int time) {
        this.operation = operation;
        this.gameBoard = gameBoard;
        this.gamePlay = gamePlay;
        this.profileList = profileList;
        this.profileActive = profileActive;
        this.playerName = playerName;
        this.newTime = time;
    }

    public String getJSON() {
        return new Gson().toJson(this);
    }

    public enum Operation {
        CheckNewGamePlay,
        GetImage,
        SetTime,
        ChangePlayer,
        setPlayerList,
        setPlayerName,
        SetGameBoar
    }

    class GamePlay {
        public int newNum;
        public int poisitionX;
        public int positionY;

        public GamePlay(int newNum, int poisitionX, int positionY) {
            this.newNum = newNum;
            this.poisitionX = poisitionX;
            this.positionY = positionY;
        }
    }
}
