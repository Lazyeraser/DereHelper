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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.derehelper.R;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lazyeraser on 2017/1/3.
 */
public class BaseActivity extends RxAppCompatActivity implements View.OnClickListener {

    private String actionBarTitle;
    private String actionBarTxt;
    public Context mContext;
    public Base umi;

    private ViewDataBinding binding;
    private List<BaseViewModel> viewModels;

    //导航栏类型
    public final static int ACTIONBAR_TYPE_NULL = 0; //无按钮
    public final static int ACTIONBAR_TYPE_PLUS = 1; //"+"按钮
    public final static int ACTIONBAR_TYPE_TXT = 5; //文字
    public final static int ACTIONBAR_TYPE_MENU = 6; //菜单
    public final static int ACTIONBAR_TYPE_CUSTOM = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        umi = new Base();
        umi.init(this);
        mContext = this;
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(0xFFB08870);
            getWindow().setNavigationBarColor(0xFFB08870);
        }
    }

    public BaseActivity setBinding(@LayoutRes int layoutId){
        binding = DataBindingUtil.setContentView(this, layoutId);
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

    public View getBView(@IdRes int id){
        return binding.getRoot().findViewById(id);
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

    //设置标题
    public void setActionBarTitle(String actionBarTitle) {
        this.actionBarTitle = actionBarTitle;
    }

    public void setActionBarTitle(@StringRes int actionBarTitle) {
        this.actionBarTitle = getString(actionBarTitle);
    }

    public void setActionBarTxt(String actionBarTxt) {
        this.actionBarTxt = actionBarTxt;
    }

    public void setActionBarTitleAgain(String actionBarTxt) {
        this.actionBarTitle = actionBarTxt;
        TextView title = (TextView) findViewById(R.id.actionBar_title);
        title.setText(actionBarTxt);
    }

    public void setActionBarTitleAgain(@StringRes int actionBarTitle) {
        setActionBarTitleAgain(getString(actionBarTitle));
    }


    //初始化导航栏（要先设置标题）
    boolean actionBarInited = false;
    public void initActionBar(int type) {
        initActionBar(type, null, null);
    }

    public void initActionBar(int type, @DrawableRes Integer idStart, @DrawableRes Integer idEnd) {

        if (!actionBarInited){
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setCustomView(R.layout.umi_actionbar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            findViewById(R.id.actionBar_btn_start).setOnClickListener(this);
            findViewById(R.id.actionBar_btn_end).setOnClickListener(this);
            actionBarInited = true;
        }

        TextView title = (TextView) findViewById(R.id.actionBar_title);
        title.setText(actionBarTitle);
        ImageView actionBarBtn_Start = (ImageView) findViewById(R.id.actionBar_btn_start_img);
        if (idStart == null) {
//            Picasso.with(mContext).load(R.drawable.umi_ic_back).into(actionBarBtn_Start);
            actionBarBtn_Start.setImageResource(R.drawable.umi_ic_back);
        }
        TextView actionBarBtn_Txt = (TextView) findViewById(R.id.actionBar_txtBtn);
        ImageView actionBarBtn_End = (ImageView) findViewById(R.id.actionBar_btn_end_img);
        switch (type) {
            case 0:
                break;
            case 1:
                Picasso.with(mContext).load(R.drawable.umi_ic_add).into(actionBarBtn_End);
                actionBarBtn_End.setVisibility(View.VISIBLE);
                actionBarBtn_End.setOnClickListener(this);
                break;
            case 5:
                actionBarBtn_Txt.setText(actionBarTxt);
                actionBarBtn_Txt.setVisibility(View.VISIBLE);
                actionBarBtn_Txt.setOnClickListener(this);
                break;
            case 6:
                actionBarBtn_End.setImageResource(R.drawable.umi_ic_menu_white);
                actionBarBtn_End.setVisibility(View.VISIBLE);
                actionBarBtn_End.setOnClickListener(this);
                break;
            case 7:
                if (idStart != null) {
                    actionBarBtn_Start.setVisibility(View.VISIBLE);
                    actionBarBtn_Start.setImageResource(idStart);
                }else {
                    actionBarBtn_Start.setVisibility(View.GONE);
                }
                if (idEnd != null) {
                    actionBarBtn_End.setVisibility(View.VISIBLE);
                    actionBarBtn_End.setImageResource(idEnd);
                }else {
                    actionBarBtn_End.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void actionBarStartAction(View.OnClickListener onClickListener) {
        findViewById(R.id.actionBar_btn_start).setOnClickListener(onClickListener);
    }

    public void actionBarEndAction(View.OnClickListener onClickListener) {
        findViewById(R.id.actionBar_btn_end).setOnClickListener(onClickListener);
    }

    public void hideActionBack(){
        findViewById(R.id.actionBar_btn_start).setVisibility(View.GONE);
        findViewById(R.id.actionBar_btn_start_img).setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.actionBar_btn_start) {
            backBtnAction();
        } else if (i == R.id.actionBar_btn_end) {
            View view1 = findViewById(R.id.actionBar_txtBtn);
            View view3 = findViewById(R.id.actionBar_btn_end_img);
            if (view1.getVisibility() == View.VISIBLE) view1.callOnClick();
            if (view3.getVisibility() == View.VISIBLE) view3.callOnClick();

        }
    }

    //返回键动作，默认结束当前activity
    protected void backBtnAction() {
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
        if (SStaticR.ANALYTICS_ON){

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SStaticR.ANALYTICS_ON){

        }
    }
}