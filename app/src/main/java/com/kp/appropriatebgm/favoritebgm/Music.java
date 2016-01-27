package com.kp.appropriatebgm.favoritebgm;

import java.io.Serializable;

public class Music implements Serializable{

    private  static final long serialVersionUID=100000L;
    private String musicPath;
    private String musicName;
    private  String musicId;

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public Music(String musicPath, String musicName, String musicId) {
        this.musicPath = musicPath;
        this.musicName = musicName;
        this.musicId = musicId;
    }

    public Music(String musicPath, String musicName) {
        this.musicPath = musicPath;
        this.musicName = musicName;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
}
