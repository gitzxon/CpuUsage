package com.example.leon.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leon on 15/1/8.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE_PROCESS = "create table Process (" +
            "id integer primary key autoincrement, " +
            "pid integer, " +
            "pr integer, " +
            "cpu integer, " +
            "s text, " +
            "thr integer, " +
            "vss integer, " +
            "rss integer, " +
            "uid text, " +
            "name text, " +
            "update_time text)";

    public static final String[] TABLE_PROCESS_COLUMN = {
            "id", "pid", "pr", "cpu", "s", "thr", "vss",
            "rss", "uid", "name", "update_time"};

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROCESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
