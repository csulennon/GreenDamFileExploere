package com.cmcm.greendamexplorer.entity;

/**
 * 收藏夹信息
 * 
 * @author Administrator
 * 
 */
public class Favorite {
    private String canonicalPath = null;
    private String name = null;
    private String appName = null;
    private int fileType;
    private long favoriteTime = 0;
    private long size = 0;
    private String extra = null;
    private boolean isChecked = false;

    public Favorite() {
    }

    public Favorite(String canonicalPath, String name, String appName, int fileType, long favoriteTime, long size, String extra) {
        this.canonicalPath = canonicalPath;
        this.name = name;
        this.appName = appName;
        this.fileType = fileType;
        this.favoriteTime = favoriteTime;
        this.size = size;
        this.extra = extra;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public void setCanonicalPath(String canonicalPath) {
        this.canonicalPath = canonicalPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(long favoriteTime) {
        this.favoriteTime = favoriteTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Favorite [canonicalPath=" + canonicalPath + ", name=" + name + ", appName=" + appName + ", fileType=" + fileType + ", favoriteTime="
                + favoriteTime + ", size=" + size + ", extra=" + extra + ", isChecked=" + isChecked + "]";
    }

}
