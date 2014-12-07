package com.cmcm.greendamexplorer.dao.db;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.cmcm.greendamexplorer.core.engine.DeploymentOperation;
import com.cmcm.greendamexplorer.log.GDLog;

/**
 * 获取greendamexplorer.db数据库操作接口
 * 
 * @author Lennon
 * @time 2014-08-04
 */
public class GDEDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_GREENDAMEXPLORER = "greendamexplorer.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_RECYCLE_BIN = "CREATE TABLE IF NOT EXISTS recycle_bin (_id INTEGER PRIMARY KEY AUTOINCREMENT,  from_path VARCHAR(200), recycle_path VARCHAR(200), name varchar(100), file_type integer, recycle_time datetime, last_modify_time datetime, size integer,  visibility  BOOL DEFAULT 0, extra text)";

    private static GDEDbHelper mDBHelper = null;
    private final AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDatabase;

    public GDEDbHelper(Context context) {
        super(context, DATABASE_GREENDAMEXPLORER, null, DATABASE_VERSION);
    }

    public GDEDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RECYCLE_BIN);// 创建回收站
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /* 获取数据库操作对象实例 */
    public static synchronized GDEDbHelper getInstance() {
        if (mDBHelper == null) {
            Context context = DeploymentOperation.getAppContext();
            mDBHelper = new GDEDbHelper(context);
        }
        return mDBHelper;
    }

    // 打开数据库
    public SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            try {
                mDatabase = getWritableDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
                GDLog.e("打开数据库 " + DATABASE_GREENDAMEXPLORER + "异常！");
            }
        }
        return mDatabase;
    }

    // 关闭数据库
    public void closeDatabase() {
        int openCount = mOpenCounter.decrementAndGet();
        if (openCount == 0) {
            try {
                if (mDatabase != null) {
                    mDatabase.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                GDLog.e("关闭数据库 " + DATABASE_GREENDAMEXPLORER + "异常！");
            } finally {
                mDatabase = null;
            }
        }
    }

    public void clearTable(String tableName) {
        try {
            openDatabase().execSQL("delete from " + tableName);
        } catch (Exception ex) {
            ex.printStackTrace();
            GDLog.e("清空表（" + tableName + "）异常");
        } finally {
            closeDatabase();
        }
    }
}
