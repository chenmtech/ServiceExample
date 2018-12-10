package com.cmtech.android.serviceexample;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cmtech.android.serviceexample.broadcastreceiver.DynamicReceiver;

public class BroadcastActivity extends AppCompatActivity {

    private DynamicReceiver dynamicReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        Button btnStartDynamic = findViewById(R.id.btn_dynamicbroadcast);
        btnStartDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                dynamicReceiver = new DynamicReceiver();
                registerReceiver(dynamicReceiver, intentFilter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(dynamicReceiver);
    }
}
