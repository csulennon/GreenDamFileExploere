package com.cmcm.greendamexplorer.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.dao.db.BaseDbDAO;

public class FileAppNameDao extends BaseDbDAO {

    private static final String SQL_FIND_APP_NAME = "select appName from dir_name_map where dir_name = ?";
    private static final String SQL_FIND_ALL_APP_NAME = "select dir_name, appName from dir_name_map";

    /**
     * 查找app名字
     * 
     * @param canonicalPath
     * @return
     */
    public String findAppName(String canonicalPath) {
        String appName = "";
        if (canonicalPath.contains(ResourceManager.mExternalStoragePath)) {
            canonicalPath = canonicalPath.substring(ResourceManager.mExternalStoragePath.length());
        }

        if (!needToFindDatabase(canonicalPath, "/", 3)) {
            return appName;
        }

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            // System.out.println("---->canonicalPath:" + canonicalPath);
            c = db.rawQuery(SQL_FIND_APP_NAME, new String[] { canonicalPath });
            if (c.moveToLast()) {
                appName = c.getString(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return appName;

    }

    /**
     * 是否需要查找数据库
     * 
     * @param str
     * @param patternStr
     * @param maxCount
     * @return
     */
    public static boolean needToFindDatabase(String str, String patternStr, int maxCount) {

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        int c = 0;
        while (matcher.find()) {
            c++;
            if (c > maxCount) {
                return false;
            }
        }
        return true;
    }

    public Map<String, String> findAllAppName() {
        Map<String, String> map = new HashMap<String, String>();

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            // System.out.println("---->canonicalPath:" + canonicalPath);
            c = db.rawQuery(SQL_FIND_ALL_APP_NAME, null);
            while (c.moveToNext()) {
                String dir = c.getString(0);
                String appName = c.getString(1);
                map.put(dir, appName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return map;
    }

}
