package com.example.activitytest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FirstActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FirstActivity", "task id is" + getTaskId());
        setContentView(R.layout.first_layout);

        // 创建Button实例
        // 调用findViewById(id)通过id获取再布局中定义的元素，返回View对象
        Button button1 = (Button) findViewById(R.id.button_1);

        // xxx.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //            }) 创建点击监听器，监听器使用onClick()响应
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                // makeText()静态方法，创建对象，context参数为下一个活动
//                // show()显示
//                Toast.makeText(FirstActivity.this, "You clicked Buton 1",
//                        Toast.LENGTH_SHORT).show();
//                finish(); // 结束当前Activity

//                // 显示Intent
//                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
//                // 隐式Intent
//                Intent intent = new Intent("com.example.activitytest.ACTION_START");
//                intent.addCategory("com.example.activitytest.MY_CATEGORY");
//                // 隐式Intent调用其他应用(调用系统浏览器）
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://www.baidu.com"));
//                // 隐式Intent调用其他应用(调用拨号界面）
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:10086"));
//                // 使用Intent传递数据
//                String data = "Hello SecondActivity";
//                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
//                intent.putExtra("extra_data", data);
//                startActivity(intent);

//                // 使用Intent接收返回数据
//                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
//                startActivityForResult(intent, 1);

                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }


    // 创建菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 菜单栏响应
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) { //调用item.getItemId()来判断点击那个菜单项
            case R.id.add_item:
                Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(this, "You clicked Remove", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("data_return");
                    Log.d("FirstActivity", returnedData);
                }
                break;
            default:
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("FirstActivity", "onRestart");
    }
}