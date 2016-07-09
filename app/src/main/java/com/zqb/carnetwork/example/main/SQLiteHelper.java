package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zqb on 2016/4/6.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(Context context) {
        super(context, "my_db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String s=new String("CREATE TABLE user(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "password TEXT," +
                "flag TEXT);");
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
