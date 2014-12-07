package com.cmcm.greendamexplorer.entity;

public class Audio {
    private int id = 0;
    private String path = null;
    private String tilte = null;
    private String artist = null;
    private String album = null;
    private int albumId = 0;
    private int duration = 0;
    private long size = 0;

    public Audio(int id, String path, String tilte, String artist, String album, int albumId, int duration, long size) {
        this.id = id;
        this.path = path;
        this.tilte = tilte;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.duration = duration;
        this.size = size;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public Audio() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTilte() {
        return tilte;
    }

    public void setTilte(String tilte) {
        this.tilte = tilte;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Audio [id=" + id + ", path=" + path + ", tilte=" + tilte + ", artist=" + artist + ", album=" + album + ", albumId=" + albumId
                + ", duration=" + duration + ", size=" + size + "]";
    }

}
