package com.lazyeraser.imas.main;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.lazyeraser.imas.cgss.utils.Utils;

import java.util.Locale;


/**
 * Created by lazyEraser on 2017/4/20.
 *
 */

public class LIVE extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 18){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        Utils.mPrint("os language:" + Locale.getDefault().getLanguage());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
