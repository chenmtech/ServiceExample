package com.cmtech.android.serviceexample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mob.MobSDK;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;

import org.litepal.LitePal;

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

        // 初始化LitePal
        LitePal.initialize(context);
        LitePal.getDatabase();

        // 初始化ShareSDK
        MobSDK.init(context);

        // init ViseLog
        ViseLog.getLogConfig()
                .configAllowLog(true)           //是否输出日志
                .configShowBorders(false)        //是否排版显示
                .configTagPrefix("BleDeviceApp")     //设置标签前缀
                .configLevel(Log.VERBOSE);      //设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(new LogcatTree());        //添加打印日志信息到Logcat的树
    }

    // 获取Application Context
    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
