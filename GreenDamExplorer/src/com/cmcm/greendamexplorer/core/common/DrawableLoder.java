package com.cmcm.greendamexplorer.core.common;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

public class DrawableLoder {

    private static LruCache<String, Drawable> mMemoryCache;

    private static DrawableLoder mImageLoader;

    private DrawableLoder() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Drawable>(cacheSize);
    }
    
    public synchronized static DrawableLoder getInstance() {
        if (mImageLoader == null) {
            synchronized (DrawableLoder.class) {
                mImageLoader = new DrawableLoder();
            }
        }
        return mImageLoader;
    }
    
    public void addDrawableToMemoryCache(String url, Drawable bitmap) {
        if (getDrawableFromMemoryCache(url) == null) {
            synchronized (mMemoryCache) {
                if (url != null && bitmap != null) {
                    mMemoryCache.put(url, bitmap);
                }
            }
        }
    }
    
    public Drawable getDrawableFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

}
