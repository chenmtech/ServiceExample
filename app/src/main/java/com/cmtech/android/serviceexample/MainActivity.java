package com.cmtech.android.serviceexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vise.log.ViseLog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String START_TAG = TAG + "启动顺序";

    private RollWaveView rollView;
    private Button updateRoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(START_TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollView = findViewById(R.id.roll_view);

        updateRoll = findViewById(R.id.btn_update_roll_view);
        updateRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollView.reset();
            }
        });

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

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Toast.makeText(MainActivity.this, "开始登陆", Toast.LENGTH_SHORT).show();
    }
}
