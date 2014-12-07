package com.cmcm.greendamexplorer.entity;

import java.io.Serializable;

public class DirNameMap implements Serializable {
    private static final long serialVersionUID = 2216144154405717594L;
    private int id = 0;
    private String dirName = null;
    private String appName = null;
    
    public DirNameMap(int id, String dirName, String appName) {
        this.id = id;
        this.dirName = dirName;
        this.appName = appName;
    }

    public DirNameMap() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDirName() {
        return dirName;
    }
    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
