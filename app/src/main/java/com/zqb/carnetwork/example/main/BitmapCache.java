package com.zqb.carnetwork.example.main;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by zqb on 2016/5/22.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String,Bitmap>mCache;
    public BitmapCache()
    {
        int maxSize=4*1024*1024;//大小
        mCache=new LruCache<String,Bitmap>(maxSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if(bitmap!=null)
        {
            mCache.put(url,bitmap);
        }
    }
}
