package com.example.androidthreadtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

public static final int UPDATE_TEXT = 1;

private TextView text;

// Android11后Android 11 /R 之后创建 Handler 构造函数
// Handler() 变更为 new Handler(Looper.myLooper())
// Handler(Handler.Callback callback) 变更为 new Handler(Looper.myLooper(), callback)
private final Handler handler = new Handler(Looper.myLooper()) {
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UPDATE_TEXT:
                // 在这里进行UI操作
                text.setText("Nice to meet you");
                break;
            default:
                break;
        }
    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        Button changeText = (Button) findViewById(R.id.change_text);
        changeText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_text:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message); // 将Message对象发送出去
                    }
                }).start();
                break;
            default:
                break;
        }
    }
}