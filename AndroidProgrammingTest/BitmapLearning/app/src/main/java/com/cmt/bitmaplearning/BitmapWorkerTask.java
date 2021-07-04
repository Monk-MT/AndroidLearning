package com.cmt.bitmaplearning;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-03-30-17:32
 */
public class BitmapWorkerTask extends AsyncTask<Integer, Bitmap, Bitmap> {
    private final WeakReference imageWeakReference;
    private int data = 0;

    public int getData() {
        return data;
    }

    /**
     * 创建一个弱引用(WeakReference)，使ImageView可以被垃圾回收
     * @param imageView ImageView控件id
     */
    public BitmapWorkerTask(ImageView imageView) {
        imageWeakReference = new WeakReference(imageView);
    }


    /**
     * 在后台执行解码操作
     * @param params 要加载的图片id
     * @return
     */
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return decodeSampledBitmapFromResource(MyApplication.getContext().getResources(), data, 300, 300);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        //由于当任务结束时不能确保ImageView仍然存在，因此我们必须对引用进行检查
        if (imageWeakReference != null && bitmap != null) {
            final ImageView imageView = (ImageView) imageWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask = AsyncDrawable.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 解码图片
     * @param res 图片资源
     * @param resId 图片id
     * @param reqWidth 解码后的宽度
     * @param reqHeight 解码后的高度
     * @return 返回解码后的图片
     */
    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        //先解码获得图片尺寸，此时图片没有加到内存中
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //不加载图片
        BitmapFactory.decodeResource(res, resId, options);

        //获得图片缩放比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //解码图片
        options.inJustDecodeBounds = false; //加载图片
        return BitmapFactory.decodeResource(res, resId, options);
    }


    /**
     * 获得图片的缩放比例
     * @param options 图片的options
     * @param reqWith 解码后的宽度
     * @param reqHeight 解码后的高度
     * @return 缩放比例
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWith, int reqHeight) {
        //获取图片的原始大小
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1; //图片缩放比例

        if (height > reqHeight || width > reqWith) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWith) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
