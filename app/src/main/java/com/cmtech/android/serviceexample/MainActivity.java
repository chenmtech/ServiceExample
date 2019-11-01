package com.cmtech.android.serviceexample;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cmtech.android.serviceexample.bmefile.BmeFileDataType;
import com.cmtech.android.serviceexample.bmefile.BmeFileHead;
import com.cmtech.android.serviceexample.bmefile.BmeFileHead10;
import com.cmtech.android.serviceexample.bmefile.BmeFileHead30;
import com.cmtech.android.serviceexample.ecgfile.EcgFileHead;
import com.cmtech.android.serviceexample.ecgfile.EcgLeadType;
import com.cmtech.android.serviceexample.ecgfile.User;
import com.cmtech.android.serviceexample.service.ForegroundService;
import com.cmtech.android.serviceexample.service.LocalService;
import com.cmtech.android.serviceexample.service.SimpleService;
import com.vise.log.ViseLog;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String START_TAG = TAG + "启动顺序";

    /**
     * ServiceConnection代表与服务的连接，它只有两个方法，
     * onServiceConnected和onServiceDisconnected，
     * 前者是在操作者在连接一个服务成功时被调用，而后者是在服务崩溃或被杀死导致的连接中断时被调用
     */
    private ServiceConnection conn;
    private LocalService mService;

    // 工具条
    private Toolbar toolbar;
    private MenuItem menuSwitch;

    private DataObject dataObj;

    private ForegroundService foregroundService;

    private ServiceConnection foregroundServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            foregroundService = ((ForegroundService.DeviceServiceBinder)iBinder).getService();

            // 成功绑定后初始化
            if(foregroundService != null) {
                initialize();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(foregroundService != null) {
                Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
                stopService(stopIntent);
                foregroundService = null;
            }
            finish();
        }
    };

    private void initialize() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(START_TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建ToolBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent startIntent = new Intent(MainActivity.this, SimpleService.class);

        Button btnStartSimpleService = findViewById(R.id.btn_startservice);
        btnStartSimpleService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startService(startIntent);
            }
        });

        Button btnStopSimpleService = findViewById(R.id.btn_stopservice);
        btnStopSimpleService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(startIntent);
            }
        });



        Button btnBindService = findViewById(R.id.BindService);
        Button btnUnBindService = findViewById(R.id.unBindService);
        Button btnGetDatas = findViewById(R.id.getServiceDatas);

        //创建绑定对象
        final Intent bindIntent = new Intent(this, LocalService.class);

        // 开启绑定
        btnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "绑定调用：bindService");
                //调用绑定方法
                bindService(bindIntent, conn, Service.BIND_AUTO_CREATE);
            }
        });

        // 解除绑定
        btnUnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "解除绑定调用：unbindService");
                // 解除绑定
                if(mService!=null) {
                    mService = null;
                    unbindService(conn);
                }
            }
        });

        // 获取数据
        btnGetDatas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    // 通过绑定服务传递的Binder对象，获取Service暴露出来的数据
                    Log.i(TAG, "从服务端获取数据：" + mService.getCount());
                } else {
                    Log.i(TAG, "还没绑定呢，先绑定,无法从服务端获取数据");
                }
            }
        });


        conn = new ServiceConnection() {
            /**
             * 与服务器端交互的接口方法 绑定服务的时候被回调，在这个方法获取绑定Service传递过来的IBinder对象，
             * 通过这个IBinder对象，实现宿主和Service的交互。
             */
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "绑定成功调用：onServiceConnected");
                // 获取Binder
                LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
                mService = binder.getService();
            }
            /**
             * 当取消绑定的时候被回调。但正常情况下是不被调用的，它的调用时机是当Service服务被意外销毁时，
             * 例如内存的资源不足时这个方法才被自动调用。
             */
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "解除绑定调用：onServiceDisconnected");
                mService=null;
            }
        };


        final Intent foregroundIntent = new Intent(this, ForegroundService.class);

        Button btnStartForeground= findViewById(R.id.startForeground);
        btnStartForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foregroundIntent.putExtra("cmd",0);//0,开启前台服务,1,关闭前台服务
                startService(foregroundIntent);
                bindService(foregroundIntent, foregroundServiceConnect, BIND_AUTO_CREATE);
            }
        });

        Button btnStopForeground= findViewById(R.id.stopForeground);
        btnStopForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foregroundIntent.putExtra("cmd",1);//0,开启前台服务,1,关闭前台服务
                unbindService(foregroundServiceConnect);
                stopService(foregroundIntent);
            }
        });

        Button btnSendNotification = findViewById(R.id.sendNotification);
        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(foregroundService != null) {
                    Random rand = new Random();
                    int i = rand.nextInt();
                    foregroundService.updateMessage(String.valueOf(i));
                }*/
                foregroundService.warn();
            }
        });

        Button btnTestBroadcast = findViewById(R.id.testbroadcast);
        btnTestBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BroadcastActivity.class);
                startActivity(intent);
            }
        });

        Button btnQQRegister = findViewById(R.id.registerusingqq);
        btnQQRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button startThread = findViewById(R.id.btn_startthread);
        startThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataObj = new DataObject();
                dataObj.start();

            }
        });

        Button stopThread = findViewById(R.id.btn_stopthread);
        stopThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataObj.stop();
            }
        });


        // 更新工具条Title
        toolbar.setTitle("MainActivity");

        if(BuildConfig.DEBUG)
          ViseLog.e("hi%s--%d");

        checkPermissions();

        testLitePal();
    }

    public void initialize1() {
        ViseLog.e(Environment.getExternalStorageDirectory().getPath());
        ViseLog.e(MyApplication.getContext().getExternalFilesDir(null));
        File DIR_WECHAT_DOWNLOAD = new File(Environment.getExternalStorageDirectory().getPath() + "/tencent/MicroMsg/Download");
        File[] files = DIR_WECHAT_DOWNLOAD.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".mp3");
            }
        });
        for(File file : files) {
            ViseLog.e(file.getPath());
        }
    }

    // 检查权限
    private void checkPermissions() {
        List<String> permission = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permission.add(ACCESS_COARSE_LOCATION);
            }
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(permission.size() != 0)
            ActivityCompat.requestPermissions(this, permission.toArray(new String[0]), 1);
        else
            initialize1();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for(int result : grantResults) {
                    if(result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "没有必要的权限，程序无法正常运行", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    }
                }
                break;
        }

        initialize1();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(START_TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        menuSwitch = menu.findItem(R.id.toolbar_switch);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(START_TAG, "onPrepareOptionsMenu()");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(START_TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.toolbar_switch:
                Log.i(START_TAG, "select menuitem switch.");
                break;

        }
        return true;
    }

    @Override
    protected void onStart() {
        Log.i(START_TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(START_TAG, "onResume()");
        super.onResume();
    }

    private void registerusingqq() {
        Platform plat = ShareSDK.getPlatform(QQ.NAME);
        if (plat == null && !plat.isClientValid()) {
            Toast.makeText(this, "无法登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断指定平台是否已经完成授权
        if(plat.isAuthValid()) {
            String userId = plat.getDb().getUserId();
            if (userId != null) {
                Toast.makeText(this, "已经授权", Toast.LENGTH_SHORT).show();
                login(plat.getName(), userId, null);
                return;
            }
        }
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                    login(platform.getName(), platform.getDb().getUserId(), hashMap);
                }
                System.out.println(hashMap);
                System.out.println("------User Name ---------" + platform.getDb().getUserName());
                System.out.println("------User ID ---------" + platform.getDb().getUserId());
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(Platform platform, int action) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Toast.makeText(MainActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(false);
        //获取用户资料
        plat.authorize();
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Toast.makeText(MainActivity.this, "开始登陆", Toast.LENGTH_SHORT).show();
    }

    private void testLitePal(){
        Person p1 = new Person();
        p1.name = "cm1";
        p1.save();
        Book b1 = new Book();
        b1.person = p1;
        b1.price = 2;
        b1.save();

        List<Book> headList = LitePal.findAll(Book.class);
        ViseLog.e(headList);
    }
}
