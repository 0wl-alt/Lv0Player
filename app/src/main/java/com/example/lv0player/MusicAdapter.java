package com.example.lv0player;

import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.lv0player.activity.MusicPlayActivity;
import com.example.lv0player.model.MusicSearchInfo;
import com.example.lv0player.util.HttpRequest;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<MusicSearchInfo.ResultDTO.SongsDTO> songsDTOS;
    private Context mContext;
    private ImageView bar_album_pic;
    private Handler handler = new Handler();

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView music_name;
        TextView artist_name;
        View musicView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicView = itemView;
            music_name = itemView.findViewById(R.id.music_name);
            artist_name = itemView.findViewById(R.id.artist_name);
        }
    }

    public MusicAdapter(Context context,List<MusicSearchInfo.ResultDTO.SongsDTO> musicList,ImageView bar_album_pic){
        this.songsDTOS = musicList;
        this.mContext = context;
        this.bar_album_pic = bar_album_pic;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                MusicSearchInfo.ResultDTO.SongsDTO songsDTO = songsDTOS.get(position);
                String music_name = songsDTO.getName();
                String artist_name = songsDTO.getArtists().get(0).getName();
                long id = songsDTO.getId();
                Intent intent = new Intent(view.getContext(), MusicPlayActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("music_name",music_name);
                intent.putExtra("artist_name",artist_name);
                // 服务器地址
                String url = "http://XXXXXX/song/detail?ids=" + id;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String picUrl = HttpRequest.GetPicUrl(url);
                            intent.putExtra("picUrl",picUrl);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(mContext)
                                            .load(picUrl)
                                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                            .into(bar_album_pic);
                                }
                            });
                            view.getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicSearchInfo.ResultDTO.SongsDTO songsDTO = songsDTOS.get(position);
        String music_name = songsDTO.getName();
        String artist_name = songsDTO.getArtists().get(0).getName();
        holder.music_name.setText(music_name);
        holder.artist_name.setText(artist_name);
    }

    @Override
    public int getItemCount() {
        return songsDTOS.size();
    }
}