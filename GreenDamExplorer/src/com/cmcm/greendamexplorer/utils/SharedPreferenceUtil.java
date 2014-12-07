package com.cmcm.greendamexplorer.utils;

import com.cmcm.greendamexplorer.core.common.FileComparator;
import com.cmcm.greendamexplorer.core.engine.DeploymentOperation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtil {
    private static final String PREFERENCE_NAME_BASIC_CONFIG = "basic_config";
    private static final String PREFERENCE_SOTR_TYPE = "sort_type";
    private static final String PREFERENCE_SHOW_HIDE_FILES = "show_hide_files";
    private static final String PREFERENCE_FIRST_TIME_USE = "first_time_use";

    private static Context mContext = DeploymentOperation.getAppContext();
    private static SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME_BASIC_CONFIG, Context.MODE_PRIVATE);

    /**
     * 获取排序方式
     * 
     * @return
     */
    public static int getSortType() {
        int sortType = sharedPreferences.getInt(PREFERENCE_SOTR_TYPE, FileComparator.SORT_TYPE_BY_NAME_UP);
        return sortType;
    }

    /**
     * 设置排序方式
     * 
     * @param sortType
     */
    public static void setSortType(int sortType) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(PREFERENCE_SOTR_TYPE, sortType);
        editor.commit();
    }

    // 是否显示隐藏文件
    public static boolean getShowHideFiles() {
        boolean showHideFile = sharedPreferences.getBoolean(PREFERENCE_SHOW_HIDE_FILES, false);
        return showHideFile;
    }

    // 设置是否显示隐藏文件
    public static void setShowHideFiles(boolean show) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCE_SHOW_HIDE_FILES, show);
        editor.commit();
    }

    public static void setFirstTimeUse(boolean isFirst) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCE_FIRST_TIME_USE, isFirst);
        editor.commit();
    }

    public static boolean getFirstTimeUse() {
        return sharedPreferences.getBoolean(PREFERENCE_FIRST_TIME_USE, true);
    }

}
