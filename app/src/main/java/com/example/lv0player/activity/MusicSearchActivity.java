package com.example.lv0player.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.lv0player.MusicAdapter;
import com.example.lv0player.R;
import com.example.lv0player.model.MusicSearchInfo;
import com.example.lv0player.service.MusicService;
import com.example.lv0player.util.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class MusicSearchActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private MusicSearchInfo musicSearchInfo;
    private List<MusicSearchInfo.ResultDTO.SongsDTO> songsDTOList=null;
    MusicAdapter adapter;
    private Handler uiHandler = new Handler();
    private SearchView searchView;
    private MenuItem menuItem;
    private MusicService.MusicPlayBinder mBinder=null;
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
                Glide.with(MusicSearchActivity.this)
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
        setContentView(R.layout.activity_music_search);
        init_activity();
        Intent bindIntent = new Intent(this,MusicService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicSearchActivity.this, MusicPlayActivity.class);
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
        // 从 playActivity 返回需改变播放状态
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("搜索");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query != null) {
                    // 服务器地址
                    String url = "http://XXXXXXX/search?keywords=" + query;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                musicSearchInfo = HttpRequest.GetMusicSearchInfo(url);
                                // 用 handler 更新 ui 组件
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (musicSearchInfo != null) {
                                            songsDTOList.addAll(musicSearchInfo.getResult().getSongs());
                                            adapter.notifyDataSetChanged();
                                        }
                                        searchView.clearFocus();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                songsDTOList.clear();
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void init_activity(){
        recyclerView = findViewById(R.id.music_search_list);
        btn_play = findViewById(R.id.bar_play_btn);
        bar_music_name = findViewById(R.id.bar_music_name);
        bar_artist_name = findViewById(R.id.bar_artist_name);
        play_bar = findViewById(R.id.play_bar);
        bar_album_pic = findViewById(R.id.bar_music_album);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        songsDTOList = new ArrayList<MusicSearchInfo.ResultDTO.SongsDTO>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(MusicSearchActivity.this);
        adapter = new MusicAdapter(this,songsDTOList,bar_album_pic);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}