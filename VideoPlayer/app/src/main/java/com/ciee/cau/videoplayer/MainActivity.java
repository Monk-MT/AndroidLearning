package com.ciee.cau.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private PlayerViewModel mPlayerViewModel;
    private ProgressBar mProgressBar;
    private SurfaceView mPlayerFrame;
    private FrameLayout mFrameLayout;
    private FrameLayout mControllerFram;
    private SeekBar mSeekBar;
    private ImageView mControllerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progress_bar);
        mFrameLayout = findViewById(R.id.frame_layout);
        mControllerFram = findViewById(R.id.controller_frame);
        mSeekBar = findViewById(R.id.seek_bar);
        mControllerButton = findViewById(R.id.controller_button);

        updatePlayerProgress();

        mPlayerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        mPlayerViewModel.progressBarVisibility.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                mProgressBar.setVisibility(visibility);
            }
        });

        getLifecycle().addObserver(mPlayerViewModel.getMediaPlayer());

        mPlayerViewModel.videoResolution.observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> pair) {
                mSeekBar.setMax(mPlayerViewModel.getMediaPlayer().getDuration());
                mPlayerFrame.post(new Runnable() {
                    @Override
                    public void run() {
                        resizePlayer(pair.first, pair.second);
                    }
                });
            }
        });

        mPlayerViewModel.controllerFrameVisibility.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                mControllerFram.setVisibility(visibility);
            }
        });

        mPlayerViewModel.bufferPercent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer percent) {
                mSeekBar.setSecondaryProgress(mSeekBar.getMax() * percent / 100);
            }
        });

        mPlayerViewModel.playerStatus.observe(this, new Observer<PlayerStatus>() {
            @Override
            public void onChanged(PlayerStatus playerStatus) {
                mControllerButton.setEnabled(true);
                switch (playerStatus) {
                    case Playing:
                        mControllerButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        break;
                    case Paused:
                        mControllerButton.setImageResource(R.drawable.ic_baseline_pause_24);
                        break;
                    case Completed:
                        mControllerButton.setImageResource(R.drawable.ic_baseline_replay_24);
                        break;
                    case NotReady:
                        mControllerButton.setEnabled(false);
                        break;
                    default:
                        mControllerButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
            }
        });


        mPlayerFrame = findViewById(R.id.player_frame);
        mPlayerFrame.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                mPlayerViewModel.getMediaPlayer().setDisplay(holder);
                mPlayerViewModel.getMediaPlayer().setScreenOnWhilePlaying(true);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });

        mPlayerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerViewModel.toggleControllerVisibility();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayerViewModel.playerSeekToProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerViewModel.togglePlayerStatus();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI();
            mPlayerViewModel.emmitVideoResolution();
        }
    }

    private void resizePlayer(int width, int height) {
        if (width == 0 || height == 0) return;
        mPlayerFrame.setLayoutParams(new FrameLayout.LayoutParams(
                mPlayerFrame.getHeight() * width / height,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        ));
    }

    private void updatePlayerProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mPlayerViewModel.getMediaPlayer().getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        },500); // 延时1秒
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}