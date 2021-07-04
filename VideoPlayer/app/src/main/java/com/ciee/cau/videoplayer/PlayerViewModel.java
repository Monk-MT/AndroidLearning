package com.ciee.cau.videoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelStore;

import java.io.IOException;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 播放器ViewModel
 * @Date 2021/5/27 15:39
 */

enum PlayerStatus {
    Playing, Paused, Completed, NotReady;
}
public class PlayerViewModel extends ViewModel {
    private long mControllerShowTime = 0L;
    private MyMediaPlayer mMediaPlayer;
    private MutableLiveData<PlayerStatus> mPlayerStatus = new MutableLiveData<>();
    LiveData<PlayerStatus> playerStatus = mPlayerStatus;
    private MutableLiveData<Integer> mBufferPercent = new MutableLiveData<>(0);
    LiveData<Integer> bufferPercent = mBufferPercent;
    private MutableLiveData<Integer> mControllerFrameVisibility = new MutableLiveData<>(View.VISIBLE);
    LiveData<Integer> controllerFrameVisibility = mControllerFrameVisibility;
    private MutableLiveData<Integer> mProgressBarVisibility = new MutableLiveData<>(View.VISIBLE);
    LiveData<Integer> progressBarVisibility = mProgressBarVisibility;
    private MutableLiveData<Pair<Integer, Integer>> mVideoResolution = new MutableLiveData<>(new Pair<>(0,0));
    LiveData<Pair<Integer, Integer>> videoResolution = mVideoResolution;

    public PlayerViewModel() {
        mMediaPlayer = new MyMediaPlayer();
        loadVideo();
    }

    public MyMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setMediaPlayer(MyMediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    private void loadVideo() {
        mProgressBarVisibility.setValue(View.VISIBLE);
        mPlayerStatus.setValue(PlayerStatus.NotReady);
        try {
            mMediaPlayer.setDataSource("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgressBarVisibility.setValue(View.INVISIBLE);
//                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
                mPlayerStatus.setValue(PlayerStatus.Playing);
            }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                mVideoResolution.setValue(new Pair<>(width, height));
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                mBufferPercent.setValue(percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerStatus.setValue(PlayerStatus.Completed);
            }
        });

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mMediaPlayer.start();
                mProgressBarVisibility.setValue(View.INVISIBLE);
            }
        });

        mMediaPlayer.prepareAsync();
    }

    public void togglePlayerStatus() {
        switch (mPlayerStatus.getValue()) {
            case Playing:
                mMediaPlayer.pause();
                mPlayerStatus.setValue(PlayerStatus.Paused);
                break;
            case Paused:
            case Completed:
                mMediaPlayer.start();
                mPlayerStatus.setValue(PlayerStatus.Playing);
                break;
            default:
        }
    }

    public void toggleControllerVisibility() {
        if (mControllerFrameVisibility.getValue() == View.INVISIBLE) {
            mControllerFrameVisibility.setValue(View.VISIBLE);
            mControllerShowTime = System.currentTimeMillis();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - mControllerShowTime > 3000) {
                        mControllerFrameVisibility.setValue(View.INVISIBLE);
                    }
                }
            },3000); // 延时1秒
        } else {
            mControllerFrameVisibility.setValue(View.INVISIBLE);
        }
    }

    public void emmitVideoResolution() {
        mVideoResolution.setValue(mVideoResolution.getValue());
    }

    public void playerSeekToProgress(int progress) {
        mProgressBarVisibility.setValue(View.VISIBLE);
        mMediaPlayer.seekTo(progress);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mMediaPlayer.release();
    }
}
