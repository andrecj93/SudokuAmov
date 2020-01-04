package com.example.sudokuamov.game;

public class GameCell {
    private int value;
    private boolean changeable;

    public GameCell(int value, boolean changeable) {
        this.value = value;
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

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

}
