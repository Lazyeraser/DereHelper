package com.lazyeraser.imas.cgss.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;


import com.lazyeraser.imas.main.BaseActivity;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/9/18.
 */


public class DBHelper extends SQLiteOpenHelper  {

    private static DBHelper instance;
    private static final String DB_NAME = "DereHelper.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME_Card = "t_card";
    public static final String TABLE_NAME_Chara_Index = "t_chara_index";
    public static final String TABLE_NAME_Chara_Detail = "t_chara";
    public static final String TABLE_NAME_Translation = "t_tran";

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper with(BaseActivity context){
        if (instance == null){
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> SQLs = new ArrayList<>();
        SQLs.add("create table if not exists " + TABLE_NAME_Card + "(id integer primary key, json VARCHAR)");
        SQLs.add("create table if not exists " + TABLE_NAME_Chara_Index + "(id integer primary key, json VARCHAR)");
        SQLs.add("create table if not exists " + TABLE_NAME_Chara_Detail + "(id integer primary key, json VARCHAR)");
        SQLs.add("create table if not exists " + TABLE_NAME_Translation + "(origin VARCHAR primary key, translate VARCHAR)");
        for (String sql : SQLs) {
            db.execSQL(sql);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAll(db, "DROP TABLE IF EXISTS ");
        onCreate(db);
    }

    private void dropAll(SQLiteDatabase db, String op){
        List<String> SQLs = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getName().startsWith("TABLE_NAME")) {
                try {
                    SQLs.add(op + field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String sql : SQLs) {
            db.execSQL(sql);
        }
    }

    private Map<String, String> getDataByKey(String tableName, String columnName, String key, List<String> keyValue) {
        Map<String, String> result = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (db == null){
            return null;
        }
        if (keyValue == null){
            cursor = db.rawQuery("select * from " + tableName, null);
        }else if (keyValue.size() > 0){
            StringBuilder paraBuilder = new StringBuilder();
            paraBuilder.append("(");
            for (String s : keyValue) {
                if (!TextUtils.isEmpty(s)){
                    paraBuilder.append("?");
                    paraBuilder.append(", ");
                }
            }
            paraBuilder.delete(paraBuilder.length() - 2, paraBuilder.length());
            paraBuilder.append(")");
            cursor = db.rawQuery("select  * from " + tableName + " where " + key + " in " + paraBuilder.toString(), keyValue.toArray(new String[keyValue.size()]));
        } else {
            return result;
        }
        while (cursor.moveToNext()) {
            result.put(cursor.getString(cursor.getColumnIndex(key)), cursor.getString(cursor.getColumnIndex(columnName)));
        }
        cursor.close();
        return result;
    }



    private String nowTableName;
    private String nowColumn;

    public DBHelper queryTable(String tableName){
        this.nowTableName = tableName;
        return this;
    }

    public DBHelper column(String column){
        this.nowColumn = column;
        return this;
    }

    public Map<String, String> where(String key, List<String> keyValue){
        if (Utils.checkEmpty(nowTableName, nowColumn, key)){
            return getDataByKey(nowTableName, nowColumn, key, keyValue);
        }else {
            return null;
        }
    }

    public List<String> go(){
        List<String> result = new ArrayList<>();
        if (Utils.checkEmpty(nowTableName, nowColumn)){
            SQLiteDatabase db = getReadableDatabase();
            if (db == null){
                return result;
            }
            Cursor cursor = db.rawQuery("select * from " + nowTableName, null);
            while (cursor.moveToNext()) {
                result.add(cursor.getString(cursor.getColumnIndex(nowColumn)));
            }
            cursor.close();
            return result;
        }else {
            return result;
        }
    }



    public boolean insertData(String tableName, ContentValues contentValues) {
        try {
            getWritableDatabase().insert(tableName, null, contentValues);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateData(String tableName, ContentValues contentValues, String where, String[] value) {
        try {
            getWritableDatabase().update(tableName, contentValues, where, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
