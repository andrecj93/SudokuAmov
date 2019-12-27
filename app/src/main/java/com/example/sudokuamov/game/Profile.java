package com.example.sudokuamov.game;

public class Profile {

    private String username;
    private String userPhotoPath;
    private String userPhotoThumbnailPath;
    private int points;

    public Profile(String username, String userPhotoPath, String userPhotoThumbnailPath) {
        this.username = username;
        this.userPhotoPath = userPhotoPath;
        this.userPhotoThumbnailPath = userPhotoThumbnailPath;
    }

    public Profile(String username) {
        this(username, "", "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhotoPath() {
        return userPhotoPath;
    }

    public void setUserPhotoPath(String userPhotoPath) {
        this.userPhotoPath = userPhotoPath;
    }

    public String getUserPhotoThumbnailPath() {
        return userPhotoThumbnailPath;
    }

    public void setUserPhotoThumbnailPath(String userPhotoThumbnailPath) {
        this.userPhotoThumbnailPath = userPhotoThumbnailPath;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        if (points == 0)
            this.points++;
        else
            this.points = points;
    }
}
