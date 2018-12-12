package com.cmtech.android.serviceexample;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cmtech.android.serviceexample.service.ForegroundService;
import com.cmtech.android.serviceexample.service.LocalService;
import com.cmtech.android.serviceexample.service.SimpleService;

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
            }
        });

        Button btnStopForeground= findViewById(R.id.stopForeground);
        btnStopForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foregroundIntent.putExtra("cmd",1);//0,开启前台服务,1,关闭前台服务
                stopService(foregroundIntent);
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


        // 更新工具条Title
        toolbar.setTitle("MainActivity");
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
}
