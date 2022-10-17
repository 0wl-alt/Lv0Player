package com.example.lv0player;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.example.lv0player.model.MusicLikeModel;

import java.util.List;

public class MusicLikeAdapter extends RecyclerView.Adapter<MusicLikeAdapter.ViewHolder> {
    private List<MusicLikeModel> mList;
    private Context mContext;
    private ImageView bar_album_pic;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View music_like_view;
        TextView music_name;
        TextView artist_name;
        ImageView btn_liked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            music_like_view = itemView;
            music_name = itemView.findViewById(R.id.item_music_name);
            artist_name = itemView.findViewById(R.id.item_artist_name);
            btn_liked = itemView.findViewById(R.id.item_btn_like);
        }
    }

    public MusicLikeAdapter(Context context,List<MusicLikeModel> music_list,ImageView bar_album_pic){
        this.mList = music_list;
        this.mContext = context;
        this.bar_album_pic = bar_album_pic;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_like_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.btn_liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                MusicLikeModel model = mList.get(position);
                Uri uri =Uri.parse("content://com.example.lv0player.provider/musiclike");
                Cursor cursor = mContext.getContentResolver().query(uri,null,"id=?"
                        ,new String[]{String.valueOf(model.getMusicId())},null);
                if(cursor!=null){
                    // 插入
                    if(!cursor.moveToNext()) {
                        viewHolder.btn_liked.setImageResource(R.drawable.ic_music_like_r);
                        ContentValues values = new ContentValues();
                        values.put("id", model.getMusicId());
                        values.put("music_name", model.getMusic_name());
                        values.put("artist_name", model.getArtist_name());
                        values.put("album_pic", model.getPicUrl());
                        mContext.getContentResolver().insert(uri, values);
                    } else {    // 删除
                        viewHolder.btn_liked.setImageResource(R.drawable.ic_music_like_boder_b);
                        Uri uri_id = Uri.parse("content://com.example.lv0player.provider/musiclike/" + model.getMusicId());
                        mContext.getContentResolver().delete(uri_id, null, null);
                    }
                }
            }
        });

        viewHolder.music_like_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                MusicLikeModel model = mList.get(position);
                Intent intent = new Intent(view.getContext(), MusicPlayActivity.class);
                intent.putExtra("id",model.getMusicId());
                intent.putExtra("music_name",model.getMusic_name());
                intent.putExtra("artist_name",model.getArtist_name());
                intent.putExtra("picUrl",model.getPicUrl());
                Glide.with(mContext)
                        .load(model.getPicUrl())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(bar_album_pic);
                view.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicLikeModel model = mList.get(position);
        holder.music_name.setText(model.getMusic_name());
        holder.artist_name.setText(model.getArtist_name());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
