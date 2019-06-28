package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ntuh.yl_mdms.db"; //資料庫名稱
    private static final int DATABASE_VERSION = 3;  //資料庫版本

    private SQLiteDatabase db;

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
        public void onCreate(SQLiteDatabase db) {
            String DATABASE_CREATE_TABLE =
                "CREATE TABLE device_tb (" +
                        "  `DID` TEXT," +
                        "  `category` TEXT," +
                        "  `model` TEXT," +
                        "  `number` TEXT," +
                        "  `user` TEXT," +
                        "  `position` TEXT," +
                        "  `status` TEXT," +
                        "  `LastModified` TEXT" +
                        ")";
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS device_tb");  //刪除舊有的資料表
        onCreate(db);
    }

    public void inster(String tb_name,ContentValues cv_input) {
        db.insert(tb_name, null, cv_input);
    }

    public void update(String tb_name,ContentValues cv_input,String where) {
        db.update(tb_name,cv_input,where,null);
    }

    public void remove(String tb_name,String where) {
        db.delete(tb_name, where,null);
    }

    public Cursor selectAll(String tb_name,String[] key,String where,String groupBy,String having,String orderBy) {
        return db.query(tb_name,key,where,null, groupBy, having, orderBy);
    }
}
