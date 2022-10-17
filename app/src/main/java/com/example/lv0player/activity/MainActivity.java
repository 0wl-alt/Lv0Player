package com.example.lv0player.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.lv0player.R;
import com.example.lv0player.service.MusicService;
import com.example.lv0player.ui.home.HomeFragment;
import com.example.lv0player.ui.like.LikeFragment;
import com.example.lv0player.ui.localmusic.LocalMusicFragment;
import com.example.lv0player.util.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MusicService.MusicPlayBinder mBinder;
    private List<Fragment> mFragments;
    private ViewPager viewPager;
    private int curIndex=0;
    private Fragment mCurrentFrgment;
    private ImageView btn_play;
    private TextView bar_music_name;
    private TextView bar_artist_name;
    private View play_bar;
    private ImageView bar_album_pic;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (MusicService.MusicPlayBinder) iBinder;
            if(mBinder.isPlaying()){
                btn_play.setImageResource(R.drawable.ic_music_pause_black);
                bar_music_name.setText(mBinder.getMusicName());
                bar_artist_name.setText("--"+mBinder.getArtistName());
                Glide.with(MainActivity.this)
                        .load(mBinder.getPicUrl())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(bar_album_pic);
            }else{
                btn_play.setImageResource(R.drawable.ic_music_play);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_activity();
        initFragments();
        init_bottom_view();
        set_bottom_play_bar();

        Intent intent = new Intent(this,MusicService.class);
        startService(intent);
        Intent bindIntent = new Intent(this,MusicService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
    }

    private void init_activity(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        bar_music_name = findViewById(R.id.bar_music_name);
        bar_artist_name = findViewById(R.id.bar_artist_name);
        bar_album_pic = findViewById(R.id.bar_music_album);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new LikeFragment(bar_album_pic));
        mFragments.add(new LocalMusicFragment());
        // 初始化展示
        setFragmentPosition(0);
    }

    private void init_bottom_view(){
        BottomNavigationView navView = findViewById(R.id.nav_bottom_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragmentPosition(0);
                        break;
                    case R.id.navigation_like:
                        setFragmentPosition(1);
                        break;
                    case R.id.navigation_local:
                        setFragmentPosition(2);
                        break;
                    default:
                        break;
                }
                // 这里注意返回true,否则点击失效
                return true;
            }
        });
    }

    private void set_bottom_play_bar(){
        play_bar = findViewById(R.id.play_bar);
        btn_play = findViewById(R.id.bar_play_btn);
        play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
                intent.putExtra("id",mBinder.getMusicId());
                intent.putExtra("music_name",mBinder.getMusicName());
                intent.putExtra("artist_name",mBinder.getArtistName());
                // 服务器地址
                String url = "http://XXXXXXXX/song/detail?ids=" + mBinder.getMusicId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String picUrl = HttpRequest.GetPicUrl(url);
                            intent.putExtra("picUrl",picUrl);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBinder.isExistPlayer()){
                    // 之前处于暂停状态
                    if(!mBinder.isPlaying()){
                        btn_play.setImageResource(R.drawable.ic_music_pause_black);
                        mBinder.continueMusic();
                    }else{
                        // 之前正在播放
                        btn_play.setImageResource(R.drawable.ic_music_play);
                        mBinder.pauseMusic();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从其他 Activity 返回需改变播放状态
        if(mBinder!=null){
            if(mBinder.isPlaying()){
                btn_play.setImageResource(R.drawable.ic_music_pause_black);
                bar_music_name.setText(mBinder.getMusicName());
                bar_artist_name.setText("--"+mBinder.getArtistName());
            }else{
                btn_play.setImageResource(R.drawable.ic_music_play);
            }
        }
    }

    private void setFragmentPosition(int position) {
        curIndex = position;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // 隐藏原有的 Fragment
        if(mCurrentFrgment!=null){
            ft.hide(mCurrentFrgment);
        }

        // 获取之前添加的 Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(mFragments.get(curIndex).getClass().getName());
        // 之前未添加过
        if(fragment == null){
            fragment = mFragments.get(position);
        }
        mCurrentFrgment = fragment;
        // 判断是否已经添加到 FragmentTranscation
        if (!fragment.isAdded()) {
            ft.add(R.id.ll_frameLayout, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this, MusicSearchActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,MusicService.class);
        unbindService(connection);
        stopService(intent);
    }
}