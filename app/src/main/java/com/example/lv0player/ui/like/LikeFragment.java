package com.example.lv0player.ui.like;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lv0player.MusicLikeAdapter;
import com.example.lv0player.model.MusicLikeModel;
import com.example.lv0player.R;

import java.util.ArrayList;
import java.util.List;

public class LikeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<MusicLikeModel> musicLike_list;
    private MusicLikeAdapter mAdapter;
    private ImageView bar_album_pic;
    private View view;

    public LikeFragment(ImageView bar_album_pic){
        this.bar_album_pic = bar_album_pic;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation_like,container,false);
        musicLike_list = new ArrayList<>();
        initRecyclerView();
        initData();
        return view;
    }

    public void initData(){
        Uri uri = Uri.parse("content://com.example.lv0player.provider/musiclike");
        Cursor cursor = getContext().getContentResolver().query(uri,null,null,
                null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                @SuppressLint("Range") int music_id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String music_name = cursor.getString(cursor.getColumnIndex("music_name"));
                @SuppressLint("Range") String artist_name = cursor.getString(cursor.getColumnIndex("artist_name"));
                @SuppressLint("Range") String picUrl = cursor.getString(cursor.getColumnIndex("album_pic"));
                MusicLikeModel model = new MusicLikeModel();
                model.setMusicId(music_id);
                model.setMusic_name(music_name);
                model.setArtist_name(artist_name);
                model.setPicUrl(picUrl);
                musicLike_list.add(model);
            }
            cursor.close();
        }
    }

    public void initRecyclerView(){
        mRecyclerView = view.findViewById(R.id.like_music_view);
        mAdapter = new MusicLikeAdapter(getActivity(),musicLike_list,bar_album_pic);
        // item 间边框
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }
}
