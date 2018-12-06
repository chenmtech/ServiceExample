package com.cmtech.android.serviceexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cmtech.android.serviceexample.service.SimpleService;

public class MainActivity extends AppCompatActivity {
    private Button btnStartService;
    private Button btnStopService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = findViewById(R.id.btn_startservice);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SimpleService.class);
                startService(intent);
            }
        });

        btnStopService = findViewById(R.id.btn_stopservice);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SimpleService.class);
                stopService(intent);
            }
        });
    }
}
