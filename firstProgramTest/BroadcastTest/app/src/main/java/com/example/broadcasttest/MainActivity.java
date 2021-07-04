package com.example.broadcasttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcasttest.MY_BROADCAST");
                // 在Android8.0上突破隐式广播的限制
                intent.addFlags(0x01000000);

                // 因为在 Android 8.0 之后，对于广播的发送与接收变严格了，需要加入Component参数
                // 只有接收的是自定义广播且为静态注册的广播接收器，才需要添加setComponent()函数
                // 第一个参数为MyBroadcastReceiver这个广播接收器的包路径名
                // 第二个参数为MyBroadcastReceiver广播接收器的类路径名
                intent.setComponent(new ComponentName("com.example.broadcasttest",
                        "com.example.broadcasttest.MyBroadcastReceiver"));
                sendOrderedBroadcast(intent,null); //第二个参数为与权限有关的字符串
            }
        });
        // 注册广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE"); // action 为要监听的广播
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册，动态注册的广播一定要取消注册
        unregisterReceiver(networkChangeReceiver);
    }

    // 广播接收器
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, "network is available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }
}