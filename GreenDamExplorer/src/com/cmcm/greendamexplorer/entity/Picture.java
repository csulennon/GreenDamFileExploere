package com.cmcm.greendamexplorer.entity;

public class Picture {

    private String path = null;
    private long size = 0;

    public Picture(String path, long size) {
        super();
        this.path = path;
        this.size = size;
    }

    public Picture() {
        super();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Picture [path=" + path + ", size=" + size + "]";
    }

}