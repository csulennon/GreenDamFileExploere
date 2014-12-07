package com.cmcm.greendamexplorer.entity;

import java.util.Locale;

import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.exception.IllegalFilePath;

/**
 * 简单的文件描述
 * 
 * @author Lennon
 * @time 2014-08-03
 */
public class SimpleFileInfo implements Comparable<SimpleFileInfo> {

    private static final long serialVersionUID = 6779119731190741814L;
    private String path = null;
    private String name = null;
    private String appName = null;
    private long createTime = 0;
    private boolean isChecked = false;
    private boolean isFavorite = false;
    private boolean isNew = false;
    private long fileSize = 0; // 字节
    private int fileType = FileType.TYPE_UNKNOWN;
    private int categoryType = FileType.CATEGORY_UNKNOWN;

    public SimpleFileInfo(String path) {
        this.path = path;
        analysisFileInfo();
    }

    public SimpleFileInfo(String path, int fileType) {
        this.path = path;
        this.fileType = fileType;
        analysisFileInfo();
    }

    public SimpleFileInfo(String path, long createTime, long size) {
        this.path = path;
        this.createTime = createTime;
        this.fileSize = size;
        analysisFileInfo();
    }
    
    public SimpleFileInfo(String path,  long size) {
        this.path = path;
        this.fileSize = size;
        analysisFileInfo();
    }

    public void analysisFileInfo() {
        initName();
        ensureType();
    }

    // 确定当前文件名字
    private void initName() {
        if (path.equals("/")) {
            name = "/";
        } else if (path.endsWith(ResourceManager.mExternalStoragePath)) {
            name = ResourceManager.mExternalStoragePath;
        }

        int index = 0;
        index = path.lastIndexOf("/");
        if (index == -1 || !path.startsWith("/")) {
            try {
                throw new IllegalFilePath("非法的标准路径：" + path);
            } catch (IllegalFilePath e) {
                e.printStackTrace();
            }
        }
        name = path.substring(index + 1);
    }

    private void ensureType() {
        if (fileType == -1) {
            return;
        }
        int[] value = FileType.getFileTypeAndCategory(path);
        fileType = value[0];
        categoryType = value[1];
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取父路径
     * 
     * @return
     */
    public String getParentPath() {

        if (path.equals("/")) {
            return "/";
        } else if (path.endsWith(ResourceManager.mExternalStoragePath)) {
            return ResourceManager.mExternalStoragePath;
        }

        int index = 0;
        index = path.lastIndexOf("/");
        if (index == -1 || !path.startsWith("/")) {
            try {
                throw new IllegalFilePath("非法的标准路径：" + path);
            } catch (IllegalFilePath e) {
                e.printStackTrace();
            }
        }
        return path.substring(0, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SimpleFileInfo)) {
            return false;
        }

        SimpleFileInfo info = (SimpleFileInfo) obj;
        if (path.equals(info.path)) {
            return true;
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "SimpleFileInfo [path=" + path + ", name=" + name + ", isChecked=" + isChecked + ", isFavorite=" + isFavorite + ", isNew=" + isNew
                + ", fileSize=" + fileSize + ", fileType=" + fileType + ", categoryType=" + categoryType + "]";
    }

    @Override
    public int compareTo(SimpleFileInfo another) {
        int flag = 0;
        if (fileType < another.fileType) {
            return -1;
        } else if (fileType > another.fileType) {
            return 1;
        }
        return name.toLowerCase(Locale.CHINA).compareTo(another.name.toLowerCase(Locale.CHINA));

    }
}
