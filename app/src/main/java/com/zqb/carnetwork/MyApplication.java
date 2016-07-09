package com.zqb.carnetwork;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by zqb on 2016/6/4.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }
}
