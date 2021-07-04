package com.cmt.nocamera;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmt.nocamera.db.Audio;
import com.cmt.nocamera.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AudioRecyclerViewAdapter extends RecyclerView.Adapter<AudioRecyclerViewAdapter.ViewHolder> {

    private final List<Audio> mAudioList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView audioImageView;
        public final TextView audioContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            audioImageView = (ImageView) view.findViewById(R.id.audio_picture);
            audioContentView = (TextView) view.findViewById(R.id.audio_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + audioContentView.getText() + "'";
        }
    }

    public AudioRecyclerViewAdapter(List<Audio> audioList) {
        mAudioList = audioList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_audio, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.audioContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Audio audio = mAudioList.get(position);
                //响应点击
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Audio audio = mAudioList.get(position);
        holder.audioImageView.setImageResource(R.drawable.apple);
        holder.audioContentView.setText(audio.getName());
    }

    @Override
    public int getItemCount() {
        return mAudioList.size();
    }
}