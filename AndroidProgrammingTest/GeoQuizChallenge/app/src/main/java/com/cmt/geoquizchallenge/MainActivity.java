package com.cmt.geoquizchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String KEY_INDEX = "index";
    public static final String KEY_ANSWERED = "answered";
    public static final String KEY_CHEATER = "cheater";
    private static final int REQUEST_CODE_CHEAT = 0;

    public Button mRightButton;
    public Button mFalseButton;
//    public Button mPreviousButton;
//    public Button mNextButton;
    public ImageButton mPreviousButton;
    public ImageButton mNextButton;
    public TextView mQuestionTextView;
    public Button mCheatButton;
    public TextView mPageShowTextView;

    private final Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, true),
            new Question(R.string.question_africa, true),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int mAnswerQuestionCount = 0;
    private boolean mIsCheater;
    private int mCheatTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) { // 取出保存的数据
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mAnswerQuestionCount = savedInstanceState.getInt(KEY_ANSWERED, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER, false);
        }

        mPageShowTextView = findViewById(R.id.page_show_text);
        mQuestionTextView = findViewById(R.id.question_text);
        updateQuestion();
        mQuestionTextView.setOnClickListener(v -> {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            updateQuestion();
        });

        mRightButton = findViewById(R.id.true_button);
        mRightButton.setOnClickListener(v -> checkAnswer(true));

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(v -> checkAnswer(false));

        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(v -> {
            if (mCurrentIndex == 0 ) {
                mCurrentIndex = mQuestionBank.length - 1;
            } else {
                mCurrentIndex--;
            }
            updateQuestion();
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(v -> {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            updateQuestion();
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(v -> {
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue,mQuestionBank[mCurrentIndex].isCheat(),mCheatTimes);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });
    }

    private void updateQuestion(){
//        Log.d(TAG, "Updating question text ", new Exception()); // 会抛出异常的日志文件
        mIsCheater = mQuestionBank[mCurrentIndex].isCheat();
        mPageShowTextView.setText((mCurrentIndex + 1) + "/" + mQuestionBank.length);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean answer){
        int toastResId;
        if (mQuestionBank[mCurrentIndex].isAnswered()) {
            Log.d(TAG, String.valueOf(mQuestionBank[mCurrentIndex].isChooseRight()));
            if (mQuestionBank[mCurrentIndex].isChooseRight()) {
                toastResId = R.string.repeat_answer_true;
            } else {
                toastResId = R.string.repeat_answer_false;
            }
        } else {
            mAnswerQuestionCount++;
            mQuestionBank[mCurrentIndex].setAnswered(true);
            if (mIsCheater) {
                toastResId = R.string.judgment_toast;
                mQuestionBank[mCurrentIndex].setCheat(true);
            } else {
                if (answer == mQuestionBank[mCurrentIndex].isAnswerTrue()) {
                    mQuestionBank[mCurrentIndex].setChooseRight(true);
                    toastResId = R.string.correct_toast;
                } else {
                    toastResId = R.string.incorrect_toast;
                }
            }
        }
        showToast(toastResId);
    }

    private void showToast(int toastResId) {
        Toast toast = new Toast(MainActivity.this);
        View toastLayout = getLayoutInflater().inflate(R.layout.toast_layout,null);
        //使用setView()来为Toast加载一个布局，View类型的参数
        toast.setView(toastLayout);

        View view = toast.getView();
        if(view!=null){
            TextView mToastText = view.findViewById(R.id.toast_text);
            mToastText.setText(toastResId);
        }

        // setGravity()不能使用在纯文字的Toast上，需要使用自定义布局
        toast.setGravity(Gravity.TOP,0,400);
        toast.show();
    }

    @Override // 处理其他Activity返回的数据
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheatTimes = CheatActivity.getcheatTimes(data);
            Log.d("CheatActivity", "onActivityResult" + mCheatTimes);
            mQuestionBank[mCurrentIndex].setCheat(true);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override // 在活动停止时保存数据
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState2()");
        outState.putInt(KEY_INDEX,mCurrentIndex);
        outState.putInt(KEY_ANSWERED,mAnswerQuestionCount);
        outState.putBoolean(KEY_CHEATER,mIsCheater);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}