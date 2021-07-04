package com.cmt.geoquizchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.cmt.geoquizchallenge.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.cmt.geoquizchallenge.answer_shown";
    private static final String EXTRA_CHEAT_TIMES = "com.cmt.geoquizchallenge.answer_shown";
    private static final String EXTRA_CHEATED_FLAG = "com.cmt.geoquizchallenge.cheated_flag";
    private static final String TAG = "CheatActivity";
    private static final int CHEAT_TIMES_MAX = 3;

    private boolean mAnswerIsTrue;
    private boolean mIsAnswerShown;
    private boolean mIsCheated;
    private int mCheatTimes;

    private Button mShowAnswerButton;
    private TextView mAnswerTextView;
    private TextView mCheatTimesTextView;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean isCheated, int cheatTimes) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEATED_FLAG,isCheated);
        intent.putExtra(EXTRA_CHEAT_TIMES, cheatTimes);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getcheatTimes(Intent result) {
        return result.getIntExtra(EXTRA_CHEAT_TIMES, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        Intent intent = getIntent();
        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mIsCheated = intent.getBooleanExtra(EXTRA_CHEATED_FLAG,false);
        mCheatTimes = intent.getIntExtra(EXTRA_CHEAT_TIMES, 0);

        if (savedInstanceState != null) {
            mIsAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false);
            mCheatTimes = savedInstanceState.getInt(EXTRA_CHEAT_TIMES,0);
            setAnswerShownResult(mIsAnswerShown, mCheatTimes);
        }

        Log.d(TAG, "onCreate: " + mCheatTimes);

        mCheatTimesTextView = findViewById(R.id.cheat_times);
        mCheatTimesTextView.setText("You have " + (CHEAT_TIMES_MAX - mCheatTimes) + " times to cheat");
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(v -> {
            if (mIsCheated) {
                mAnswerTextView.setText(String.valueOf(mAnswerIsTrue));
                mIsAnswerShown = true;
            } else {
                if (mCheatTimes < CHEAT_TIMES_MAX) {
                    mCheatTimes++;
                    mCheatTimesTextView.setText("You have " + (CHEAT_TIMES_MAX - mCheatTimes) + " times to cheat");
                    mAnswerTextView.setText(String.valueOf(mAnswerIsTrue));
                    mIsAnswerShown = true;
                    setAnswerShownResult(mIsAnswerShown, mCheatTimes);
                } else {
                    Toast.makeText(CheatActivity.this, R.string.Cheating_too_much,Toast.LENGTH_SHORT).show();
                }
            }

            int cx = mShowAnswerButton.getWidth() / 2;
            int cy = mShowAnswerButton.getHeight() / 2;
            float radius = mShowAnswerButton.getWidth();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator anim;
                anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mShowAnswerButton.setVisibility(View.INVISIBLE);
                    }
                });
                anim.start();
            } else {
                mShowAnswerButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_ANSWER_SHOWN, mIsAnswerShown);
        outState.putInt(EXTRA_CHEAT_TIMES, mCheatTimes);
        Log.d(TAG, "onSaveInstanceState: " + mCheatTimes);
    }

    private void setAnswerShownResult(boolean isAnswerShown , int cheatTimes) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        intent.putExtra(EXTRA_CHEAT_TIMES,cheatTimes);
        setResult(RESULT_OK,intent);
        Log.d(TAG, "setAnswerShownResult: " + cheatTimes);
    }
}