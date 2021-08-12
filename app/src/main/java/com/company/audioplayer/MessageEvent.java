package com.company.audioplayer;

public class MessageEvent {
    private int playPause;
    private String name;
    private String album;
    private int totalTimeStamp;
    private int currentTimeStamp;
    private String artist;
    private String path;
    private int favourite;

    public MessageEvent(int playPause, String name, String album, int totalTimeStamp,
                        int currentTimeStamp, String artist, String path, int favourite) {
        this.playPause = playPause;
        this.name = name;
        this.album = album;
        this.totalTimeStamp = totalTimeStamp;
        this.currentTimeStamp = currentTimeStamp;
        this.artist = artist;
        this.path = path;
        this.favourite = favourite;
    }

    public int getPlayPause() {
        return playPause;
    }

    public void setPlayPause(int playPause) {
        this.playPause = playPause;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getTotalTimeStamp() {
        return totalTimeStamp;
    }

    public void setTotalTimeStamp(int totalTimeStamp) {
        this.totalTimeStamp = totalTimeStamp;
    }

    public int getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    public void setCurrentTimeStamp(int currentTimeStamp) {
        this.currentTimeStamp = currentTimeStamp;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }


}
