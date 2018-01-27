package com.lazyeraser.imas.cgss.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lazyeraser.imas.main.BaseActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lazyeraser.imas.main.SStaticR.isCn;

/**
 * Created by lazyeraser on 2017/9/18.
 */


public class DBHelper extends SQLiteOpenHelper  {

    private static volatile Map<String, DBHelper> instances;

    private static final int DB_VERSION = 1;

    // db to create
    private static final String DB_NAME = isCn ? "DereHelper.db" : "DereHelper_EN.db";

    public static final String TABLE_NAME_Card = "t_card";
    public static final String TABLE_NAME_Chara_Index = "t_chara_index";
    public static final String TABLE_NAME_Chara_Detail = "t_chara";
    public static final String TABLE_NAME_Translation = "t_tran";

    // db from downloading
    public static final String DB_NAME_manifest = "manifest.db";
    public static final String CGSS_TABLE_NAME_Manifest = "manifests";

    public static final String DB_NAME_master = "master.db";

    private static final Map<Class<?>, String> TYPES;

    static {
        TYPES = new HashMap<>();
        TYPES.put(byte.class, "BYTE");
        TYPES.put(boolean.class, "INTEGER");
        TYPES.put(short.class, "SHORT");
        TYPES.put(int.class, "INTEGER");
        TYPES.put(long.class, "LONG");
        TYPES.put(String.class, "TEXT");
        TYPES.put(byte[].class, "BLOB");
        TYPES.put(float.class, "FLOAT");
        TYPES.put(double.class, "DOUBLE");
    }


    public static DBHelper with(BaseActivity context){
        if (instances == null || instances.get(DB_NAME) == null) {
            synchronized (DBHelper.class){
                if (instances == null)
                    instances = new HashMap<>();
                if (instances.get(DB_NAME) == null){
                    instances.put(DB_NAME, new DBHelper(context, DB_NAME));
                }
            }
        }
//        instances.putIfAbsent(DB_NAME, new DBHelper(context)); // Java8 not supported under Android 6.0
        return instances.get(DB_NAME);
    }
    public static DBHelper with(BaseActivity context, String dbName){
        if (instances == null || instances.get(dbName) == null) {
            synchronized (DBHelper.class) {
                if (instances == null)
                    instances = new HashMap<>();
                if (instances.get(dbName) == null){
                    instances.put(dbName, new DBHelper(context, context.getFilesDir().getAbsolutePath() + "/" + dbName));
                }
            }
        }
//        instances.putIfAbsent(dbName, new DBHelper(context, context.getFilesDir().getAbsolutePath() + "/" + dbName));
        return instances.get(dbName);
    }

    public static void refresh(BaseActivity context, String dbName){
        if (instances == null) {
            synchronized (DBHelper.class) {
                if (instances == null)
                    instances = new HashMap<>();
            }
        }
        instances.put(dbName, new DBHelper(context, context.getFilesDir().getAbsolutePath() + "/" + dbName));
    }

    public static DBHelper getInstance(){
        if (instances == null || instances.get(DB_NAME) == null) {
            throw new NullPointerException("No instance of DBHelper");
        }
        return instances.get(DB_NAME);
    }
    public static DBHelper getInstance(String dbName){
        if (instances == null || instances.get(dbName) == null) {
            throw new NullPointerException("No instance of DBHelper");
        }
        return instances.get(dbName);
    }

    /*====================================*/



    private DBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
        onCreate(getReadableDatabase());
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (getDatabaseName().equals(DB_NAME)){
            List<String> SQLs = new ArrayList<>();
            SQLs.add("create table if not exists " + TABLE_NAME_Card + "(id integer primary key, json VARCHAR)");
            SQLs.add("create table if not exists " + TABLE_NAME_Chara_Index + "(id integer primary key, json VARCHAR)");
            SQLs.add("create table if not exists " + TABLE_NAME_Chara_Detail + "(id integer primary key, json VARCHAR)");
            SQLs.add("create table if not exists " + TABLE_NAME_Translation + "(origin VARCHAR primary key, translate VARCHAR)");
            for (String sql : SQLs) {
                db.execSQL(sql);
            }
        }
    }

    public void beginTran(){
        getWritableDatabase().beginTransaction();
    }

    public void setTranSuccess(){
        getWritableDatabase().setTransactionSuccessful();
    }

    public void endTran(){
        getWritableDatabase().endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        allTableExe(db, "DROP TABLE IF EXISTS ", null);
        onCreate(db);
    }

    public interface OnSqlExecutedListener{
        void onExecuted(boolean b);
    }

    public void allTableExe(String sqlHead, OnSqlExecutedListener onSqlExecutedListener){
        allTableExe(getWritableDatabase(), sqlHead, onSqlExecutedListener);
    }

    private void allTableExe(SQLiteDatabase db, String op, OnSqlExecutedListener onSqlExecutedListener){
        for (String sql : getTableSqls(op)) {
            db.execSQL(sql);
        }
        if (onSqlExecutedListener != null){
            onSqlExecutedListener.onExecuted(true);
        }
    }

    private List<String> getTableSqls(String op){
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
        return SQLs;
    }

    public boolean deleteAllTables(){
        for (String sql : getTableSqls("")) {
            getWritableDatabase().delete(sql, null, null);
        }
        return true;
    }

    private Cursor prepareCursor(String tableName, @Nullable  String key, @Nullable List<String> keyValue){
        SQLiteDatabase db = getReadableDatabase();
        if (db == null){
            return null;
        }
        if (key == null || keyValue == null || keyValue.size() == 0){
            return db.rawQuery("select * from " + tableName, null);
        }else {
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
            return db.rawQuery("select  * from " + tableName + " where " + key + " in " + paraBuilder.toString(), keyValue.toArray(new String[keyValue.size()]));
        }
    }

    private Cursor prepareLikeCursor(@NonNull String tableName, @NonNull  String key, @NonNull String likeValue){
        SQLiteDatabase db = getReadableDatabase();
        if (db == null){
            return null;
        }
        return db.rawQuery("select * from " + tableName + " where " + key + " like ?", new String[]{ likeValue });
    }

    /*================Java Bean & DataBase=================*/
    private String getTableBuildingSQL(Class<?> clazz) {
        StringBuilder strBuilder = new StringBuilder("create table if not exists ");
        strBuilder.append(clazz.getSimpleName());
        strBuilder.append("(");
        Field[] arrField = clazz.getDeclaredFields();
        for (int i = arrField.length - 1; i >= 0; i--) {
            Field f = arrField[i];
            String type = TYPES.get(f.getType());
            if (type != null) {
                strBuilder.append(f.getName()).append(" ").append(type);
                if (f.isAnnotationPresent(Primary.class)) {
                    strBuilder.append(" PRIMARY KEY");
                }
                if (i > 0) {
                    strBuilder.append(",");
                }
            }
        }
        strBuilder.append(")");
        return strBuilder.toString();
    }

    public static <T> ContentValues translate2ContentValues(T bean) {
        ContentValues cv = new ContentValues();

        Field[] arrField = bean.getClass().getDeclaredFields();
        try {
            for (Field f : arrField) {
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                String name = f.getName();
                Object value = f.get(bean);

                if (value instanceof Byte) {
                    cv.put(name, (Byte) value);
                } else if (value instanceof Short) {
                    cv.put(name, (Short) value);
                } else if (value instanceof Integer) {
                    cv.put(name, (Integer) value);
                } else if (value instanceof Long) {
                    cv.put(name, (Long) value);
                } else if (value instanceof String) {
                    cv.put(name, (String) value);
                } else if (value instanceof byte[]) {
                    cv.put(name, (byte[]) value);
                } else if (value instanceof Boolean) {
                    cv.put(name, (Boolean) value);
                } else if (value instanceof Float) {
                    cv.put(name, (Float) value);
                } else if (value instanceof Double) {
                    cv.put(name, (Double) value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cv;
    }

    private static <T> List<T> cursor2List(Cursor cursor, Class theClass) throws IllegalAccessException, InstantiationException {
        return cursor2List(cursor, theClass, null);
    }

    private static <T> List<T> cursor2List(Cursor cursor, Class theClass, String mColumnName) throws IllegalAccessException, InstantiationException {
        List<T> result = new ArrayList<>();
        if (theClass.equals(Integer.class) && mColumnName != null){
            int columnIdx = cursor.getColumnIndex(mColumnName);
            while (cursor.moveToNext()) {
                if (cursor.isBeforeFirst()) {
                    continue;
                }
                Integer value = cursor.getInt(columnIdx);
                result.add((T)value);
            }
            cursor.close();
            return result;
        }
        Field[] arrField = theClass.getDeclaredFields();
        while (cursor.moveToNext()){
            if (cursor.isBeforeFirst()){
                continue;
            }
            Object bean = theClass.newInstance();
            for (Field f : arrField) {
                String columnName = f.getName();
                int columnIdx = cursor.getColumnIndex(columnName);
                if (columnIdx != -1) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    Class<?> type = f.getType();
                    if (type == byte.class) {
                        f.set(bean, (byte) cursor.getShort(columnIdx));
                    } else if (type == short.class) {
                        f.set(bean, cursor.getShort(columnIdx));
                    } else if (type == int.class) {
                        f.set(bean, cursor.getInt(columnIdx));
                    } else if (type == long.class) {
                        f.set(bean, cursor.getLong(columnIdx));
                    } else if (type == String.class) {
                        f.set(bean, cursor.getString(columnIdx));
                    } else if (type == byte[].class) {
                        f.set(bean, cursor.getBlob(columnIdx));
                    } else if (type == boolean.class) {
                        f.set(bean, cursor.getInt(columnIdx) == 1);
                    } else if (type == float.class) {
                        f.set(bean, cursor.getFloat(columnIdx));
                    } else if (type == double.class) {
                        f.set(bean, cursor.getDouble(columnIdx));
                    }
                }
            }
            result.add((T)bean);
        }
        cursor.close();
        return result;
    }
    /*================Java Bean & DataBase=================*/

    /*==========methods to use===========*/
    public Map<String, String> where(String tableName, String columnName, String key, List<String> keyValue) {
        Cursor cursor = prepareCursor(tableName, key, keyValue);
        if (cursor == null){
            return null;
        }
        Map<String, String> result = new HashMap<>();
        while (cursor.moveToNext()) {
            result.put(cursor.getString(cursor.getColumnIndex(key)), cursor.getString(cursor.getColumnIndex(columnName)));
        }
        cursor.close();
        return result;
    }

    public List<String> getAll(String tableName, String column){
        if (Utils.checkEmpty(tableName, column)){
            SQLiteDatabase db = getReadableDatabase();
            if (db == null){
                return null;
            }
            List<String> result = new ArrayList<>();
            Cursor cursor = db.rawQuery("select * from " + tableName, null);
            while (cursor.moveToNext()) {
                result.add(cursor.getString(cursor.getColumnIndex(column)));
            }
            cursor.close();
            return result;
        }else {
            return null;
        }
    }

    public void insertData(String tableName, ContentValues contentValues) {
        getWritableDatabase().replace(tableName, null, contentValues);
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

    public <T> List<T> getBeanList(String tableName, Class theClass) throws InstantiationException, IllegalAccessException {
        Cursor cursor = prepareCursor(tableName, null, null);
        if (cursor == null)
            return null;
        return cursor2List(cursor, theClass);
    }

    public <T> List<T> getBeanList(String tableName, Class theClass, String key, List<String> keyValues) throws InstantiationException, IllegalAccessException {
        Cursor cursor = prepareCursor(tableName, key, keyValues);
        if (cursor == null)
            return null;
        return cursor2List(cursor, theClass);
    }

    public <T> List<T> getBeanListLike(String tableName, Class theClass, String key, String likeValue) throws InstantiationException, IllegalAccessException {
        Cursor cursor = prepareLikeCursor(tableName, key, likeValue);
        if (cursor == null)
            return null;
        return cursor2List(cursor, theClass);
    }

    public <T> T getBean(String tableName, Class theClass, String key, String keyValue) throws InstantiationException, IllegalAccessException {
        Cursor cursor = prepareCursor(tableName, key, Collections.singletonList(keyValue));
        if (cursor == null)
            return null;
        List<T> data = cursor2List(cursor, theClass);
        if (data != null && data.size() > 0)
            return data.get(0);
        return null;
    }

    public <T> T getBeanByRaw(String sql, String keyValue, Class theClass) throws InstantiationException, IllegalAccessException {
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{keyValue});
        if (cursor == null)
            return null;
        List<T> data = cursor2List(cursor, theClass);
        if (data != null && data.size() == 1)
            return data.get(0);
        return null;
    }

    public <T> List<T> getBeanListByRaw(String sql, Class theClass) throws InstantiationException, IllegalAccessException {
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        if (cursor == null)
            return null;
        return cursor2List(cursor, theClass);
    }

    public <T> T getBeanByRaw(String sql, String keyValue, Class theClass, String coName) throws InstantiationException, IllegalAccessException {
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{keyValue});
        if (cursor == null)
            return null;
        List<T> data = cursor2List(cursor, theClass, coName);
        if (data != null && data.size() == 1)
            return data.get(0);
        return null;
    }

    public <T> List<T> getBeanListByRaw(String sql, Class theClass, String coName) throws InstantiationException, IllegalAccessException {
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        if (cursor == null)
            return null;
        return cursor2List(cursor, theClass, coName);
    }
}
