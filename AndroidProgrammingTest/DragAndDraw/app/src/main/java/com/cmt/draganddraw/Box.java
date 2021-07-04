package com.cmt.draganddraw;

import android.graphics.PointF;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 用于表示一个矩形框的定义数据
 * @Date 2021/5/24 16:08
 */
public class Box {
    private PointF mOrigin;
    private PointF mCurrent;
    private float mAngle;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
        mAngle = 0.0f;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
}
