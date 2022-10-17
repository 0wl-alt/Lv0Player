package com.example.lv0player.util;

import android.util.Log;

import com.example.lv0player.model.MusicDetail;
import com.example.lv0player.model.MusicSearchInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    public static MusicSearchInfo GetMusicSearchInfo(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String json_data = response.body().string();
        MusicSearchInfo musicSearchInfo = parseJsonWithGson_MusicSearch(json_data);
        Log.d("OkHttp_Music",musicSearchInfo.getResult().getSongs().get(0).getName());
        return musicSearchInfo;
    }

    public static String GetPicUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String json_data = response.body().string();
        MusicDetail musicDetail = parseJsonWithGson_MusicDetail(json_data);
        return musicDetail.getSong().getAl().getPicUrl();
    }

    public static String GetMusicPlayUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String json_data = response.body().string();

        return parseJsonWithJsonObject(json_data);
    }

    public static MusicSearchInfo parseJsonWithGson_MusicSearch(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, MusicSearchInfo.class);
    }

    public static MusicDetail parseJsonWithGson_MusicDetail(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, MusicDetail.class);
    }

    public static String parseJsonWithJsonObject(String jsonData){
        String url = null;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONObject data = jsonArray.getJSONObject(0);
            url = data.getString("url");
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }
}
