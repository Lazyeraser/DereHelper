package com.lazyeraser.imas.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.derehelper.R;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;



import java.util.ArrayList;
import java.util.List;

import rx.functions.Func0;

/**
 * Created by Lazyeraser on 2017/1/3.
 */
public class BaseActivity extends RxAppCompatActivity implements View.OnClickListener {

    public Context mContext;
    public Base umi;

    private ViewDataBinding binding;
    private List<BaseViewModel> viewModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        umi = new Base();
        umi.init(this);
        mContext = this;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
//        initView();
    }

    public BaseActivity setBinding(@LayoutRes int layoutId){
        binding = DataBindingUtil.setContentView(this, layoutId);
        initView();
        return this;
    }

    public BaseActivity setVariable(int id, Object v){
        binding.setVariable(id, v);
        if (v instanceof BaseViewModel){
            if (viewModels == null){
                viewModels = new ArrayList<>();
            }
            viewModels.add((BaseViewModel)v);
        }
        return this;
    }

    public View getBView(){
        return binding.getRoot();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBView(@IdRes int id){
        return (T) getBView().findViewById(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Toolbar 返回键
                backBtnAction();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            if (onBackBtnClickedListener != null){
                if (onBackBtnClickedListener.onClick()){
                    return true;
                }
            }
            backBtnAction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initView(){
        initToolbar(R.id.toolBar);
    }

    protected Toolbar initToolbar(@IdRes int id, boolean backBtn, String title){
        Toolbar toolbar = getBView(id);
        if (toolbar == null){
            return null;
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorInvert));
        toolbar.setBackgroundResource(R.color.colorPrimaryStatusBar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        if (!TextUtils.isEmpty(title)){
            actionBar.setTitle(title);
        }
        String label = toolbar.getTitle().toString();
        if (label.contains("/")){
            actionBar.setTitle(label.split("/")[0]);
            toolbar.setSubtitle(label.split("/")[1]);
            toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorInvert));
        }

        actionBar.setHomeButtonEnabled(backBtn);
        actionBar.setDisplayHomeAsUpEnabled(backBtn);
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int sHeight = mContext.getResources().getDimensionPixelSize(resourceId);
            toolbar.setPadding(toolbar.getPaddingRight(), sHeight + toolbar.getPaddingTop(),
                    toolbar.getPaddingLeft(), toolbar.getPaddingBottom());
        }
        return toolbar;
    }

    protected Toolbar initToolbar(@IdRes int id){
        return initToolbar(id, true, null);
    }

    protected Toolbar initToolbar(@IdRes int id, String title){
        return initToolbar(id, true, title);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        /*if (i == R.id.actionBar_btn_start) {
            backBtnAction();
        } else if (i == R.id.actionBar_btn_end) {
            View view1 = findViewById(R.id.actionBar_txtBtn);
            View view3 = findViewById(R.id.actionBar_btn_end_img);
            if (view1.getVisibility() == View.VISIBLE) view1.callOnClick();
            if (view3.getVisibility() == View.VISIBLE) view3.callOnClick();

        }*/
    }

    // 返回true则消化掉返回键点击事件
    private Func0<Boolean> backKeyAction;

    public void setBackKeyAction(Func0<Boolean> backKeyAction) {
        this.backKeyAction = backKeyAction;
    }

    //返回键动作，默认结束当前activity
    protected void backBtnAction() {
        if (backKeyAction != null) {
            if (backKeyAction.call()) {
                return;
            }
        }
        Utils.hideAllInput(this);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private OnBackBtnClickedListener onBackBtnClickedListener;

    public void setOnBackBtnClickedListener(OnBackBtnClickedListener onBackBtnClickedListener) {
        this.onBackBtnClickedListener = onBackBtnClickedListener;
    }

    public interface OnBackBtnClickedListener {
        boolean onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}