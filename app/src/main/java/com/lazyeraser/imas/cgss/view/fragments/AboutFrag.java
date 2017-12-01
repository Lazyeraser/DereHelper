package com.lazyeraser.imas.cgss.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihidea.multilinechooselib.MultiLineChooseLayout;
import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.viewmodel.AboutViewModel;
import com.lazyeraser.imas.cgss.viewmodel.CharaListViewModel;
import com.lazyeraser.imas.derehelper.BR;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseFragment;
import com.lazyeraser.imas.main.SStaticR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lazyeraser on 2017/11/27.
 */

public class AboutFrag extends BaseFragment {


    public AboutFrag() {
        setTitle(R.string.about);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.setBinding(R.layout.frag_about, inflater, container)
                .setVariable(BR.viewModel, new AboutViewModel(mContext))
                .getInflatedView();
    }

    @Override
    protected void initView() {

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
