package com.example.sudokuamov.game;

import androidx.annotation.Nullable;

public class Profile {

    private String username;
    private String userPhotoPath;
    private String userPhotoThumbnailPath;
    private String ip;
    private int points;

    public Profile(String username, String userPhotoPath, String userPhotoThumbnailPath, String ip) {
        this.username = username;
        this.userPhotoPath = userPhotoPath;
        this.userPhotoThumbnailPath = userPhotoThumbnailPath;
        this.ip = ip;
    }

    public Profile(String username) {
        this(username, "", "", "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;

        if (obj.getClass() != this.getClass())
            return false;

        try {
            Profile prof = (Profile) obj;

            if (prof.ip.equals(this.ip))
                return true;

        } catch (Exception e) {
        }

        return false;
    }
}
