package com.example.sudokuamov.game.helpers;

public class GameCell {
    private int value;
    private int solution;
    private String lastPlayerSet;
    private boolean changeable;

    public GameCell(int value, int solution, String lastPlayerSet, boolean changeable) {
        this.value = value;
        this.solution = solution;
        this.lastPlayerSet = lastPlayerSet;
        this.changeable = changeable;
    }

    public GameCell(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValuePlayer(int value, String name) {
        this.value = value;
        lastPlayerSet = name;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    public String getLastPlayerSet() {
        return lastPlayerSet;
    }

    public void setLastPlayerSet(String lastPlayerSet) {
        this.lastPlayerSet = lastPlayerSet;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }
}
