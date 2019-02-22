package com.lazyeraser.imas.cgss.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.view.MainActivity;
import com.lazyeraser.imas.cgss.viewmodel.AboutViewModel;
import com.lazyeraser.imas.derehelper.BR;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseFragment;

/**
 * Created by lazyeraser on 2017/11/27.
 */

public class AboutFrag extends BaseFragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.setBinding(R.layout.frag_about, inflater, container)
                .setVariable(BR.viewModel, new AboutViewModel(mContext))
                .getInflatedView();
    }

    @Override
    protected void initView() {
        Toolbar toolbar = initToolbar(R.id.toolBar, getString(R.string.about));
        toolbar.setNavigationIcon(R.drawable.umi_ic_menu_white);
        toolbar.setNavigationOnClickListener(view -> Messenger.getDefault().sendNoMsg(MainActivity.TOKEN_MOVE_NAVI));

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
