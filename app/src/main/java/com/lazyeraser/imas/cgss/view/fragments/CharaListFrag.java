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
import com.lazyeraser.imas.cgss.viewmodel.CharaListViewModel;
import com.lazyeraser.imas.derehelper.BR;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseFragment;
import com.lazyeraser.imas.main.SStaticR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lazyeraser on 2017/11/27.
 */

public class CharaListFrag extends BaseFragment {

    DrawerLayout drawerLayout;

    public final static int TOKEN_CLOSE_FILTER = 0x450;
    public final static int TOKEN_RESET_FILTER = 0x451;

    public CharaListFrag() {
        setTitle(R.string.chara_list);
        setMenuAction_end(menu -> umi.moveDrawer(drawerLayout, Gravity.END));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.setBinding(R.layout.frag_chara_list, inflater, container)
                .setVariable(BR.viewModel, new CharaListViewModel(mContext))
                .getInflatedView();
    }

    @Override
    protected void initView() {
        drawerLayout = (DrawerLayout)getBView(R.id.drawerLayout);
        // filter button
        initChooseSet();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Messenger.getDefault().register(mContext, TOKEN_CLOSE_FILTER, () -> drawerLayout.closeDrawer(Gravity.END));
        Messenger.getDefault().register(mContext, TOKEN_RESET_FILTER, this::initChooseSet);
        mContext.setOnBackBtnClickedListener(() ->{
            if (drawerLayout.isDrawerOpen(Gravity.END)){
                drawerLayout.closeDrawer(Gravity.END);
                return true;
            }
            return false;
        });
    }

    private void initChooseSet(){

        MultiLineChooseLayout typeChoose = (MultiLineChooseLayout)getBView(R.id.typeChoose);
        List<String> type = new ArrayList<>();
        type.addAll(SStaticR.typeMap.values());
        typeChoose.setList(type);

        for (int i = 0; i < type.size(); i++) {
            typeChoose.setIndexItemSelected(i);
        }
    }
}
