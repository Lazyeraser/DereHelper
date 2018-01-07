package com.lazyeraser.imas.main;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.Locale;


/**
 * Created by lazyEraser on 2017/4/20.
 *
 */

public class LIVE extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){ // 7.0以上自动更新apk安装时的报错避免
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
//            builder.detectFileUriExposure();
        }
        Utils.mPrint("os language:" + Locale.getDefault().getLanguage());
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        UMConfigure.init(this, "5a51be59b27b0a6d310004c2", "ALL", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setLocation(0, 0);
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
