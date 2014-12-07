package com.cmcm.greendamexplorer.dao.db;

import com.cmcm.greendamexplorer.log.GDLog;

public abstract class BaseDbDAO {

    protected GDEDbHelper mGdeDbHelper = null;
    protected APPNameDbHelper mAppNameDbHelper = null;

    public BaseDbDAO() {
        mGdeDbHelper = GDEDbHelper.getInstance();
        mAppNameDbHelper = APPNameDbHelper.getInstance();
    }
    
    public void clearTable(IDBHelper helper, String tableName) {
        try {
            helper.openDatabase().execSQL("delete from " + tableName);
        } catch (Exception ex) {
            ex.printStackTrace();
            GDLog.e("清空表（" + tableName + "）异常");
        } finally {
            helper.closeDatabase();
        }
    }

}
