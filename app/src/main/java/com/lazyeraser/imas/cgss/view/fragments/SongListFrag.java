package com.lazyeraser.imas.cgss.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.utils.view.MultiLineChooseLayout;
import com.lazyeraser.imas.cgss.view.MainActivity;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.setBinding(R.layout.frag_song_list, inflater, container)
                .setVariable(BR.viewModel, new SongListVM(mContext))
                .getInflatedView();
    }

    @Override
    protected void initView() {
        Toolbar toolbar = initToolbar(R.id.toolBar, getString(R.string.song_list));
        toolbar.setNavigationIcon(R.drawable.umi_ic_menu_white);
        toolbar.setNavigationOnClickListener(view -> Messenger.getDefault().sendNoMsg(MainActivity.TOKEN_MOVE_NAVI));

        drawerLayout = (DrawerLayout)getBView(R.id.drawerLayout);
        // filter button
        initChooseSet();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        menu.findItem(R.id.nav_filter).setOnMenuItemClickListener(menuItem -> {
            umi.moveDrawer(drawerLayout, Gravity.END);
            return true;
        });
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
