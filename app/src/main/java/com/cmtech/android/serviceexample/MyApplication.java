package com.cmtech.android.serviceexample;

import android.app.Application;
import android.content.Context;

import com.mob.MobSDK;
/**
 * MyApplication
 * Created by bme on 2018/2/19.
 */

public class MyApplication extends Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Context context = getApplicationContext();

        // 初始化ShareSDK
        MobSDK.init(context);
    }

    // 获取Application Context
    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
