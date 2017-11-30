package com.lazyeraser.imas.cgss.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {

    private Context mContext;

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }

    //保存
    public void save(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //读取
    public String read(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
}
