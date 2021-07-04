package com.cmt.bitmaplearning;

import android.app.Application;
import android.content.Context;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-03-30-18:03
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}