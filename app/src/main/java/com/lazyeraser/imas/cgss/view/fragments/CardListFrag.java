package com.lazyeraser.imas.cgss.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lazyeraser.imas.cgss.utils.view.MultiLineChooseLayout;
import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.viewmodel.CardListViewModel;
import com.lazyeraser.imas.derehelper.BR;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseFragment;
import com.lazyeraser.imas.main.SStaticR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lazyeraser on 2017/11/27.
 * CardList in main menu
 */

public class CardListFrag extends BaseFragment {

    DrawerLayout drawerLayout;
    MultiLineChooseLayout skillTypeC;
    List<String> skillTypeList;

    public final static int TOKEN_CLOSE_FILTER = 450;
    public final static int TOKEN_RESET_FILTER = 451;

    public CardListFrag() {
        setTitle(R.string.card_list);
        setMenuAction_end(menu -> umi.moveDrawer(drawerLayout, Gravity.END));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.setBinding(R.layout.frag_card_list, inflater, container)
                .setVariable(BR.viewModel, new CardListViewModel(mContext))
                .getInflatedView();
    }

    @Override
    protected void initView() {
        drawerLayout = (DrawerLayout)getBView(R.id.drawerLayout);
        // filter button
        initChooseSet();
        getBView(R.id.empty_skill_type_choose).setOnClickListener(v -> skillTypeC.cancelAllSelectedItems());
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

    public void initChooseSet(){
        MultiLineChooseLayout rareChoose = (MultiLineChooseLayout)getBView(R.id.rareChoose);
        MultiLineChooseLayout typeChoose = (MultiLineChooseLayout)getBView(R.id.typeChoose);
        MultiLineChooseLayout evoC = (MultiLineChooseLayout)getBView(R.id.evoChoose);
        MultiLineChooseLayout sttC = (MultiLineChooseLayout)getBView(R.id.sttChoose);
        MultiLineChooseLayout stmC = (MultiLineChooseLayout)getBView(R.id.stmChoose);
        skillTypeC = (MultiLineChooseLayout)getBView(R.id.skillTypeChoose);

        List<String> rare = new ArrayList<>();
        List<String> type = new ArrayList<>();
        List<String> stt = new ArrayList<>();
        skillTypeList = new ArrayList<>();
        List<String> evo = Arrays.asList(getString(R.string.evo_before), getString(R.string.evo_after));
        List<String> stm = Arrays.asList(getString(R.string.sort_method_0), getString(R.string.sort_method_1));
        rare.addAll(SStaticR.rarityMap_lite.values());
        type.addAll(SStaticR.typeMap.values());
        stt.addAll(SStaticR.sortTypeMap_Card.keySet());
        skillTypeList.addAll(SStaticR.skillTypeMap.keySet());

        rareChoose.setList(rare);
        typeChoose.setList(type);
        evoC.setList(evo);
        sttC.setList(stt);
        stmC.setList(stm);
        skillTypeC.setList(skillTypeList);
        rareChoose.setIndexItemSelected(2);
        rareChoose.setIndexItemSelected(3); // default SSR SR
        for (int i = 0; i < type.size(); i++) {
            typeChoose.setIndexItemSelected(i);
        }
        for (int i = 0; i < skillTypeList.size(); i++) {
            skillTypeC.setIndexItemSelected(i);
        }
        evoC.setIndexItemSelected(0); // default before evo
        sttC.setIndexItemSelected(0);
        stmC.setIndexItemSelected(0);

        MultiLineChooseLayout getTypeC = (MultiLineChooseLayout)getBView(R.id.getTypeChoose);
        getTypeC.setResList(CardListViewModel.getTypeMap_UI.values());
        getTypeC.selectAll();
    }
}
