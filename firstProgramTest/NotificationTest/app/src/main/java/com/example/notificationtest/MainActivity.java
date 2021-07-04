package com.example.notificationtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendNotice = (Button) findViewById(R.id.send_notice);
        sendNotice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_notice:
                // 设置点击事件
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                // 添加渠道

                if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    //只在Android O之上需要渠道，这里的第一个参数要和下面的channelId一样

                    // 方法一
                    String channelId = createNotificationChannel("my_channel_ID",
                            "my_channel_NAME", NotificationManager.IMPORTANCE_HIGH);
                    // 方法二
                    NotificationChannel notificationChannel = new NotificationChannel("my_channel_ID",
                            "my_channel_NAME", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                // 设计通知
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "my_channel_ID")
                    .setContentTitle("通知") // 通知标题
                    .setContentText("收到一条消息") // 通知内容
                    .setContentIntent(pendingIntent) //设置点击事件
                    .setSmallIcon(R.mipmap.ic_launcher) // 小图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)) // 大图标
                    //.setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                    //.setVibrate(new long[] {0, 1000, 1000, 1000}) //震动
                    //.setLights(Color.GREEN, 1000, 1000) // LED闪烁
                    .setDefaults(NotificationCompat.DEFAULT_ALL) // 默认设置
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 优先级
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("此处可以放长文字，但会覆盖setContentText()中的内容"))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.big_image)))
                    .setAutoCancel(true); // 点击后自动关闭通知
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                // 创建通知
                notificationManager.notify(1, notification.build());
                break;
            default:
                break;
        }
    }

    // 创建渠道
    private String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }

}