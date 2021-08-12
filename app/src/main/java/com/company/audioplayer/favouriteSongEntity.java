package com.company.audioplayer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favouriteSongs")
public class favouriteSongEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String path;
    private String name;
    private String album;
    private String artist;

    public favouriteSongEntity(String path, String name, String album, String artist) {
        this.path = path;
        this.name = name;
        this.album = album;
        this.artist = artist;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
