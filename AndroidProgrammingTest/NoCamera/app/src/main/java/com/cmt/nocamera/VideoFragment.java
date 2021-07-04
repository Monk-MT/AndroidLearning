package com.cmt.nocamera;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmt.nocamera.db.Video;
import com.cmt.nocamera.util.LogUtil;


import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class VideoFragment extends Fragment {

    private int mColumnCount = 1;
    private List<Video> videoList = new ArrayList<>();
    private VideoRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //先从数据库获取数据,再添加到列表
//        videoList = LitePal.findAll(Video.class);

        for (int i = 0; i < 20; i++) {
            Video video = new Video();
            video.setName(String.valueOf(i) + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            videoList.add(video);
        }


        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.video_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new VideoRecyclerViewAdapter(videoList);
        recyclerView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh); // 下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary); // 下拉刷新进度条颜色
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // 下拉监听器
            @Override
            public void onRefresh() {
                //处理刷新逻辑
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        return view;
    }
}