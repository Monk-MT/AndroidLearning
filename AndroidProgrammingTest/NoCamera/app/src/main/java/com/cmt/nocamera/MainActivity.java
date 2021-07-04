package com.cmt.nocamera;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBar actionBar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // 标题栏
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_view);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // 显示导航按钮HomeAsUp（默认返回），下面会更改点击事件
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); //设置导航按钮图标
        }
        navView.setCheckedItem(R.id.nav_call); // 设置滑动菜单默认选中项
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override // 处理滑动菜单点击事件，这里没写
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                return true;
            }
        });
        viewPager = findViewById(R.id.main_pager);
        tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 2;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position){
                    case 0:
                        return new VideoFragment();
                    case 1:
                        return new AudioFragment();
                }
                return new VideoFragment();
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Vedio");
                        break;
                    case 1:
                        tab.setText("Audio");
                        break;
                }
            }
        }).attach();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //悬浮按钮
        fab.setOnClickListener(new View.OnClickListener() { // 悬浮按钮点击事件
            @Override
            public void onClick(View v) {
                //处理悬浮菜单点击事件
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) { // 加载Toolbar中的菜单
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // 响应Toolbar菜单的点击事件
        switch (item.getItemId()) {
            case android.R.id.home: // 响应HomeAsUp按钮的点击事件
                drawerLayout.openDrawer(GravityCompat.START); //显示滑动菜单
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
