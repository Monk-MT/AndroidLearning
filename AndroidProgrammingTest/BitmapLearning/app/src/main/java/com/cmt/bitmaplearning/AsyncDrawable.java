package com.cmt.bitmaplearning;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-03-31-14:22
 */
public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
        super(res, bitmap);
        this.bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
    }

    /**
     * @return 获取图片加载器
     */
    public BitmapWorkerTask getBitmapWorkerTask(){
        return (BitmapWorkerTask) bitmapWorkerTaskReference.get();
    }

    /**
     * 加载图片resId到控件imageView中
     * @param resId
     * @param imageView
     */
    public static void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId,imageView)) { // 取消掉之前在加载的图片
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView); //创建加载图片的子线程
            final AsyncDrawable asyncDrawable = new AsyncDrawable(MyApplication.getContext().getResources(),
                    BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.image), task); // 使用AsyncDrawable来管理图片的加载
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }

    /**
     * 取消掉已加载的图片
     * @param data
     * @param imageView
     * @return 是否需要重新加载图片
     */
    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView); // 获取加载图片所用的加载器（线程）
        if (bitmapWorkerTask != null) { // 如果有正在加载的图片
            final int bitmapData = bitmapWorkerTask.getData(); //获取正在加载图片的id
            if (bitmapData == 0 || bitmapData != data) { //id和要加载的图片不符，则取消掉之前的加载
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取加载图片所用的加载器（线程）
     * @param imageView
     * @return
     */
    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}

