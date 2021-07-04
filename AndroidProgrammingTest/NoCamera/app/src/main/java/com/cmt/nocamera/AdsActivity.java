package com.cmt.nocamera;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmt.nocamera.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdsActivity extends AppCompatActivity {

    String requestBingPic = "http://guolin.tech/api/bing_pic";
    private ImageView logInPicImg;
    private TextView adsSkip;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) { // 设置状态栏隐藏
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_ads);

        ActionBar actionBar = getSupportActionBar(); // 隐藏标题栏
        if (actionBar != null) {
            actionBar.hide();
        }

        //初始化控件
        logInPicImg = (ImageView) findViewById(R.id.login_picture);
        adsSkip = (TextView) findViewById(R.id.ads_skip);


        String LogInPicPath = getFilesDir().getAbsolutePath() + "/log_in_picture.jpg"; // 获取本地缓存文件的存储路径
        ShowLogInPicture(LogInPicPath); // 加载图片

        countDownTimer = new CountDownTimer(3000, 1000) { // 计时器，共5s，每1s更新一次
            @Override
            public void onTick(long millisUntilFinished) { // 每次更新
                adsSkip.setText("即将开始" + (millisUntilFinished/1000) + "秒");
            }

            @Override
            public void onFinish() { // 计时结束
                //需要加入检测，判断是否跳过登录
                Intent intent = new Intent(AdsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();

        adsSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();

                //需要加入检测，判断是否跳过登录
                Intent intent = new Intent(AdsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 加载启动页面的图片
     * @param PicturePath
     */
    private void ShowLogInPicture(String PicturePath) {

        LogUtil.v("AdsActivity", PicturePath);
        File logInPicFile = new File(PicturePath); // 创建本地缓存文件的File
        LogUtil.d("AdsActivity", "logInPicFile.exists() = " + logInPicFile.exists());
        if (logInPicFile.exists()) { // 判断文件是否存在，存在则直接加载，不存在则从网络上获取
            LogUtil.v("AdsActivity", "Load in file：" + PicturePath);
            Glide.with(AdsActivity.this).load(PicturePath).into(logInPicImg); //加载图片到控件
            long lastModified = logInPicFile.lastModified(); // 获取上次修改时间
            long currentTimeMillis = System.currentTimeMillis(); // 获取当前时间
            if ((currentTimeMillis - lastModified) > 86400000) { //修改时间大于1天则加载新图片
                loadLogInPicture();
                LogUtil.v("AdsActivity", "modify due to over one day");
            }
        } else {
            String bingPic = PreferenceManager.getDefaultSharedPreferences(AdsActivity.this).getString("bing_pic", null); // 从Preference（键值对存储）中读取图片url
            if (bingPic != null) {// 有缓存则加载，没缓存则获取
                Glide.with(AdsActivity.this).load(bingPic).into(logInPicImg); //加载图片到控件
                savePictureToSD(bingPic);
                LogUtil.v("AdsActivity", "Load in Preference：" + bingPic);
            } else {
                loadLogInPicture();
            }
        }
    }


    /**
     * 获取必应每日一图的url到Preference（键值对存储）和内部存储中，并加载图片
     */
    private void loadLogInPicture () {
        LogUtil.v("AdsActivity", "Load in url");
        new Thread(new Runnable() { // 开启新线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()  //存放要发送参数
                            .url(requestBingPic)
                            .build();
                    LogUtil.d("AdsActivity", requestBingPic);
                    Response response = client.newCall(request).execute(); // 发送请求
                    final String bingPic = response.body().string(); // 解析出返回数据，为图片uri
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AdsActivity.this).edit(); //获取Preference（键值对存储）存放实例
                    editor.putString("bing_pic",bingPic); // 添加数据
                    editor.apply(); // 把图片uri存入Preference（键值对存储）中
                    runOnUiThread(new Runnable() { // 跳到主线程
                        @Override
                        public void run() {
                            Glide.with(AdsActivity.this).load(bingPic).into(logInPicImg); //加载图片到控件
                        }
                    });
                    savePictureToSD(bingPic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 存储图片文件，减少打开应用时图片的加载时间
     */
    private void savePictureToSD (String bingPic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.v("AdsActivity", "Save Picture to SD");
                try {
                    BitmapDrawable bitmap = (BitmapDrawable) Glide.with(AdsActivity.this).load(bingPic).submit().get(); // 下载图片
                    Bitmap bm = bitmap.getBitmap(); //转化为bitmap
                    BufferedOutputStream bof = null;
                    try {
                        bof = new BufferedOutputStream(openFileOutput("log_in_picture.jpg", Context.MODE_PRIVATE)); //获取文件缓冲流
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, bof); // 保存文件
                        LogUtil.v("AdsActivity", "Save success");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (bof != null) {
                            try {
                                bof.close(); // 关闭流
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

