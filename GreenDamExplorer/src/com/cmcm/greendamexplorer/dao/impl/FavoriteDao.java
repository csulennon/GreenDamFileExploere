package com.cmcm.greendamexplorer.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cmcm.greendamexplorer.dao.db.BaseDbDAO;
import com.cmcm.greendamexplorer.entity.Favorite;

/**
 * 收藏夹操作
 * 
 * @author Administrator
 * 
 */
public class FavoriteDao extends BaseDbDAO {

    private static final String SQL_FIND_FAVORITE_BY_PATH = "select * from favorite where path=?";
    private static final String SQL_FIND_ALL_FAVORITE = "select * from favorite";
    public static final String SQL_INSERT_FAVORITE = "insert into favorite values(null, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DELETE_BY_PATH = "delete from favorite where path=?";
    private static final String TAG = "FavoriteDao";

    public Favorite findFavoriteByFullPath(String canonicalPath) {
        Favorite favorite = null;

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            c = db.rawQuery(SQL_FIND_FAVORITE_BY_PATH, new String[] { canonicalPath });

            if (c.moveToLast()) {
                String path = c.getString(c.getColumnIndex("path"));
                String name = c.getString(c.getColumnIndex("name"));
                String appName = c.getString(c.getColumnIndex("app_name"));
                int fileType = c.getInt(c.getColumnIndex("file_type"));
                long date = c.getLong(c.getColumnIndex("favorite_time"));
                long size = c.getLong(c.getColumnIndex("size"));
                String extra = c.getString(c.getColumnIndex("extra"));
                favorite = new Favorite(path, name, appName, fileType, date, size, extra);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mAppNameDbHelper.closeDatabase();
            if (c != null) {
                c.close();
            }
        }
        return favorite;
    }

    /**
     * 查询所有收藏内容
     * 
     * @return
     */
    public List<Favorite> findAllFavorite() {
        List<Favorite> favorites = new ArrayList<Favorite>();
        favorites.clear();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            c = db.rawQuery(SQL_FIND_ALL_FAVORITE, null);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex("path"));
                String name = c.getString(c.getColumnIndex("name"));
                String appName = c.getString(c.getColumnIndex("app_name"));
                int fileType = c.getInt(c.getColumnIndex("file_type"));
                long date = c.getLong(c.getColumnIndex("favorite_time"));
                long size = c.getLong(c.getColumnIndex("size"));
                String extra = c.getString(c.getColumnIndex("extra"));
                Favorite favorite = new Favorite(path, name, appName, fileType, date, size, extra);
                favorites.add(favorite);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return favorites;
    }

    // "insert into favorite values(null, ?, ?, ?, ?, ?, ?, ?)"
    public void insertFavorite(Favorite favorite) {
        SQLiteDatabase db = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            db.execSQL(
                    SQL_INSERT_FAVORITE,
                    new String[] { favorite.getCanonicalPath(), favorite.getName(), favorite.getAppName() + "", favorite.getFileType() + "",
                            favorite.getFavoriteTime() + "", favorite.getSize() + "", favorite.getExtra() + "" });
        } catch (SQLException e) {
            Log.e(TAG, "插入数据" + favorite + "失败");
            e.printStackTrace();
        }
    }

    /**
     * 批量插入数据
     * 
     * @param favorites
     */
    public void insertFavorites(List<Favorite> favorites) {

        SQLiteDatabase db = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            db.beginTransaction();// 开始事务
            for (Favorite favorite : favorites) {
                db.execSQL(SQL_INSERT_FAVORITE,
                        new String[] { favorite.getCanonicalPath(), favorite.getName(), favorite.getAppName() + "", favorite.getFileType() + "",
                                favorite.getFavoriteTime() + "", favorite.getSize() + "", favorite.getExtra() + "" });
            }
            db.setTransactionSuccessful();// 提交事务
        } catch (SQLException e) {
            Log.e(TAG, "--->插入多条数据" + favorites + "失败");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 清空表
     */
    public void clearAll() {
        super.clearTable(mAppNameDbHelper, "favorite");
    }

    public void deleteFavorite(String path) {
        SQLiteDatabase db = null;
        try {
            db = mAppNameDbHelper.openDatabase();
            db.execSQL(SQL_DELETE_BY_PATH, new String[] { path });
        } catch (SQLException e) {
            Log.e(TAG, "删除数据" + path + "失败");
            e.printStackTrace();
        }
    }

    public void deleteFavorite(Favorite favorite) {
        deleteFavorite(favorite.getCanonicalPath());
    }

    public void deleteFavorites(List<String> favorites) {
        for (String path : favorites) {
            deleteFavorite(path);
        }
    }

}
