package com.cmt.nocamera;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmt.nocamera.db.Video;
import com.cmt.nocamera.dummy.DummyContent.DummyItem;
import com.cmt.nocamera.util.LogUtil;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private final List<Video> mVideoList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView videoImageView;
        TextView videoContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            videoImageView = (ImageView) view.findViewById(R.id.video_picture);
            videoContentView = (TextView) view.findViewById(R.id.video_content);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + videoContentView.getText() + "'";
        }
    }

    public VideoRecyclerViewAdapter(List<Video> videoList) {
        mVideoList = videoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_video, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.videoContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Video video = mVideoList.get(position);
                //响应点击
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Video video = mVideoList.get(position);
        holder.videoImageView.setImageResource(R.drawable.ic_done);
        holder.videoContentView.setText(video.getName());
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
}