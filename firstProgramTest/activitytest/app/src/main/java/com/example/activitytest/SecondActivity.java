package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SecondActivity", "task id is" + getTaskId());
        setContentView(R.layout.second_layout);
//        // 获取Intent传入的数据
//        Intent intent = getIntent();
//        String data = intent.getStringExtra("extra_data");
//        Log.d("SecondActivity", data);

        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra("data_return", "Hello FirstActivity");
//                // 返回数据给上一个活动
//                setResult(RESULT_OK, intent);
//                finish();
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data_return", "Hello FirstActivity By Back");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SecondActivity", "onDestroy");
    }
}