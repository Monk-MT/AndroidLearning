package com.cmt.geoquizchallenge;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-01-15:12
 */
public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mIsAnswered;
    private boolean mIsChooseRight;
    private boolean mIsCheat;

    public Question(int mTextResId, boolean mAnswerTrue) {
        this.mTextResId = mTextResId;
        this.mAnswerTrue = mAnswerTrue;
        mIsAnswered = false;
        mIsChooseRight = false;
        mIsCheat = false;
    }

    public boolean isAnswered() {
        return mIsAnswered;
    }

    public void setAnswered(boolean answered) {
        mIsAnswered = answered;
    }

    public boolean isChooseRight() {
        return mIsChooseRight;
    }

    public void setChooseRight(boolean chooseRight) {
        mIsChooseRight = chooseRight;
    }

    public boolean isCheat() {
        return mIsCheat;
    }

    public void setCheat(boolean cheat) {
        mIsCheat = cheat;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
