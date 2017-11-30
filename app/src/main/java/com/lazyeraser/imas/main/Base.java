package com.lazyeraser.imas.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.lazyeraser.imas.cgss.utils.view.LoadingDialog;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lazyEraser on 2017/4/19.
 */

public class Base {

    private SharedHelper sh;
    private LoadingDialog loadingDialog;
    private static Map<String, BaseActivity> activityMap = new HashMap<>();

    protected AlertDialog alert = null;
    protected AlertDialog.Builder builder = null;

    private boolean canceled, loaded;
    private BaseActivity baseActivity;
    private BaseFragment baseFragment;
    private boolean inFragment = false;

    public void init(BaseActivity activity){
        baseActivity = activity;
        sh = new SharedHelper(activity.getApplicationContext());
        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setOnCancelListener((dialogInterface -> canceled = true));
        activityMap.put(activity.getClass().getSimpleName(), activity);
    }

    public void init(BaseFragment fragment){
        this.baseFragment = fragment;
        inFragment = true;
        init((BaseActivity) fragment.getActivity());
    }

    public Context getContext(){ return baseActivity.mContext; }





    /*------------通用方法-------------*/
    public void setOCL(@IdRes int id, View.OnClickListener ocl){
        baseActivity.findViewById(id).setOnClickListener(ocl);
    }
    //弹Toast
    public void makeToast(String str) {
        Toast.makeText(baseActivity, str, Toast.LENGTH_SHORT).show();
    }

    //弹SnackBar
    public void makeSBar(String str, View v) {
        Utils.hideAllInput(baseActivity);
        Snackbar snackbar = Snackbar.make(v, str, Snackbar.LENGTH_LONG);
        Utils.setSnackbarMsgTextColor(snackbar, Color.WHITE);
        snackbar.show();
    }

    public void makeToast(@StringRes int id) {
        makeToast(baseActivity.getString(id));
    }

    //弹SnackBar
    public void makeSBar(@StringRes int id, View v) {
        makeSBar(baseActivity.getString(id), v);
    }

    public void moveDrawer(DrawerLayout drawerLayout, int gravity){
        if (drawerLayout.isDrawerOpen(gravity)){
            drawerLayout.closeDrawer(gravity);
        } else {
            drawerLayout.openDrawer(gravity);
        }
    }

    public void jumpToAndFinish(Class dst) {
        Intent intent = new Intent();
        intent.setClass(baseActivity, dst);
        baseActivity.startActivity(intent);
        baseActivity.finish();
    }

    public void jumpToAndFinish(Class dst, Bundle data) {
        Intent intent = new Intent();
        intent.putExtras(data);
        intent.setClass(baseActivity, dst);
        baseActivity.startActivity(intent);
        baseActivity.finish();
    }

    public void jumpTo(Class dst) {
        Intent intent = new Intent();
        intent.setClass(baseActivity, dst);
        baseActivity.startActivity(intent);
    }

    public void jumpTo(Class dst, Bundle data) {
        Intent intent = new Intent();
        intent.putExtras(data);
        intent.setClass(baseActivity, dst);
        baseActivity.startActivity(intent);
    }

    public void jumpForRE(Class dst, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(baseActivity, dst);
        baseActivity.startActivityForResult(intent, requestCode);
    }

    public void jumpForRE(Class dst, Bundle data, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(data);
        intent.setClass(baseActivity, dst);
        baseActivity.startActivityForResult(intent, requestCode);
    }

    public String getIntentString(String key) {
        try {
            String result = baseActivity.getIntent().getExtras().getString(key);
            return TextUtils.isEmpty(result) ? "" : result;
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void spSave(String key, String value) {
        sh.save(key, value);
    }

    public String spRead(String key) {
        return sh.read(key);
    }

    public void destroyAllActivity() {
        activityMap.values().stream().filter(activity -> activity != null).forEach(Activity::finish);
    }

    public void destroyActivity(String activityName) {
        Set<String> keySet = activityMap.keySet();
        keySet.stream().filter(key -> key.equals(activityName)).forEach(key -> activityMap.get(key).finish());
    }

    public void destroyAllActivityWithout(String... s) {
        activityMap.keySet().stream().filter(key -> Utils.varArgsContain(key, s)).forEach(key -> activityMap.get(key).finish());
    }

    public BaseActivity getTheActivity(String activityName) {
        Set<String> keySet = activityMap.keySet();
        for (String key : keySet) {
            if (key.equals(activityName)) {
                return activityMap.get(key);
            }
        }
        return null;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void showLoading() {
        Utils.hideAllInput(baseActivity);
        if (!loadingDialog.isShowing() && !loaded) loadingDialog.show();
    }

    public void dismissLoading() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
//            loadingDialog = new LoadingDialog(baseActivity);
        }
    }
}
