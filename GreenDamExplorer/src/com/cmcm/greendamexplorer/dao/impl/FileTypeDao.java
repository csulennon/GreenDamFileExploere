package com.cmcm.greendamexplorer.dao.impl;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.dao.db.BaseDbDAO;
import com.cmcm.greendamexplorer.log.GDLog;

public class FileTypeDao extends BaseDbDAO {

    // "select file_type from file_type where extern_name = ?";
    private static String[]    mColums            = new String[] { "file_type" };
    private static String      mTableName         = "file_type";
    private static String      mSelection         = "extern_name=?";
    private Cursor             mCursor            = null;

    public static final String SQL_QUERY_CATEGORY = "select category from file_type where extern_name=?";
    public static final String SQL_QUERY_ALL = "select extern_name, file_type, category from file_type";

    // "select file_type from file_type where extern_name = ?";
    /**
     * 根据文件后缀查询文件的类型
     * 
     * @param extern_name
     * @return
     */
    public int getFileType(String extern_name) {
        int type = FileType.TYPE_UNKNOWN;
        try {
            mCursor = super.mAppNameDbHelper.openDatabase().query(false, mTableName, mColums, mSelection, new String[] { extern_name }, null, null,
                    null, null, null);
            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    type = mCursor.getInt(mCursor.getColumnIndex("file_type"));
                } else {
                    type = FileType.TYPE_UNKNOWN;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            GDLog.e("查询文件类型（" + extern_name + "）异常");
        } finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
        return type;
    }

    /**
     * 查询文件所说的类别
     * 
     * @param extern_name
     * @return
     */
    public int getFileCategory(String extern_name) {
        int category = FileType.CATEGORY_UNKNOWN;
        try {
            SQLiteDatabase db = super.mAppNameDbHelper.openDatabase();
            mCursor = db.rawQuery(SQL_QUERY_CATEGORY, new String[] { extern_name });
            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    category = mCursor.getInt(mCursor.getColumnIndex("category"));
                } else {
                    category = FileType.CATEGORY_UNKNOWN;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            GDLog.e("查询文件类型（" + extern_name + "）异常");
        } finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
        return category;
    }
    
    public Map<String, int[]> getAllExtensionFileTypeMap() {
        Map<String, int[]> extensions = new HashMap<String, int[]>();
        SQLiteDatabase db = null;
        try {
            db = super.mAppNameDbHelper.openDatabase();
            mCursor = db.rawQuery(SQL_QUERY_ALL, null);
            String extension = null;
            
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    int[] value = new int[2];
                    extension = mCursor.getString(0);
                    value[0] =  mCursor.getInt(1);
                    value[1] = mCursor.getInt(2);
                    
                    extensions.put(extension, value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            GDLog.e("查询数据库（表" + mTableName + "）异常");
        } finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
        
        return extensions;
    }

}
