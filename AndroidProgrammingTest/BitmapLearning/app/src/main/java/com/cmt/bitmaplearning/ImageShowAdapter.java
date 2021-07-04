package com.cmt.bitmaplearning;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-03-31-15:08
 */
public class ImageShowAdapter extends RecyclerView.Adapter<ImageShowAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{
        View imageShowView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageShowView = view;
            imageView = (ImageView) view.findViewById(R.id.image_show);
        }
    }

    public ImageShowAdapter() {
    }

    @NonNull
    @Override
    public ImageShowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_show_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageShowAdapter.ViewHolder holder, int position) {
        AsyncDrawable.loadBitmap(R.drawable.test_image, holder.imageView);
    }

    @Override
    public int getItemCount() {
        return 30;
    }
}
