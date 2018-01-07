package com.lazyeraser.imas.cgss.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Handler;
import android.widget.ListView;

import com.lazyeraser.imas.cgss.entity.LiveDetail;
import com.lazyeraser.imas.cgss.entity.Song;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.view.SongDetailActivity;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2018/1/4.
 */

public class SongVM extends BaseViewModel {

    public final ObservableField<Song> song = new ObservableField<>();
    public final ObservableField<String> songJacketUrl = new ObservableField<>();
    public final ObservableField<Integer> songIcon = new ObservableField<>();

    public ObservableList<LiveVm> itemViewModel;
    public ItemView itemView;


    public SongVM(BaseActivity mContext, Song song) {
        super(mContext);
        song.name = song.name.replace("\\n", "\n");
        songJacketUrl.set(String.format(SStaticR.SERVER_URL_RES + "/jacket/%s.png", song.music_id));
        switch (song.type){
            case 1:
                songIcon.set(R.drawable.ic_song_cute);
                break;
            case 2:
                songIcon.set(R.drawable.ic_song_cool);
                break;
            case 3:
                songIcon.set(R.drawable.ic_song_passion);
                break;
            case 4:
                songIcon.set(R.drawable.ic_song_all);
                break;
        }
        if (mContext instanceof SongDetailActivity){
            song.discription = song.discription.replace("\\n", "\n");
            itemViewModel = new ObservableArrayList<>();
            itemView = ItemView.of(com.lazyeraser.imas.derehelper.BR.viewModel, R.layout.item_list_live);
            loadLiveDetail(String.valueOf(song.music_id));
        }
        this.song.set(song);
    }

    private static final String sql_raw = "SELECT b.* from live_data a, live_detail b " +
            "where b.live_data_id = a.id and a.music_data_id = %s " +
            "GROUP BY b.difficulty_type";

    private void loadLiveDetail(String search){
        String sql = String.format(sql_raw, search);
        try {
            Observable<List<LiveDetail>> songList = Observable.just(DBHelper.with(mContext, DBHelper.DB_NAME_master)
                    .getBeanListByRaw(sql, LiveDetail.class));
            songList.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lives -> {
                        for (LiveDetail live : lives) {
                            itemViewModel.add(new LiveVm(mContext, live));
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
