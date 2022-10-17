package com.example.lv0player.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.core.app.NotificationCompat;

import com.example.lv0player.R;

public class MusicService extends Service {
    private MusicPlayBinder mBinder = new MusicPlayBinder();
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    ActivityBroadcastReceiver mReceiver;

    public static final int GET_DURATION=0;
    public static final int GET_PROGRESS=1;
    public static final int GET_BUFFERING_LEVEL=2;
    public static final int COMPLETION=3;
    public static final int SEND_PROGRESS=5;
    public static final int SEND_PROGRESS_STOP_TOUCH=6;
    public static final int SEND_MUSIC_INFO=7;

    public static final String BROADCAST_MUSIC_SERVICE_PROGRESS = "MusicService.PROGRESS";
    public static final String BROADCAST_MUSIC_SERVICE_CONTROL = "MusicService.CONTROL";

    private int duration=0;
    private int progress=0;
    private int playing_mode=0;
    private int bufferingLevel=0;
    private String music_name="";
    private String artist_name="";
    private long g_music_id= Long.valueOf(0);
    private boolean startedPlay=false;
    private String picUrl="";

    class ActivityBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BROADCAST_MUSIC_SERVICE_CONTROL)){
                int cmd = intent.getIntExtra("cmd",-1);
                switch (cmd){
                    case SEND_PROGRESS:
                        mediaPlayer.seekTo(intent.getIntExtra("progress",0));
                        break;
                    case SEND_PROGRESS_STOP_TOUCH:
                        if (mediaPlayer != null && mediaPlayer.isPlaying()){
                            mediaPlayer.seekTo(intent.getIntExtra("stop_touch_progress",0));
                        }
                        break;
                    case SEND_MUSIC_INFO:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // Android 8 以上适配
                            music_name = intent.getStringExtra("music_name");
                            artist_name = intent.getStringExtra("artist_name");
                            picUrl = intent.getStringExtra("picUrl");
                            startMyOwnForeground();
                        } else {
                            // 没具体实现
                            startForeground(1, new Notification());
                        }
                        break;
                    default:
                }
            }
        }
    }

    public class MusicPlayBinder extends Binder{

        public void playMusic(Long music_id) {
            g_music_id = music_id;
            startedPlay = true;
            try {
                Thread.sleep(300);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.reset();
                mediaPlayer.setDataSource("https://music.163.com/song/media/outer/url?id=" + music_id + ".mp3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setVolume(0.8f, 0.8f);
                    mediaPlayer.setLooping(false);
                    duration = mediaPlayer.getDuration();
                    sendServiceBroadcast(GET_DURATION);
                    mediaPlayer.start();
                    if(playing_mode==0)
                        mediaPlayer.setLooping(false);
                    else if(playing_mode==1)
                        mediaPlayer.setLooping(true);
                    updateSeekBar();
                }
            });

            mediaPlayer.prepareAsync();
            // 缓冲进度
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int i) {
                    double ratio = i / 100.0;
                    bufferingLevel = (int) (mp.getDuration() * ratio);
                    //seekBar.setSecondaryProgress(bufferingLevel);
                    sendServiceBroadcast(GET_BUFFERING_LEVEL);
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //btnPlay.setImageResource(R.drawable.ic_music_play_white);
                    sendServiceBroadcast(COMPLETION);
                    startedPlay = false;
                    handler.removeCallbacks(runnable);
                }
            });
        }

        private void updateSeekBar() {
            if(mediaPlayer !=null) {
                int currentPosition = mediaPlayer.getCurrentPosition();

                //seekBar.setProgress(currentPosition);
                progress = currentPosition;
                sendServiceBroadcast(GET_PROGRESS);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        updateSeekBar();
                    }
                };
                // 每秒执行一次
                handler.postDelayed(runnable, 1000);
            }
        }

        public void pauseMusic(){
            handler.removeCallbacks(runnable);
            mediaPlayer.pause();
        }

        public void stopMusic(){
            handler.removeCallbacks(runnable);
            if(mediaPlayer!=null) {
                startedPlay = false;
                mediaPlayer.stop();
            }
        }

        public void continueMusic(){
            mediaPlayer.start();
            updateSeekBar();
        }

        public boolean isPlaying(){
            if(mediaPlayer!=null)
                return mediaPlayer.isPlaying();
            return false;
        }

        public boolean isExistPlayer(){
            if(mediaPlayer!=null)
                return true;
            return false;
        }

        public boolean isStartedPlay(){
            return startedPlay;
        }

        public String getMusicName(){
            return music_name;
        }

        public String getArtistName(){
            return artist_name;
        }

        public Long getMusicId(){
            return g_music_id;
        }

        public int getDuration(){
            if(mediaPlayer!=null)
                return duration;
            return 0;
        }

        public int getProgress(){
            return progress;
        }

        public void setPlayingMode(int mode){
            playing_mode = mode;
            if(playing_mode==0)
                mediaPlayer.setLooping(false);
            else if(playing_mode==1)
                mediaPlayer.setLooping(true);
        }

        public int getPlayingMode(){
            return playing_mode;
        }
        public String getPicUrl(){
            return picUrl;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        mReceiver = new ActivityBroadcastReceiver();
        registerReceiver(mReceiver,new IntentFilter(BROADCAST_MUSIC_SERVICE_CONTROL));
    }

    private void sendServiceBroadcast(int content) {
        Intent intent = null;
        switch(content){
            case GET_DURATION:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_PROGRESS);
                intent.putExtra("type",content);
                intent.putExtra("duration",duration);
                break;
            case GET_PROGRESS:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_PROGRESS);
                intent.putExtra("type",content);
                intent.putExtra("progress",progress);
                break;
            case GET_BUFFERING_LEVEL:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_PROGRESS);
                intent.putExtra("type",content);
                intent.putExtra("bufferingLevel",bufferingLevel);
                break;
            case COMPLETION:
                intent = new Intent(BROADCAST_MUSIC_SERVICE_PROGRESS);
                intent.putExtra("type",content);
                intent.putExtra("music_completion",true);
                break;
            default:
        }
        if(intent!=null)
            sendBroadcast(intent);
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.lv0player";
        String channelName = "MusicBackground Service";
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(music_name+"  -- "+artist_name)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        cleanMediaPlayer();
        return super.onUnbind(intent);
    }

    private void cleanMediaPlayer(){
        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        stopForeground(true);
    }
}