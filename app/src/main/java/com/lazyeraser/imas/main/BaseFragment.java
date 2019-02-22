package com.lazyeraser.imas.main;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lazyeraser.imas.derehelper.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lazyeraser on 2017/1/3.
 */
public class BaseFragment extends Fragment {

    public BaseActivity mContext;
    public Base umi;
    private View baseView;

    private ViewDataBinding binding;
    private View.OnClickListener menuAction_start, menuAction_end;
    private List<BaseViewModel> viewModels;

    private int title;

    public void setTitle(@StringRes int title) {
        this.title = title;
    }

    public @StringRes int getTitle() {
        return title;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (umi == null) umi = new Base();
        umi.init(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity)getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    public BaseFragment setBinding(@LayoutRes int layoutId, LayoutInflater inflater, @Nullable ViewGroup container){
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);
        return this;
    }

    public BaseFragment setVariable(int id, Object v){
        binding.setVariable(id, v);
        if (v instanceof BaseViewModel){
            if (viewModels == null){
                viewModels = new ArrayList<>();
            }
            viewModels.add((BaseViewModel)v);
        }
        return this;
    }

    public View getInflatedView(){
        baseView = binding.getRoot();
        return baseView;
    }

    public View getBView(@IdRes int id){
        return baseView.findViewById(id);
    }

    protected void initView(){}

    protected Toolbar initToolbar(@IdRes int id, boolean backBtn, String title) {
        Toolbar toolbar = (Toolbar) getBView(id);
        if (toolbar == null) {
            return null;
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorInvert));
        toolbar.setBackgroundResource(R.color.colorPrimaryStatusBar);
        mContext.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        ActionBar actionBar = mContext.getSupportActionBar();
        assert actionBar != null;
        if (!TextUtils.isEmpty(title)) {
            actionBar.setTitle(title);
        }
        String label = toolbar.getTitle().toString();
        if (label.contains("/")) {
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

    protected Toolbar initToolbar(@IdRes int id) {
        return initToolbar(id, true, null);
    }

    protected Toolbar initToolbar(@IdRes int id, String title) {
        return initToolbar(id, false, title);
    }

    public View.OnClickListener getMenuAction_start() {
        return menuAction_start;
    }

    public void setMenuAction_start(View.OnClickListener menuAction_start) {
        this.menuAction_start = menuAction_start;
    }

    public View.OnClickListener getMenuAction_end() {
        return menuAction_end;
    }

    public void setMenuAction_end(View.OnClickListener menuAction_end) {
        this.menuAction_end = menuAction_end;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModels != null){
            for (BaseViewModel viewModel : viewModels) {
                if (viewModel != null){
                    viewModel.onDestroy();
                }
            }
        }
    }
}
