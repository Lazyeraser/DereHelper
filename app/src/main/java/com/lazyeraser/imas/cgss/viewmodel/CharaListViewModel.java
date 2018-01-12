package com.lazyeraser.imas.cgss.viewmodel;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.kelin.mvvmlight.command.ReplyCommand;
import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.entity.Chara;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.view.CharaDetailActivity;
import com.lazyeraser.imas.cgss.view.MainActivity;
import com.lazyeraser.imas.cgss.view.fragments.CharaListFrag;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;
import com.lazyeraser.imas.retrofit.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/9/19.
 */

public class CharaListViewModel extends BaseViewModel {

    public final ObservableList<CharaViewModel> itemViewModel = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(com.lazyeraser.imas.derehelper.BR.viewModel, R.layout.item_list_chara);

    private Map<Chara, CharaViewModel> charaDataList;

    // go detail activity
    public final ReplyCommand<Pair<Integer, View>> onListItemClickCommand = new ReplyCommand<>(pair -> {
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, pair.second.findViewById(R.id.chara_icon), "chara_icon");
        Bundle bundle = transitionActivityOptions.toBundle();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("theChara", JsonUtils.getJsonFromBean(itemViewModel.get(pair.first).chara.get()));
        Intent intent = new Intent();
        intent.setClass(mContext, CharaDetailActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(mContext, intent, bundle);
    });

    private List<String> typeFilter = new ArrayList<>();

    private String search = "";

    public final ReplyCommand<List<String>> onTypeSelCommand = new ReplyCommand<>(strings -> {
        typeFilter = strings;
        filterChara();
    });

    public final ReplyCommand<String> onSearchCommand = new ReplyCommand<>(string -> {
        search = string;
        filterChara();
    });

    public final ReplyCommand filterCardsCommand = new ReplyCommand(() -> {
        filterChara();
        Messenger.getDefault().sendNoMsg(CharaListFrag.TOKEN_CLOSE_FILTER);

    });

    public final ReplyCommand resetFilterCommand = new ReplyCommand(() -> {
        initFilter();
        Messenger.getDefault().sendNoMsg(CharaListFrag.TOKEN_RESET_FILTER);
    });

    public CharaListViewModel(BaseActivity mContext) {
        super(mContext);
        initFilter();
        loadData();
        Messenger.getDefault().register(mContext, MainActivity.TOKEN_DATA_UPDATED, this::loadData);
    }

    private void loadData() {
        itemViewModel.clear();
        umi.showLoading();
        Observable.just(DBHelper.with(mContext)
                .getAll(DBHelper.TABLE_NAME_Chara_Detail, "json"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(js -> Observable.create((Observable.OnSubscribe<Map<Chara, CharaViewModel>>) subscriber -> {
                    if (js.size() == 0) {
                        subscriber.onNext(new HashMap<Chara, CharaViewModel>());
                        subscriber.onCompleted();
                    }
                    StringBuilder charaJsonBuilder = new StringBuilder();
                    charaJsonBuilder.append("[");
                    for (String j : js) {
                        charaJsonBuilder.append(j);
                        charaJsonBuilder.append(",");
                    }
                    charaJsonBuilder.delete(charaJsonBuilder.length() - 1, charaJsonBuilder.length());
                    charaJsonBuilder.append("]");
                    List<Chara> charas = JsonUtils.getArrayFromJson(charaJsonBuilder.toString(), new TypeToken<List<Chara>>() {
                    });
                    Map<Chara, CharaViewModel> map = new HashMap<>();
                    for (Chara chara : charas) {
                        map.put(chara, new CharaViewModel(mContext, chara));
                    }
                    subscriber.onNext(map);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(charas -> {
                            charaDataList = charas;
                            filterChara();
                        }), ExceptionHandler::handleException);
    }

    private void initFilter() {
        typeFilter.clear();
        typeFilter.addAll(SStaticR.typeMap.values());
    }

    private boolean checkSearch(Chara chara) {
        return TextUtils.isEmpty(search) || Utils.igCaseContain(search, chara.getConventional()) || Utils.igCaseContain(search, chara.getVoice()) || Utils.igCaseContain(search, chara.getName()) || Utils.igCaseContain(search, chara.getName_kana());
    }

    private void filterChara() {

        for (Chara chara : charaDataList.keySet()) {
            CharaViewModel vm = charaDataList.get(chara);
            if (typeFilter.contains(chara.getType().toUpperCase()) && checkSearch(chara)) {
                // 符合条件 如不在当前显示的列表中则加入
                if (!itemViewModel.contains(vm)) {
                    itemViewModel.add(vm);
                }
            } else {
                // 不符合 如果存在则remove
                if (itemViewModel.contains(vm)) {
                    itemViewModel.remove(vm);
                }
            }
        }

        Collections.sort(itemViewModel, (a, b) -> {
            Chara charaA = a.chara.get();
            Chara charaB = b.chara.get();
            boolean desc = false;
            if (charaB.getChara_id() < charaA.getChara_id()) {
                return desc ? -1 : 1;
            } else {
                return desc ? 1 : -1;
            }
        });
        umi.dismissLoading();
    }
}
