package com.cmt.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 简单视图
 * @Date 2021/5/24 15:47
 */
public class BoxDrawingView extends View {
    public static final String TAG = "BoxDrawingView";
    public static final float ORIGIN_ANGLE = 1;

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    //使用代码创建该view时用
    public BoxDrawingView(Context context) {
        super(context);
    }

    //使用XML布局创建该view时用
    public BoxDrawingView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float botton = Math.max(box.getOrigin().y, box.getCurrent().y);
            float angle = box.getAngle();
            float centerX = (box.getOrigin().x + box.getCurrent().x) / 2.0f;
            float centerY = (box.getOrigin().y + box.getCurrent().y) / 2.0f;

            canvas.rotate(angle, centerX, centerY);
            canvas.drawRect(left, top, right, botton, mBoxPaint);
            canvas.rotate(-angle, centerX, centerY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        PointF firstOrigin = null;
        PointF secondOrigin = null;
        PointF firstCurrent = null;
        PointF secondCurrent = null;
        double slopeOrigin;
        double slopeCurrent;
        String action = "";

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                firstOrigin = current;
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN";
                secondOrigin = current;
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                switch (event.getActionIndex()) {
                    case 0:
                        if (event.getPointerCount() == 1) {
                            firstOrigin = current;
                        } else {
                            firstCurrent = current;
                        }
                        break;
                    case 1:
                        secondCurrent = current;
                }

                if (firstCurrent != null && secondCurrent != null) {
                    slopeOrigin = Math.abs(firstOrigin.y - secondOrigin.y) / Math.abs(firstOrigin.x - secondOrigin.x);
                    slopeCurrent = Math.abs(firstCurrent.y - secondCurrent.y) / Math.abs(firstCurrent.x - secondCurrent.x);
                    mCurrentBox.setAngle((float)Math.toDegrees(Math.atan(slopeCurrent - slopeOrigin)));
                }

                if (event.getPointerCount() == 1 && mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP";

                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }
}
