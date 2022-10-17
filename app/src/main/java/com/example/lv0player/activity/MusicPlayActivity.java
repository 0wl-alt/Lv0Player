package com.example.lv0player.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.lv0player.R;
import com.example.lv0player.service.MusicService;
import com.example.lv0player.util.TimeTransUtil;

public class MusicPlayActivity extends AppCompatActivity{

    private ImageView btnPlay;
    private SeekBar seekBar;
    private long music_id;
    private Handler handler;
    private TextView music_duration;
    private TextView music_played;
    private String picUrl="";
    private ImageView playing_mode;
    private ImageView btn_like;
    private int progress_g=0;
    private int progress_stop=0;
    private MusicService.MusicPlayBinder mBinder;
    ServiceBroadcastReceiver mReceiver;
    private String music_name="";
    private String artist_name="";

    public static final int GET_DURATION=0;
    public static final int GET_PROGRESS=1;
    public static final int GET_BUFFERING_LEVEL=2;
    public static final int COMPLETION=3;

    public static final int SEND_PROGRESS=5;
    public static final int SEND_PROGRESS_STOP_TOUCH=6;
    public static final int SEND_MUSIC_INFO=7;

    public static final String BROADCAST_MUSIC_SERVICE_CONTROL = "MusicService.CONTROL";
    public static final String BROADCAST_MUSIC_SERVICE_PROGRESS = "MusicService.PROGRESS";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (MusicService.MusicPlayBinder) iBinder;
            int duration = mBinder.getDuration();
            // 如果 musicInfo 和原来不一样要销毁原来的状态
            // 这里只判断下 musicId
            if(mBinder.getMusicId()!=music_id && mBinder.getMusicId()!=0){
                mBinder.stopMusic();
                duration=0;
                seekBar.setMax(duration);
                music_duration.setText(TimeTransUtil.millsToTime(duration));
                seekBar.setProgress(0);
                music_played.setText(TimeTransUtil.millsToTime(0));
            }else{
                seekBar.setProgress(mBinder.getProgress());
                music_played.setText(TimeTransUtil.millsToTime(mBinder.getProgress()));
            }
            // 每次重新进入需要判断播放状态
            if(mBinder.isPlaying()){
                btnPlay.setImageResource(R.drawable.ic_music_pause_white);
            }else{
                btnPlay.setImageResource(R.drawable.ic_music_play_white);
            }
            // 重新进入设置 duration
            if(duration>0) {
                seekBar.setMax(duration);
                music_duration.setText(TimeTransUtil.millsToTime(duration));
            }
            // 重新设置 playingmode 显示图片
            if(mBinder.getPlayingMode()==0){
                playing_mode.setImageResource(R.drawable.ic_music_repeat_white);
            }
            else if(mBinder.getPlayingMode()==1){
                playing_mode.setImageResource(R.drawable.ic_music_repeat_one);
            }
            // 设置 like 按钮
            Uri uri =Uri.parse("content://com.example.lv0player.provider/musiclike");
            Cursor cursor = getContentResolver().query(uri,null,"id=?"
                    ,new String[]{String.valueOf(music_id)},null);
            if(cursor!=null) {
                if (cursor.moveToNext()) {
                    btn_like.setImageResource(R.drawable.ic_music_like_r);
                } else {
                    btn_like.setImageResource(R.drawable.ic_music_like_boder_w);
                }
                cursor.close();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    // 与 Service 交互
    class ServiceBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BROADCAST_MUSIC_SERVICE_PROGRESS)) {
                int type = intent.getIntExtra("type", -1);
                switch (type) {
                    case GET_DURATION:
                        int duration = intent.getIntExtra("duration", 0);
                        seekBar.setMax(duration);
                        music_duration.setText(TimeTransUtil.millsToTime(duration));
                        break;
                    case GET_PROGRESS:
                        int progress = intent.getIntExtra("progress", 0);
                        seekBar.setProgress(progress);
                        music_played.setText(TimeTransUtil.millsToTime(progress));
                        break;
                    case COMPLETION:
                        if (intent.getBooleanExtra("music_completion", false)) {
                            btnPlay.setImageResource(R.drawable.ic_music_play_white);
                        }
                        break;
                    case GET_BUFFERING_LEVEL:
                        seekBar.setSecondaryProgress(intent.getIntExtra("bufferingLevel", 0));
                        break;
                    default:
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        init_activity();
        init_musicinfo();
        init_toolbarTitle();
        setPlayingMode();
        setBtnLike();

        Intent bindIntent = new Intent(this,MusicService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBinder.isExistPlayer()) {
                    if(!mBinder.isPlaying()) {
                        btnPlay.setImageResource(R.drawable.ic_music_pause_white);
                        // 改成暂停图像
                        if(!mBinder.isStartedPlay()) {
                            mBinder.playMusic(music_id);
                            sendActivityBroadcast(SEND_MUSIC_INFO);
                        }else {
                            mBinder.continueMusic();
                        }
                    }else{
                        btnPlay.setImageResource(R.drawable.ic_music_play_white);
                        mBinder.pauseMusic();
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    progress_g = progress;
                    sendActivityBroadcast(SEND_PROGRESS);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 拖动进度条，播放指定位置
                progress_stop = seekBar.getProgress();
                sendActivityBroadcast(SEND_PROGRESS_STOP_TOUCH);
            }
        });
    }

    // 初始化 Toolbar
    private void init_toolbarTitle(){
        TextView musicTitle = findViewById(R.id.musicTitle);
        TextView musicArtist = findViewById(R.id.musicArtist);
        musicTitle.setText(music_name);
        musicArtist.setText(artist_name);
    }

    private void init_activity(){
        Toolbar toolbar = findViewById(R.id.toolbar_play);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_down_white);
        }
        btnPlay = findViewById(R.id.playing_play);
        seekBar = findViewById(R.id.seekbar_play);
        mReceiver = new ServiceBroadcastReceiver();
        registerReceiver(mReceiver,new IntentFilter(MusicService.BROADCAST_MUSIC_SERVICE_PROGRESS));

        music_played = findViewById(R.id.music_duration_played);
        music_duration = findViewById(R.id.music_duration);
        handler = new Handler();
    }

    private void init_musicinfo(){
        Intent intent = getIntent();
        music_id = intent.getLongExtra("id",0);
        music_name = intent.getStringExtra("music_name");
        artist_name = intent.getStringExtra("artist_name");
        picUrl = intent.getStringExtra("picUrl");
        ImageView album_pic = findViewById(R.id.album_pic);
        Glide.with(this)
                .load(picUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(album_pic);
    }

    private void setPlayingMode(){
        playing_mode = findViewById(R.id.playing_mode);
        playing_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // repeat -> repeat_one
                if(mBinder.getPlayingMode()==0){
                    //Toast.makeText(MusicPlayActivity.this,"repeat",Toast.LENGTH_SHORT).show();
                    playing_mode.setImageResource(R.drawable.ic_music_repeat_one);
                    mBinder.setPlayingMode(1);
                }
                // repeat_one -> repeat
                else if(mBinder.getPlayingMode()==1){
                    //Toast.makeText(MusicPlayActivity.this,"repeat_one",Toast.LENGTH_SHORT).show();
                    playing_mode.setImageResource(R.drawable.ic_music_repeat_white);
                    mBinder.setPlayingMode(0);
                }
            }
        });
    }

    private void setBtnLike(){
        btn_like = findViewById(R.id.music_like);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri =Uri.parse("content://com.example.lv0player.provider/musiclike");
                Cursor cursor = getContentResolver().query(uri,null,"id=?"
                        ,new String[]{String.valueOf(music_id)},null);
                if(cursor!=null){
                    // 插入
                    if(!cursor.moveToNext()) {
                        btn_like.setImageResource(R.drawable.ic_music_like_r);
                        ContentValues values = new ContentValues();
                        values.put("id", music_id);
                        values.put("music_name", music_name);
                        values.put("artist_name", artist_name);
                        values.put("album_pic", picUrl);
                        getContentResolver().insert(uri, values);
                    } else {    // 删除
                        btn_like.setImageResource(R.drawable.ic_music_like_boder_w);
                        Uri uri_id = Uri.parse("content://com.example.lv0player.provider/musiclike/" + music_id);
                        getContentResolver().delete(uri_id, null, null);
                    }
                }
            }
        });
    }

    // 向 Service 发送数据
    private void sendActivityBroadcast(int command){
        Intent intent = null;
        switch (command){
            case SEND_PROGRESS:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_CONTROL);
                intent.putExtra("cmd",SEND_PROGRESS);
                intent.putExtra("progress",progress_g);
                break;
            case SEND_PROGRESS_STOP_TOUCH:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_CONTROL);
                intent.putExtra("cmd",SEND_PROGRESS_STOP_TOUCH);
                intent.putExtra("stop_touch_progress",progress_stop);
                break;
            case SEND_MUSIC_INFO:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_CONTROL);
                intent.putExtra("cmd",SEND_MUSIC_INFO);
                intent.putExtra("music_name",music_name);
                intent.putExtra("artist_name",artist_name);
                intent.putExtra("picUrl",picUrl);
                break;
            default:
        }
        if(intent != null)
            sendBroadcast(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                // 设置 activity 跳转动画
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(connection);
    }
}