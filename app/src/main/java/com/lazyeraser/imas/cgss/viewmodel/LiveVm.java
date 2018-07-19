package com.lazyeraser.imas.cgss.viewmodel;

import android.annotation.SuppressLint;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.util.SparseArray;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.cgss.entity.LiveDetail;
import com.lazyeraser.imas.cgss.entity.Manifest;
import com.lazyeraser.imas.cgss.entity.Note;
import com.lazyeraser.imas.cgss.entity.SongRaw;
import com.lazyeraser.imas.cgss.service.CGSSService;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.FileHelper;
import com.lazyeraser.imas.cgss.utils.LZ4Helper;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.view.BeatMapActivity;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.retrofit.ExceptionHandler;
import com.lazyeraser.imas.retrofit.RetrofitProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2018/1/6.
 *
 */

public class LiveVm extends BaseViewModel {

    public final ObservableField<String> difficultyName = new ObservableField<>();
    public final ObservableField<String> level = new ObservableField<>();

    private LiveDetail liveDetail;


    public final static SparseArray<String> difNameMap = new SparseArray<>();
    static {
        difNameMap.put(1, "DEBUT");
        difNameMap.put(2, "REGULAR");
        difNameMap.put(3, "PRO");
        difNameMap.put(4, "MASTER");
        difNameMap.put(5, "MASTER+");
        difNameMap.put(101, "LEGACY MASTER+");
        difNameMap.put(11, "LIGHT");
        difNameMap.put(12, "TRICK");
    }

    public LiveVm(BaseActivity mContext, LiveDetail liveDetail) {
        super(mContext);
        difficultyName.set(difNameMap.get(liveDetail.difficulty_type));
        level.set(String.valueOf(liveDetail.level_vocal));
        this.liveDetail = liveDetail;
    }

    public final ReplyCommand goBeatMapView = new ReplyCommand(() ->{
        loadBeatMapData(liveDetail.live_data_id, liveDetail.difficulty_type);
    });

    @SuppressLint("DefaultLocale")
    private void loadBeatMapData(int liveId, int diffcult){
        String dbFile = String.format("/musicscores/musicscores_m%03d.bdb", liveId);
        String fileName = String.format("musicscores/m%03d/%d_%d.csv", liveId, liveId, diffcult);
        try {
            Observable.just(DBHelper.with(mContext, dbFile)
                    .getBean("blobs", SongRaw.class, "name", fileName))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {
                        SongRaw song = (SongRaw)o;
                        if (song != null){
                            String value = new String(song.data);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", value);
                            bundle.putString("name", difficultyName.get());
                            umi.dismissLoading();
                            umi.jumpTo(BeatMapActivity.class, bundle);
                        }else {
                            updateBeatMapFile(liveId, diffcult);
                        }
                    }, throwable -> updateBeatMapFile(liveId, diffcult));
        } catch (Exception e) {
            e.printStackTrace();
            updateBeatMapFile(liveId, diffcult);
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateBeatMapFile(int liveId, int diffcult){
        umi.showLoading();
        umi.makeToast(R.string.update_hint4);
        String dbFile = String.format("/musicscores/musicscores_m%03d.bdb", liveId);
        String fileName = String.format("musicscores_m%03d.bdb", liveId);
        try {
            Observable.just(DBHelper.with(mContext, DBHelper.DB_NAME_manifest)
                    .getBean(DBHelper.CGSS_TABLE_NAME_Manifest, Manifest.class,
                            "name", fileName))
                    .subscribeOn(Schedulers.io())
                    .subscribe(o -> {
                        Manifest manifest = (Manifest)o;
                        Observable<ResponseBody> file = umi.getSP(SharedHelper.KEY_USE_REVERSE_PROXY) ?
                                RetrofitProvider.getInstance(false).create(CGSSService.class).getResourcesRP(manifest.getHash()) :
                                RetrofitProvider.getInstance(false).create(CGSSService.class).getResources(manifest.getHash(), umi.spRead(SharedHelper.KEY_UNITY_VERSION));
                        file.subscribeOn(Schedulers.io())
                                .subscribe(responseBody -> {
                                    try {
                                        FileHelper.writeFile(LZ4Helper.uncompressCGSS(responseBody.bytes()), mContext.getFilesDir().getAbsolutePath() + "/musicscores", fileName);
                                        DBHelper.refresh(mContext, dbFile);
                                        loadBeatMapData(liveId, diffcult);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }, ExceptionHandler::handleException);

                    }, ExceptionHandler::handleException);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
