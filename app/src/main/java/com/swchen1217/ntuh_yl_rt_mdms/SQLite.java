package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ntuh.yl_mdms.db"; //資料庫名稱
    private static final int DATABASE_VERSION = 1;  //資料庫版本

    private SQLiteDatabase db;

    public SQLite(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE_TABLE =
                "CREATE TABLE `device_tb` (\n" +
                        "  `DID` varchar(30) COLLATE utf8_unicode_ci NOT NULL,\n" +
                        "  `category` varchar(30) COLLATE utf8_unicode_ci NOT NULL,\n" +
                        "  `model` varchar(30) COLLATE utf8_unicode_ci NOT NULL,\n" +
                        "  `number` varchar(30) COLLATE utf8_unicode_ci NOT NULL,\n" +
                        "  `user` varchar(30) COLLATE utf8_unicode_ci NOT NULL,\n" +
                        "  `position` varchar(30) COLLATE utf8_unicode_ci NOT NULL,\n" +
                        "  `status` varchar(30) COLLATE utf8_unicode_ci NOT NULL\n" +
                        ")";
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS device_tb");  //刪除舊有的資料表
        onCreate(db);
    }
}
