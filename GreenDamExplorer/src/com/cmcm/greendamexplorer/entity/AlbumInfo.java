package com.cmcm.greendamexplorer.entity;

import com.cmcm.greendamexplorer.core.engine.ResourceManager;

/**
 * 相册
 * 
 * @author Lennon
 * @time 2014-08-03
 */
public class AlbumInfo {
    private String displayName;
    private String iconPath;
    private String albumPath = "";
    private int pictureCount;

    public boolean isFromExternal() {
        if (ResourceManager.mExternalStoragepaths.size() > 1) {
            if (albumPath.startsWith(ResourceManager.mExternalStoragepaths.get(1))) {
                return true;
            }
        }
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public int getPictureCount() {
        return pictureCount;
    }

    public void setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
    }

    @Override
    public String toString() {
        return "AlbumInfo [displayName=" + displayName + ", iconPath=" + iconPath + ", albumPath=" + albumPath
                + ", pictureCount=" + pictureCount + "]";
    }

}
