package com.cmtech.android.serviceexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zejian
 * Time 2016/10/2.
 * Description:绑定服务简单实例--服务端
 */
public class LocalService extends Service{
    private final static String TAG = "LocalService";
    private int count;
    private boolean quit;
    private Thread thread;
    private LocalBinder binder = new LocalBinder();

    /**
     * 创建Binder对象，返回给客户端即Activity使用，提供数据交换的接口
     */
    public class LocalBinder extends Binder {
        // 声明一个方法，getService。（提供给客户端调用）
        public LocalService getService() {
            // 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
            return LocalService.this;
        }
    }

    /**
     * 把Binder类返回给客户端
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service is invoke Created");
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 每间隔一秒count加1 ，直到quit为true。
                while (!quit) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        });
        thread.start();
    }

    /**
     * 公共方法
     * @return
     */
    public int getCount(){
        return count;
    }
    /**
     * 解除绑定时调用
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service is invoke onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is invoke Destroyed");
        this.quit = true;
        super.onDestroy();
    }
}