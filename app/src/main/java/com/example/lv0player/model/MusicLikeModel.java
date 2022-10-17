package com.example.lv0player.model;

public class MusicLikeModel {
    private long id;
    private String music_name;
    private String artist_name;
    private String picUrl;

    public long getMusicId() {
        return id;
    }

    public void setMusicId(long id) {
        this.id = id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
