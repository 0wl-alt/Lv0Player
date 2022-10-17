package com.example.lv0player;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String CREATE_LIKED = "create table Musiclike("
            +"id integer primary key,"
            +"music_name text,"
            + "artist_name text,"
            +"album_pic text)";

    public static final String CREATE_LOCAL = "create table Musiclocal("
            +"id integer primary key,"
            +"music_name text,"
            +"artist_name text,"
            + "album_pic text)";

    public MyDataBaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_LIKED);
        sqLiteDatabase.execSQL(CREATE_LOCAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Musiclike");
        sqLiteDatabase.execSQL("drop table if exists Musiclocal");
        onCreate(sqLiteDatabase);
    }
}
