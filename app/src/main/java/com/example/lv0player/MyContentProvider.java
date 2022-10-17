package com.example.lv0player;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.xml.transform.URIResolver;

public class MyContentProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    public static final int MUSICLIKE_DIR=0;
    public static final int MUSICLIKE_ITEM=1;
    public static final int MUSICLOCAL_DIR=2;
    public static final int MUSICLOCAL_ITEM=3;
    private MyDataBaseHelper dbHelper;
    public static final String AUTHORITY="com.example.lv0player.provider";

    public MyContentProvider() {
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"musiclike",MUSICLIKE_DIR);
        uriMatcher.addURI(AUTHORITY,"musiclike/#",MUSICLIKE_ITEM);
        uriMatcher.addURI(AUTHORITY,"musiclocal",MUSICLOCAL_DIR);
        uriMatcher.addURI(AUTHORITY,"musiclocal/#",MUSICLOCAL_ITEM);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleteRows=0;
        switch (uriMatcher.match(uri)){
            case MUSICLIKE_DIR:
                deleteRows = db.delete("Musiclike",selection,selectionArgs);
                break;
            case MUSICLIKE_ITEM:
                String musicId = uri.getPathSegments().get(1);
                deleteRows = db.delete("Musiclike","id=?",new String[]{musicId});
                break;
            case MUSICLOCAL_DIR:
                deleteRows = db.delete("Musiclocal",selection,selectionArgs);
                break;
            case MUSICLOCAL_ITEM:
                String musicLocalId = uri.getPathSegments().get(1);
                deleteRows = db.delete("Musiclocal","id=?",new String[]{musicLocalId});
                break;
            default:
                break;
        }
        return deleteRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case MUSICLIKE_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.lv0player.provider.musiclike";
            case MUSICLIKE_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.lv0player.provider.musiclike";
            case MUSICLOCAL_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.lv0player.provider.musiclocal";
            case MUSICLOCAL_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.lv0player.provider.musiclocal";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case MUSICLIKE_DIR:
            case MUSICLIKE_ITEM:
                long newLikeId = db.insert("Musiclike",null,values);
                uriReturn = Uri.parse("content://"+AUTHORITY+"/musiclike/"+newLikeId);
                break;
            case MUSICLOCAL_DIR:
            case MUSICLOCAL_ITEM:
                long newDownloadId = db.insert("Musiclocal",null,values);
                uriReturn = Uri.parse("content://"+AUTHORITY+"/musiclocal"+newDownloadId);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDataBaseHelper(getContext(),"Music.db",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case MUSICLIKE_DIR:
                cursor = db.query("Musiclike",projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case MUSICLIKE_ITEM:
                String musicId = uri.getPathSegments().get(1);
                cursor = db.query("Musiclike",projection,"id=?",new String[]{musicId},
                        null,null,sortOrder);
                break;
            case MUSICLOCAL_DIR:
                cursor = db.query("Musiclocal",projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case MUSICLOCAL_ITEM:
                String musicLocalId = uri.getPathSegments().get(1);
                cursor = db.query("Musiclocal",projection,"id=?",new String[]{musicLocalId},
                        null,null,sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateRows=0;
        switch (uriMatcher.match(uri)){
            case MUSICLIKE_DIR:
                updateRows = db.update("Musiclike",values,selection,selectionArgs);
                break;
            case MUSICLIKE_ITEM:
                String musicId = uri.getPathSegments().get(1);
                updateRows = db.update("Musiclike",values,"id=?",new String[]{musicId});
                break;
            case MUSICLOCAL_DIR:
                updateRows = db.update("Musiclocal",values,selection,selectionArgs);
                break;
            case MUSICLOCAL_ITEM:
                String musicLocalId = uri.getPathSegments().get(1);
                updateRows = db.update("Musiclocal",values,"id=?",new String[]{musicLocalId});
                break;
            default:
                break;
        }
        return  updateRows;
    }
}