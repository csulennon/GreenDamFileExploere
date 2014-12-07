package com.cmcm.greendamexplorer.core.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.LruCache;

/**
 * 采用单例模式,防止多个实例出现共同占用内存造成OOM
 * 
 * @refer http://blog.csdn.net/guolin_blog/article/details/10470797
 * @author Lennon
 */
public class CMImageLoader {

    /* 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。 */
    private static LruCache<String, Bitmap> mMemoryCache;

    /* ImageLoader的实例 */
    private static CMImageLoader mImageLoader;

    private CMImageLoader() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 5;
        // 设置图片缓存大小为程序最大可用内存的1/5
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();// 重写该方法可以在使用的时候获得
            }
        };
    }

    /**
     * 需要做线程同步
     * 
     * @return
     */
    public synchronized static CMImageLoader getInstance() {
        if (mImageLoader == null) {
            synchronized (CMImageLoader.class) {
                mImageLoader = new CMImageLoader();
            }
        }
        return mImageLoader;
    }

    /**
     * 将一张图片存储到LruCache中。
     * 
     * @param url
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入Bitmap对象。
     */
    public void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(url) == null) {
            synchronized (mMemoryCache) {
                if (url != null && bitmap != null) {
                    mMemoryCache.put(url, bitmap);
                }
            }
        }
    }

    /**
     * 获取视频图像
     * 
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     * 
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth) {

        if (pathName.endsWith(".mp4") || pathName.endsWith(".avi") || pathName.endsWith(".3gp") || pathName.endsWith(".rmvb")) {
            return getVideoThumbnail(pathName);
        }
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

}
