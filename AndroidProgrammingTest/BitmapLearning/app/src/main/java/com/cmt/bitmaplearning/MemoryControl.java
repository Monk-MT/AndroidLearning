package com.cmt.bitmaplearning;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-03-31-16:59
 */
public class MemoryControl {
    private LruCache<String, Bitmap> mMemoryCache;

    public void getMemoryCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // 获取最大可用内存数

        final int cacheSize = maxMemory / 8; // 使用1/8来缓存图片

        mMemoryCache = new LruCache<String,Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

}
