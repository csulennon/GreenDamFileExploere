package com.cmcm.greendamexplorer.core.engine;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Favorite;
import com.cmcm.greendamexplorer.entity.SimplePackgeInfo;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class ResourceManager {
    private static ResourceManager mManager = null;
    private Context mContext = DeploymentOperation.getAppContext();
    public static final String DATABASES_DIR = "/data/data/com.cmcm.greendamexplorer.activity/databases";
    public static List<String> mExternalStoragepaths = new ArrayList<String>(); // 存储卡数量
    public static String mExternalStoragePath = "/";
    public static String mDownloadPath = null;
    public static long mToalBytes = 0;
    public static long mUsedBytes = 0;
    public static long mFreeBytes = 0;

    private static FavoriteDao mDao = DaoFactory.getFavoriteDao(DeploymentOperation.getAppContext());

    static {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            mExternalStoragePath = sdDir.toString();
            mExternalStoragepaths.add(mExternalStoragePath);
        }
        
        calcStorageSize();

        mDownloadPath = Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {

        if (mManager == null) {
            mManager = new ResourceManager();
        }
        return mManager;
    }

    /**
     * 
     * 获取图片地址列表
     * 
     * @return list
     */
    public static ArrayList<String> getImagesPathFromMedia(Context context) {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "_data" }, null, null,
                    null);
            while (cursor.moveToNext()) {
                list.add("file://" + cursor.getString(1));// 将图片路径添加到list中
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public static SimplePackgeInfo getApkPackgageInfo(String archiveFilePath) {

        PackageManager pm = DeploymentOperation.getAppContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);

        SimplePackgeInfo pkgInfo = null;
        if (info != null) {

            ApplicationInfo appInfo = info.applicationInfo;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName; // 得到安装包名称
            String version = info.versionName; // 得到版本信息
            Drawable icon = pm.getApplicationIcon(appInfo);// 得到图标信息

            pkgInfo = new SimplePackgeInfo(icon, appName, version, packageName);
        }

        return pkgInfo;
    }

    // 获取用户应用
    public List<PackageInfo> getCustomApps() {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pm = mContext.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pm.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(pak);
            }
        }
        return apps;
    }

    // 获取系统应用
    public List<PackageInfo> getSystemApps() {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pm = mContext.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pm.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                apps.add(pak);
            }
        }
        return apps;
    }

    // 获取所有应用
    public List<PackageInfo> getAllApps() {
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> paklist = pm.getInstalledPackages(0);
        return paklist;
    }

    public static List<Favorite> getAllFavorites() {
        List<Favorite> favorites = new ArrayList<Favorite>();

        favorites = mDao.findAllFavorite();
        return favorites;
    }

    public static void clearAllFavorite() {
        mDao.clearAll();
        DeploymentOperation.initialFavoriteDatabase();
    }

    public static void calcStorageSize() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File filePath = Environment.getExternalStorageDirectory(); // 获得sd卡的路径
            StatFs stat = new StatFs(filePath.getPath()); // 创建StatFs对象
            
            long blockSize = stat.getBlockSize(); // 获取block的size
            float totalBlocks = stat.getBlockCount(); // 获取block的总数
            
            mToalBytes = (long) (blockSize * totalBlocks) ;
            long availableBlocks = stat.getAvailableBlocks(); // 获取可用块大小
            
            
            mUsedBytes= (long) ((totalBlocks - availableBlocks) * blockSize);
            mFreeBytes = mToalBytes - mUsedBytes;
            
            System.out.println("存储空间" + TextUtil.getSizeSting(mToalBytes) + ",已用" +  TextUtil.getSizeSting(mUsedBytes) + "剩余：" + TextUtil.getSizeSting(mFreeBytes));
        } else {
            System.out.println("存储卡不存在：存储空间" + TextUtil.getSizeSting(mToalBytes) + ",已用" +  TextUtil.getSizeSting(mUsedBytes) + "剩余：" + TextUtil.getSizeSting(mFreeBytes));
        }
    }

    public static void removeItem(String path) {
        FavoriteDao dao = DaoFactory.getFavoriteDao(DeploymentOperation.getAppContext());
        dao.deleteFavorite(path);
    }
}
