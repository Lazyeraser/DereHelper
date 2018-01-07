package com.lazyeraser.imas.cgss.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedHelper {

    private Context mContext;

    private final static Map<String, String> defaultKeyValues;

    public final static String KEY_AUTO_DATA = "autoDataUpdate";
    public final static String KEY_AUTO_APP = "autoAppUpdate";
    public final static String KEY_DEFAULT_TRAN = "defaultTranslated";

    public final static String KEY_TruthVersion = "truthVersion";
    public final static String KEY_MasterDbHash = "masterHash";

    private static boolean inited = false;
    static {
        defaultKeyValues = new HashMap<>();
        defaultKeyValues.put(KEY_AUTO_DATA, "true");
        defaultKeyValues.put(KEY_AUTO_APP, "true");
        defaultKeyValues.put(KEY_DEFAULT_TRAN, "false");
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
        if (inited)
            return;
        SharedPreferences sp = mContext.getSharedPreferences("DereHelperSp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (String key : defaultKeyValues.keySet()) {
            if (!sp.contains(key)){
                editor.putString(key, defaultKeyValues.get(key));
            }
        }
        editor.apply();
        inited = true;
    }

    //保存
    public void save(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("DereHelperSp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //读取
    public String read(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("DereHelperSp", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
}
