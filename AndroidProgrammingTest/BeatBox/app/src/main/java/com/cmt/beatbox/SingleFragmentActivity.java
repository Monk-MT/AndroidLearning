package com.cmt.beatbox;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-22-16:27
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container); //从FragementManager的队列中获取fragment
        if (fragment == null) { //若队列中没有fragment
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit(); //造一个fragment，然后添加到队列中
        }
    }
}
