package com.lazyeraser.imas.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.utils.view.LoadingDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by lazyEraser on 2017/4/19.
 */

public class Base {

    private final static Map<String, BaseActivity> activityMap = new HashMap<>();
    private final static Stack<BaseActivity> activityStack = new Stack<>();

    private SharedHelper sh;
    private LoadingDialog loadingDialog;


    protected AlertDialog alert = null;
    protected AlertDialog.Builder builder = null;

    private boolean canceled, loaded;
    private BaseActivity baseActivity;
    private BaseFragment baseFragment;
    private boolean inFragment = false;

    public void init(BaseActivity activity){
        baseActivity = activity;
        activityStack.push(activity);
        sh = new SharedHelper(activity.getApplicationContext());
        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setOnCancelListener((dialogInterface -> canceled = true));
        String screenName = activity.getClass().getSimpleName();
        activityMap.put(screenName  + System.currentTimeMillis(), activity);
        if (SStaticR.ANALYTICS_ON){
            // Obtain the shared Tracker instance.
            LIVE application = (LIVE)baseActivity.getApplication();
            Tracker mTracker = application.getDefaultTracker();
            mTracker.setScreenName(screenName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void init(BaseFragment fragment){
        this.baseFragment = fragment;
        inFragment = true;
        init((BaseActivity) fragment.getActivity());
    }

    public static BaseActivity getTopContext(){
        while (true){
            BaseActivity c = activityStack.peek();
            if (c != null){
                return c;
            }else if (activityStack.size() > 0){
                activityStack.pop();
            }else {
                return null;
            }
        }
    }


    /*------------通用方法-------------*/
    public void setOCL(@IdRes int id, View.OnClickListener ocl){
        baseActivity.findViewById(id).setOnClickListener(ocl);
    }
    //弹Toast
    public void makeToast(String str) {
        try {
            Toast.makeText(baseActivity, str, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //
        }
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

    @SafeVarargs
    public final static void jumpWithTran(BaseActivity mContext, Class dst, View view, String name, Pair<String, String>... data) {
        Pair<Intent, Bundle> pair = prepareTran(mContext, dst, view, name, data);
        ActivityCompat.startActivity(mContext, pair.first, pair.second);
    }

    @SafeVarargs
    public final static void jumpForResultWithTran(int code, BaseActivity mContext, Class dst, View view, String name, Pair<String, String>... data) {
        Pair<Intent, Bundle> pair = prepareTran(mContext, dst, view, name, data);
        ActivityCompat.startActivityForResult(mContext, pair.first, code, pair.second);
    }

    @SafeVarargs
    private final static Pair<Intent, Bundle> prepareTran(BaseActivity mContext, Class dst, View view, String name, Pair<String, String>... data) {
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, view, name);
        Bundle bundle = transitionActivityOptions.toBundle();
        if (bundle == null) {
            bundle = new Bundle();
        }
        for (Pair<String, String> p : data) {
            bundle.putString(p.first, p.second);
        }
        Intent intent = new Intent();
        intent.setClass(mContext, dst);
        intent.putExtras(bundle);
        return new Pair<>(intent, bundle);
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

    public boolean getSP(String key){
        String value = spRead(key);
        return !TextUtils.isEmpty(value) && value.equals("true");
    }

    public static void destroyAllActivity() {
        for (BaseActivity activity : activityMap.values()) {
            if (activity != null){
                activity.finish();
            }
        }
    }

    public void destroyActivity(String activityName) {
        BaseActivity activity = activityMap.get(activityName);
        if (activity != null){
            activity.finish();
        }
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
        if (!loadingDialog.isShowing() && !loaded) {
            try {
                loadingDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dismissLoading() {
        if (loadingDialog.isShowing()) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            loadingDialog = new LoadingDialog(baseActivity);
        }
    }
}
