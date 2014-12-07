package com.cmcm.greendamexplorer.core.common;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.cmcm.greendamexplorer.core.engine.DeploymentOperation;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.entity.Audio;
import com.cmcm.greendamexplorer.entity.Video;
import com.cmcm.greendamexplorer.fragment.FileCategoryPageFragment;
import com.cmcm.greendamexplorer.utils.FileUtils;

public class MediaResourceManager {

    private static Context mContext = DeploymentOperation.getAppContext();
    public static ContentResolver mContentResolver = mContext.getContentResolver();

    private static final Uri mArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public static List<Audio> getAudiosFromMedia() {
        List<Audio> audios = new ArrayList<Audio>();
        Cursor c = null;
        try {
            FileCategoryPageFragment.mAllAudioSize = 0;
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                if (!FileUtils.isExists(path)) {
                    continue;
                }

                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 时间
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时间
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String tilte = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                FileCategoryPageFragment.mAllAudioSize += size;

                Audio audio = new Audio(id, path, tilte, artist, album, albumId, duration, size);
                audios.add(audio);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return audios;

    }

    public static List<Video> getVideosFromMedia() {

        List<Video> videos = new ArrayList<Video>();

        Cursor c = null;
        try {
            FileCategoryPageFragment.mAllVideoSize = 0;
            // String[] mediaColumns = { "_id", "_data", "_display_name",
            // "_size", "date_modified", "duration", "resolution" };
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                if (!FileUtils.isExists(path)) {
                    continue;
                }

                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 大小
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 歌曲名
                String resolution = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时间
                long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));

                FileCategoryPageFragment.mAllVideoSize += size;

                Video video = new Video(id, path, name, resolution, size, date, duration);
                videos.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videos;
    }

    /**
     * 
     * 获取图片地址列表
     * 
     * @return list
     */
    public static List<String> getImagesFromMedia() {
        ArrayList<String> pictures = new ArrayList<String>();
        Cursor c = null;
        try {
            FileCategoryPageFragment.mAllPictureSize = 0;
            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "_data", "_size" }, null, null, null);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                if (!FileUtils.isExists(path)) {
                    continue;
                }
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                FileCategoryPageFragment.mAllPictureSize += size;
                pictures.add(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return pictures;
    }

    /**
     * 获取下载路径
     * 
     * @return
     */
    public static String getDownloadPath() {
        String downloadPath = ResourceManager.mExternalStoragePath + File.separator + "Download";
        File dir = new File(downloadPath);

        if (!dir.exists()) {
            downloadPath = ResourceManager.mExternalStoragePath + File.separator + "download";
            dir = new File(downloadPath);
        }
        dir.mkdirs();
        System.out.println("下载路径:" + dir.getAbsolutePath());
        return downloadPath;
    }

    public static List<String> getDownloads() {
        List<String> downloads = new ArrayList<String>();
        String downloadPath = getDownloadPath();
        File dir = new File(downloadPath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    downloads.add(file.getAbsolutePath());
                }
            }
        }
        return downloads;
    }

    public static String getBluetoothPath() {
        String blueToothPath = ResourceManager.mExternalStoragePath + File.separator + "Bluetooth";
        File dir = new File(blueToothPath);

        if (!dir.exists()) {
            blueToothPath = ResourceManager.mExternalStoragePath + File.separator + "bluetooth";
            dir = new File(blueToothPath);
        }
        dir.mkdirs();
        System.out.println("蓝牙路径:" + dir.getAbsolutePath());

        return blueToothPath;
    }

    public static List<String> getBluetooths() {
        List<String> bluetooths = new ArrayList<String>();

        String path = getBluetoothPath();
        File dir = new File(path);

        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    bluetooths.add(file.getAbsolutePath());
                }
            }
        }
        return bluetooths;
    }

    public static int getApplicationCount() {
        PackageManager pm = DeploymentOperation.getAppContext().getPackageManager();
        return pm.getInstalledApplications(PackageManager.GET_ACTIVITIES).size();
    }

    // 获取视频缩略图
    public static Bitmap getVideoThumbnail(int id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, Images.Thumbnails.MICRO_KIND, options);
        return bitmap;
    }

    // 获取专辑图片
    public static Bitmap getArtworkFromFile(long songid, long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }

        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = mContentResolver.openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(mArtworkUri, albumid);
                ParcelFileDescriptor pfd = mContentResolver.openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return bm;
    }

}
