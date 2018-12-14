package com.cmtech.android.serviceexample.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cmtech.android.serviceexample.MainActivity;
import com.cmtech.android.serviceexample.R;
/**
 *  BleDeviceService: BleDevice服务
 *  Created by bme on 2018/12/09.
 */

public class ForegroundService extends Service implements IForegroundServiceUpdateMsg {
    private final static String TAG = "BleDeviceService";

    private String notiTitle;
    private final static String NOTIFICATION_CONTENT_TEXT = "当前设备连接状态";

    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int SERVICE_NOTIFICATION_ID = 0x0001;


    public class DeviceServiceBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    private DeviceServiceBinder binder = new DeviceServiceBinder();

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    private Ringtone notificationRingtone;

    @Override
    public void onCreate() {
        super.onCreate();

        notiTitle = "欢迎使用" + getResources().getString(R.string.app_name);
        notificationRingtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initNotificationBuilder();

        startForeground(SERVICE_NOTIFICATION_ID, createNotification(NOTIFICATION_CONTENT_TEXT));
    }

    private void initNotificationBuilder() {
        notificationBuilder = new NotificationCompat.Builder(this, "default");
        //设置状态栏的通知图标
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //设置通知栏横条的图标
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        //禁止用户点击删除按钮删除
        notificationBuilder.setAutoCancel(false);
        //禁止滑动删除
        notificationBuilder.setOngoing(true);
        //右上角的时间显示
        notificationBuilder.setShowWhen(true);
        //设置通知栏的标题内容
        notificationBuilder.setContentTitle(notiTitle);
        notificationBuilder.setContentText(NOTIFICATION_CONTENT_TEXT);

        Intent startMainActivity = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, startMainActivity, 0);
        notificationBuilder.setContentIntent(pi);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);

        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void updateMessage(String msg) {

        sendNotification(msg, true);

        Log.i(TAG, msg);
    }

    private void sendNotification(String content) {
        sendNotification(content, false);
    }

    private void sendNotification(String content, boolean isPlaySound) {
        if(isPlaySound) {
            notificationRingtone.play();
        }
        notificationManager.notify(SERVICE_NOTIFICATION_ID, createNotification(content));
    }

    public void warn() {
        notificationRingtone.play();
    }

    /**
     * Notification
     */
    private Notification createNotification(String content){
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(notiTitle);
        inboxStyle.addLine(content);
        notificationBuilder.setStyle(inboxStyle);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        notification.defaults = Notification.DEFAULT_SOUND;
        //创建通知
        return notification;
    }
}
