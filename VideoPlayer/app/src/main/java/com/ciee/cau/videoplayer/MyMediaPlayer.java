package com.ciee.cau.videoplayer;

import android.media.MediaPlayer;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description:
 * @Date 2021/5/27 16:44
 */
public class MyMediaPlayer extends MediaPlayer implements LifecycleObserver {
    public MyMediaPlayer() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pausePlayer() {
        pause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumePlayer() {
        start();
    }
}
