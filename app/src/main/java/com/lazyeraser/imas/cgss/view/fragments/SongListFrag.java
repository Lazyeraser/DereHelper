package com.lazyeraser.imas.cgss.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.utils.view.MultiLineChooseLayout;
import com.lazyeraser.imas.cgss.viewmodel.CharaListViewModel;
import com.lazyeraser.imas.cgss.viewmodel.SongListVM;
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

public class SongListFrag extends BaseFragment {

    DrawerLayout drawerLayout;

    public final static String TOKEN_CLOSE_FILTER = "token_song_close";
    public final static String TOKEN_RESET_FILTER = "token_song_reset";

    public SongListFrag() {
        setTitle(R.string.song_list);
        setMenuAction_end(menu -> umi.moveDrawer(drawerLayout, Gravity.END));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.setBinding(R.layout.frag_song_list, inflater, container)
                .setVariable(BR.viewModel, new SongListVM(mContext))
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
        MultiLineChooseLayout sttC = (MultiLineChooseLayout)getBView(R.id.sttChoose);
        MultiLineChooseLayout stmC = (MultiLineChooseLayout)getBView(R.id.stmChoose);


        List<String> stMethod = Arrays.asList(getString(R.string.sort_method_0), getString(R.string.sort_method_1));
        List<String> stType = Arrays.asList(getString(R.string.online_date), getString(R.string.bpm));
        typeChoose.setList(SStaticR.songTypeMap.values());
        sttC.setList(stType);
        stmC.setList(stMethod);
        typeChoose.selectAll();
        sttC.setIndexItemSelected(0);
        stmC.setIndexItemSelected(0);
    }
}
