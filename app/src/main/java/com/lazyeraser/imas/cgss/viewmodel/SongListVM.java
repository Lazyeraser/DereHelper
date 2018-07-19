package com.lazyeraser.imas.cgss.viewmodel;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.View;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.entity.Song;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.view.CharaDetailActivity;
import com.lazyeraser.imas.cgss.view.SongDetailActivity;
import com.lazyeraser.imas.cgss.view.fragments.SongListFrag;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2018/1/4.
 * 歌曲列表/谱面列表
 */

public class SongListVM extends BaseViewModel {

    public final ObservableList<SongVM> itemViewModel = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(com.lazyeraser.imas.derehelper.BR.viewModel, R.layout.item_list_song);
    public final ObservableField<List<Song>> songData = new ObservableField<>();

    public SongListVM(BaseActivity mContext) {
        super(mContext);
        init("", "");
    }

    public SongListVM(BaseActivity mContext, int charaID) {
        super(mContext);
        init(String.format(" and %d in (c.chara_position_1, c.chara_position_2, c.chara_position_3, c.chara_position_4, c.chara_position_5)", charaID), "");
    }

    private void init(String search, String orderType){
        initFilter();
        load(search, orderType);
        songData.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                if (songData.get().size() > 0){
                    List<SongVM> vmList = new ArrayList<>();
                    for (Song song : songData.get()) {
                        vmList.add(new SongVM(mContext, song));
                    }
                    itemViewModel.addAll(vmList);
                    umi.dismissLoading();
                }else {
                    umi.dismissLoading();
                }

            }
        });
    }

    private void load(String search, String orderType){
        itemViewModel.clear();
        umi.showLoading();
        loadData(mContext, search, orderType, songData);
    }

    private static final String sql_raw =
            "SELECT b.name,b.bpm, b.id as music_id, b.composer, b.lyricist , max( a.type ) type ,min( a.start_date ) start_date, a.event_type , c.chara_position_1, c.chara_position_2, c.chara_position_3, c.chara_position_4, c.chara_position_5, b.name_kana, d.discription " +
            "from live_data a, music_data b, music_info d LEFT OUTER JOIN live_data_position c ON a.id = c.live_data_id " +
            "where a.music_data_id = b.id and d.id = b.id and d.discription <> '？' and music_id not in (1901, 1902, 90001) %s " +
            "GROUP BY b.id " +
            "ORDER BY %s";

    static void loadData(BaseActivity mContext, String search, String orderType, ObservableField<List<Song>> target){
        if (TextUtils.isEmpty(orderType)){
            orderType = "a.start_date DESC";
        }
        String sql = String.format(sql_raw, search, orderType);
        try {
            Observable<List<Song>> songList = Observable.just(DBHelper.with(mContext, DBHelper.DB_NAME_master)
                    .getBeanListByRaw(sql, Song.class));
            songList.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(target::set);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Integer sortMethod; // 0 : desc
    private String sortType;
    private List<String> typeFilter = new ArrayList<>();

    public final ReplyCommand<List<String>> onTypeSelCommand = new ReplyCommand<>(strings -> typeFilter = strings);

    public final ReplyCommand<List<String>> sortTypeCommand = new ReplyCommand<>(strings -> {
        for (Integer integer : SStaticR.sortTypeMap_Song.keySet()) {
            if (mContext.getString(integer).equals(strings.get(0))){
                sortType = SStaticR.sortTypeMap_Song.get(integer);
                break;
            }
        }
    });

    public final ReplyCommand<List<Integer>> sortMethodCommand = new ReplyCommand<>(integers -> sortMethod = integers.get(0));


    public final ReplyCommand doFiltCommand = new ReplyCommand(() ->{
        filterSong();
        Messenger.getDefault().sendNoMsg(SongListFrag.TOKEN_CLOSE_FILTER);
    });

    public final ReplyCommand resetFilterCommand = new ReplyCommand(() -> {
        initFilter();
        Messenger.getDefault().sendNoMsg(SongListFrag.TOKEN_RESET_FILTER);
    });

    private void initFilter(){
        sortMethod = 0;
        sortType = "a.start_date ";
        typeFilter.clear();
        typeFilter.addAll(SStaticR.songTypeMap.values());
    }

    private void filterSong(){
        String search = "";
        String order = "";
        if (!TextUtils.isEmpty(sortType)){
            order = sortType + (sortMethod == 0 ? "DESC" : "");
        }
        if (typeFilter.size() < SStaticR.songTypeMap.values().size() && typeFilter.size() > 0){
            search += " and a.type in (";
            for (String s : typeFilter) {
                search += SStaticR.typeMap_int.get(s.toLowerCase());
                search += ",";
            }
            search = search.substring(0, search.length() - 1);
            search += ") ";
        }else if (typeFilter.size() == 0){
            search += " and 1 <> 1 ";
        }
        load(search, order);
    }
}
