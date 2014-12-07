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
 * 获取appdirname.db数据库操作接口
 * 
 * @author Lennon
 * @time 2014-08-04
 */
public class APPNameDbHelper extends SQLiteOpenHelper implements IDBHelper {
    public static final String DATABASE_APP_DIR_NAME = "appdirname.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_DIR_NAME_MAP = "CREATE TABLE IF NOT EXISTS dir_name_map (_id INTEGER PRIMARY KEY AUTOINCREMENT, dir_name VARCHAR(200), appName VARCHAR)";
    private static final String SQL_CREATE_CACHE = "CREATE TABLE IF NOT EXISTS cache (_id INTEGER PRIMARY KEY AUTOINCREMENT, path VARCHAR(200), packag VARCHAR, name varchar(100))";
    private static final String SQL_CREATE_FILE_TYPE = "CREATE TABLE IF NOT EXISTS file_type (_id INTEGER PRIMARY KEY AUTOINCREMENT, extern_name VARCHAR(20), file_type integer, category integer, type_name varchar(20), category_name varchar(20)) ";
    private static final String SQL_CREATE_FAVORITE = "CREATE TABLE IF NOT EXISTS favorite (_id INTEGER PRIMARY KEY AUTOINCREMENT, path VARCHAR(200), name varchar(100),app_name varchar(100), file_type integer, favorite_time integer, size integer, extra text)";
    private static APPNameDbHelper mDBHelper = null;
    private final AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDatabase = null;

    public APPNameDbHelper(Context context) {
        super(context, DATABASE_APP_DIR_NAME, null, DATABASE_VERSION);
    }

    public APPNameDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DIR_NAME_MAP);// 创建目录应用映射表
        db.execSQL(SQL_CREATE_CACHE);// 创建文件目录映射缓存
        db.execSQL(SQL_CREATE_FILE_TYPE);// 创建扩展名对应类型表
        db.execSQL(SQL_CREATE_FAVORITE);// 创建收藏列表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /* 获取数据库操作对象实例 */
    public static APPNameDbHelper getInstance() {
        if (mDBHelper == null) {
            mDBHelper = new APPNameDbHelper(DeploymentOperation.getAppContext());
        }
        return mDBHelper;
    }

    /* 打开数据库 */
    public SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            try {
                mDatabase = getWritableDatabase();

            } catch (SQLException ex) {
                ex.printStackTrace();
                GDLog.e("打开数据库 " + DATABASE_APP_DIR_NAME + "异常！");
            }
        }
        return mDatabase;
    }

    /* 关闭数据库 */
    public void closeDatabase() {
        int openCount = mOpenCounter.decrementAndGet();
        if (openCount == 0) {
            try {
                if (mDatabase != null) {
                    mDatabase.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                GDLog.e("数据库 " + DATABASE_APP_DIR_NAME + "关闭异常！");
            } finally {
                mDatabase = null;
            }
        }
    }
}
