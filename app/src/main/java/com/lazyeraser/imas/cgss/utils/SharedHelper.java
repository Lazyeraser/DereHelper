package com.lazyeraser.imas.cgss.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lazyeraser.imas.main.SStaticR;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SharedHelper {

    private Context mContext;

    private final static Map<String, String> defaultKeyValues;

    public final static String KEY_AUTO_DATA = "autoDataUpdate";
    public final static String KEY_AUTO_APP = "autoAppUpdate";
    public final static String KEY_DEFAULT_TRAN = "defaultTranslated";

    public final static String KEY_ANALYTICS_ON = "GAOn";
    public final static String KEY_ANALYTICS_ASKED = "GAAsked";

    public final static String KEY_TruthVersion = "truthVersion";
    public final static String KEY_MasterDbHash = "masterHash";

    public final static String KEY_USE_REVERSE_PROXY = "reverse proxy on";

    public final static String KEY_UNITY_VERSION = "KEY_UNITY_VERSION";

    private final static AtomicBoolean init = new AtomicBoolean(false);

    static {
        defaultKeyValues = new HashMap<>();
        defaultKeyValues.put(KEY_AUTO_DATA, "true");
        defaultKeyValues.put(KEY_AUTO_APP, "true");
        defaultKeyValues.put(KEY_DEFAULT_TRAN, "false");
        defaultKeyValues.put(KEY_ANALYTICS_ON, "true");
        defaultKeyValues.put(KEY_ANALYTICS_ASKED, "false");
        defaultKeyValues.put(KEY_USE_REVERSE_PROXY, "false");
        defaultKeyValues.put(KEY_UNITY_VERSION, SStaticR.UNITY_VERSION);
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
        synchronized (init){
            if (init.get())
                return;
            SharedPreferences sp = mContext.getSharedPreferences("DereHelperSp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            for (String key : defaultKeyValues.keySet()) {
                if (!sp.contains(key)){
                    editor.putString(key, defaultKeyValues.get(key));
                }
            }
            editor.apply();
            init.set(true);
        }
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
