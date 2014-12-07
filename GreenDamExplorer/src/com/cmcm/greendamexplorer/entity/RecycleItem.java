package com.cmcm.greendamexplorer.entity;

import java.sql.Date;

import com.cmcm.greendamexplorer.core.common.FileType;

public class RecycleItem {
    private int id;
    private String fromPath = null;
    private String recyclePath = null;
    private String name = null;
    private int fileType = FileType.TYPE_UNKNOWN;
    private Date recycleTime = null;
    private Date lastModifyTime = null;
    private long size;// 字节
    private boolean visibility = true;
    private String extra;

    public RecycleItem(String fromPath, String recyclePath, String name, int fileType, Date recycleTime,
            Date lastModifyTime, long size, boolean visibility) {
        this.fromPath = fromPath;
        this.recyclePath = recyclePath;
        this.name = name;
        this.fileType = fileType;
        this.recycleTime = recycleTime;
        this.lastModifyTime = lastModifyTime;
        this.size = size;
        this.visibility = visibility;
    }

    public RecycleItem(int id, String fromPath, String recyclePath, String name, int fileType, Date recycleTime,
            Date lastModifyTime, long size, boolean visibility, String extra) {
        this.id = id;
        this.fromPath = fromPath;
        this.recyclePath = recyclePath;
        this.name = name;
        this.fileType = fileType;
        this.recycleTime = recycleTime;
        this.lastModifyTime = lastModifyTime;
        this.size = size;
        this.visibility = visibility;
        this.extra = extra;
    }

    public RecycleItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromPath() {
        return fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    public String getRecyclePath() {
        return recyclePath;
    }

    public void setRecyclePath(String recyclePath) {
        this.recyclePath = recyclePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getRecycleTime() {
        return recycleTime;
    }

    public void setRecycleTime(Date recycleTime) {
        this.recycleTime = recycleTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "RecycleItem [id=" + id + ", fromPath=" + fromPath + ", recyclePath=" + recyclePath + ", name=" + name
                + ", fileType=" + fileType + ", recycleTime=" + recycleTime + ", lastModifyTime=" + lastModifyTime
                + ", size=" + size + ", visibility=" + visibility + ", extra=" + extra + "]";
    }

}
