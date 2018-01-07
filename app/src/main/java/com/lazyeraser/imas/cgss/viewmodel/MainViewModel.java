package com.lazyeraser.imas.cgss.viewmodel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.os.Handler;
import android.text.TextUtils;

import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.entity.CardIndex;
import com.lazyeraser.imas.cgss.entity.Chara;
import com.lazyeraser.imas.cgss.entity.CharaIndex;
import com.lazyeraser.imas.cgss.entity.Manifest;
import com.lazyeraser.imas.cgss.service.CardService;
import com.lazyeraser.imas.cgss.service.CgssService;
import com.lazyeraser.imas.cgss.service.InfoService;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.FileHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.utils.LZ4Helper;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;
import com.lazyeraser.imas.retrofit.ExceptionHandler;
import com.lazyeraser.imas.retrofit.RetrofitProvider;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/9/18.
 */

public class MainViewModel extends BaseViewModel {


    //    private List<String> nowIds_card;
    private List<String> newIds_card = new ArrayList<>();
    private List<String> allIds_card = new ArrayList<>();

    private Integer total = 0;
    private Integer solved = 0;


    public final ObservableBoolean haveUpdate = new ObservableBoolean(false);
    public final ObservableBoolean upToDate = new ObservableBoolean(false);
    public final ObservableBoolean agree = new ObservableBoolean(false);
    public final ObservableBoolean isShowProgress = new ObservableBoolean(false);
    public final ObservableFloat progress = new ObservableFloat(0);
    public final ObservableField<String> progressTxt = new ObservableField<>();

    public MainViewModel(BaseActivity mContext) {
        super(mContext);
        if (umi.getSP(SharedHelper.KEY_AUTO_DATA)) {
            checkDataUpdate();
        }
        if (SStaticR.isDebug) {
            umi.spSave(SharedHelper.KEY_MasterDbHash, "");
            umi.spSave(SharedHelper.KEY_TruthVersion, "");
        }
    }

    public void checkDataUpdate() {
        umi.showLoading();
        newIds_card.clear();
        allIds_card.clear();
        //cards
        Observable<List<String>> now_cardIdList = Observable.just(DBHelper.with(mContext).getAll(DBHelper.TABLE_NAME_Card, "id"));

        Observable<List<CardIndex>> newestCardIndex = RetrofitProvider.getInstance()
                .create(CardService.class)
                .getCardIdList();

        Observable.combineLatest(now_cardIdList, newestCardIndex, (nowIdList, newestIdList) -> {
            if (nowIdList == null || newestIdList == null) {
                return false;
            }
            for (CardIndex cardIndex : newestIdList) {
                allIds_card.add(String.valueOf(cardIndex.id));
                allIds_card.add(String.valueOf(cardIndex.evolution_id));
                if (!nowIdList.contains(String.valueOf(cardIndex.id))) {
                    newIds_card.add(String.valueOf(cardIndex.id));
                }
                if (!nowIdList.contains(String.valueOf(cardIndex.evolution_id))) {
                    newIds_card.add(String.valueOf(cardIndex.evolution_id));
                }
            }
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        checkData();
                    }
                }, ExceptionHandler::handleException);
    }

    private void checkData() {

        total = newIds_card.size();
        // check truth version
        RetrofitProvider.getInstance().create(InfoService.class)
                .getGameInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                .subscribe(info -> {
                    boolean update = false;
                    if (info != null) {
                        String nowTruthVersion = umi.spRead(SharedHelper.KEY_TruthVersion);
                        if (!nowTruthVersion.equals(info.getTruth_version())) {
                            // truth version differs, update manifest.db
                            update = true;
                        } else {
                            info.setTruth_version("");
                        }
                    }
                    if (total > 0) {
                        update = true;
                    }
                    if (update){
                        haveUpdate.set(true);
                    }else {
                        upToDate.set(true);
                    }
                    umi.dismissLoading();
                    android.databinding.Observable.OnPropertyChangedCallback agreeCallBack = new android.databinding.Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(android.databinding.Observable observable, int i) {
                            if (agree.get()) {
                                agree.set(false);
                                solved = 0;
                                boolean isUpdateManifest = info != null && !TextUtils.isEmpty(info.getTruth_version());
                                isShowProgress.set(true);
                                total = allIds_card.size();
                                if (newIds_card.size() > 0 || isUpdateManifest) {
                                    upDateDB(isUpdateManifest, isUpdateManifest ? info.getTruth_version() : null);
                                }
                                agree.removeOnPropertyChangedCallback(this);
                            }
                        }
                    };
                    agree.addOnPropertyChangedCallback(agreeCallBack);
                }, ExceptionHandler::handleException);


    }

    private void updateManifest(String truthVersion) {
        progress.set(-1);
        progressTxt.set(mContext.getString(R.string.update_hint_manifest));
        RetrofitProvider.getInstance(false).create(CgssService.class)
                .getManifests(truthVersion)
                .subscribeOn(Schedulers.io())
                .subscribe(body -> {
                    try {
                        FileHelper.writeFile(LZ4Helper.uncompressCGSS(body.bytes()),
                                mContext.getFilesDir().getAbsolutePath(), DBHelper.DB_NAME_manifest);
                        // get hash of master.db
                        List<Manifest> list = DBHelper.with(mContext, DBHelper.DB_NAME_manifest)
                                .getBeanList(DBHelper.CGSS_TABLE_NAME_Manifest, Manifest.class,
                                        "name", Collections.singletonList("master.mdb"));
                        String masterHash = list.get(0).getHash();
                        List<Observable<Boolean>> obList = new ArrayList<>();
                        if (!masterHash.equals(umi.spRead(SharedHelper.KEY_MasterDbHash))) {
                            // update master.db
                            obList.add(createFileOB(masterHash, DBHelper.DB_NAME_master, mContext.getFilesDir().getAbsolutePath()));
                        }
                        // update music (beatMap
                        List<Manifest> musicList = DBHelper.with(mContext, DBHelper.DB_NAME_manifest)
                                .getBeanListLike(DBHelper.CGSS_TABLE_NAME_Manifest, Manifest.class,
                                        "name", "%musicscores_%.bdb");
                        String musicDataPath = mContext.getFilesDir().getAbsolutePath() + "/musicscores";
                        for (Manifest manifest : musicList) {
                            if (!FileHelper.isFileExists(musicDataPath, manifest.getName())) {
                                obList.add(createFileOB(manifest.getHash(), manifest.getName(), musicDataPath));
                            }
                        }
                        total = obList.size();
                        solved = 0;
                        if (total == 0){
                            Observable.just(true)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(b -> {
                                        progress.set(1);
                                        progressTxt.set("100%");
                                        // update masterHash
                                        if (!masterHash.equals(umi.spRead(SharedHelper.KEY_MasterDbHash))) {
                                            umi.spSave(SharedHelper.KEY_MasterDbHash, masterHash);
                                        }
                                        // update truth version
                                        umi.spSave(SharedHelper.KEY_TruthVersion, truthVersion);
                                    });
                        }else {
                            Observable.combineLatest(obList, args -> true)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                                    .subscribe(b -> {
                                        progress.set(1);
                                        progressTxt.set("100%");
                                        // update masterHash
                                        if (!masterHash.equals(umi.spRead(SharedHelper.KEY_MasterDbHash))) {
                                            umi.spSave(SharedHelper.KEY_MasterDbHash, masterHash);
                                        }
                                        // update truth version
                                        umi.spSave(SharedHelper.KEY_TruthVersion, truthVersion);
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, ExceptionHandler::handleException);
    }

    private Observable<Boolean> createFileOB(String hash, String fileName, String filePath) {
        return Observable.create(subscriber -> {
            RetrofitProvider.getInstance(false).create(CgssService.class)
                    .getResources(hash)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        try {
                            FileHelper.writeFile(LZ4Helper.uncompressCGSS(responseBody.bytes()), filePath, fileName);
                            progress.set((float) ++solved / (float) total);
                            progressTxt.set(getProgress());
                            subscriber.onNext(true);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        });
    }


    private String getProgress(){
        return (int)((float)solved * 100 / (float)total) + "%";
    }

    private static final AtomicBoolean isUpdating = new AtomicBoolean();
    private void upDateDB(boolean updateManifest, String truthVersion) {
        synchronized (isUpdating) {
            if (isUpdating.get()) {
                return;
            }
            isUpdating.set(true);
            progress.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    if (progress.get() >= 1){
                        isUpdating.set(false);
                    }
                }
            });
        }
        doUpDateDB(updateManifest, truthVersion);
    }

    @SuppressLint("UseSparseArrays")
    private void doUpDateDB(boolean updateManifest, String truthVersion) {

        Utils.mPrint("Start updating");

        StringBuilder ids = new StringBuilder();
        List<String> idList = new ArrayList<>();
        int i = 0;
        for (String newId : updateManifest ? allIds_card : newIds_card) {
            ids.append(newId);
            ids.append(",");
            if (i < 50) {
                i++;
            } else {
                idList.add(ids.toString());
                ids.setLength(0);
                i = 0;
            }
        }
        idList.add(ids.toString());

        List<Card> newCardList = new ArrayList<>();
        // 数据库操作
        Observable<Boolean> dataBaseOB = Observable.create(subscriber -> {
            ContentValues contentValues = new ContentValues();
            Map<Integer, CharaIndex> charaIndexMap_exist = new HashMap<>();
            Map<Integer, CharaIndex> charaIndexMap_new = new HashMap<>();
            Map<Integer, CharaIndex> charaIndexMap_update = new HashMap<>();
            Map<Integer, Chara> charaMap = new HashMap<>();

            for (String json : DBHelper.with(mContext).getAll(DBHelper.TABLE_NAME_Chara_Index, "json")) {
                CharaIndex ci = JsonUtils.getBeanFromJson(json, CharaIndex.class);
                charaIndexMap_exist.put(ci.getChara_id(), ci);
            }
//                            SparseArray<CharaIndex> charaIndexMap = new SparseArray<>();
            for (Card card : newCardList) {
                contentValues.clear();
                contentValues.put("id", String.valueOf(card.getId()));
                contentValues.put("json", JsonUtils.getJsonFromBean(card));
                subscriber.onNext(DBHelper.with(mContext).insertData(DBHelper.TABLE_NAME_Card, contentValues));
                boolean oldChara = charaIndexMap_exist.containsKey(card.getChara_id());

                if (oldChara) { // 旧偶像有新卡
                    if (!charaIndexMap_update.containsKey(card.getChara_id())) {
                        total++;
                    }
                    charaIndexMap_update.put(card.getChara_id(), charaIndexMap_exist.get(card.getChara_id()));
                }

                if (!oldChara && !charaIndexMap_new.containsKey(card.getChara_id())) { // 新偶像，先新增
                    Chara chara = card.getChara();
                    charaMap.put(card.getChara_id(), chara);
                    CharaIndex charaIndex = new CharaIndex();
                    charaIndex.setChara_id(chara.getChara_id());
                    charaIndex.setConventional(chara.getConventional());
                    charaIndex.setKana_spaced(chara.getKana_spaced());
                    charaIndex.setKanji_spaced(chara.getKanji_spaced());
                    List<Integer> cardList = new ArrayList<>();
                    cardList.add(card.getId());
                    charaIndex.setCards(cardList);
                    charaIndexMap_new.put(chara.getChara_id(), charaIndex);
                    total = total + 2;
                } else {
                    if (oldChara) {
                        charaIndexMap_update.get(card.getChara_id()).getCards().add(card.getId());
                    } else {
                        charaIndexMap_new.get(card.getChara_id()).getCards().add(card.getId());
                    }
                }
            }
            // 更新旧偶像卡片目录
            for (CharaIndex charaIndex : charaIndexMap_update.values()) {
                contentValues.clear();
                contentValues.put("id", String.valueOf(charaIndex.getChara_id()));
                contentValues.put("json", JsonUtils.getJsonFromBean(charaIndex));
                subscriber.onNext(DBHelper.with(mContext).updateData(DBHelper.TABLE_NAME_Chara_Index, contentValues, "id = ?", new String[]{String.valueOf(charaIndex.getChara_id())}));
            }
            // 偶像目录信息新增
            for (CharaIndex charaIndex : charaIndexMap_new.values()) {
                contentValues.clear();
                contentValues.put("id", String.valueOf(charaIndex.getChara_id()));
                contentValues.put("json", JsonUtils.getJsonFromBean(charaIndex));
                subscriber.onNext(DBHelper.with(mContext).insertData(DBHelper.TABLE_NAME_Chara_Index, contentValues));
            }
            // 偶像详情信息增加
            for (Chara chara : charaMap.values()) {
                contentValues.clear();
                contentValues.put("id", String.valueOf(chara.getChara_id()));
                contentValues.put("json", JsonUtils.getJsonFromBean(chara));
                subscriber.onNext(DBHelper.with(mContext).insertData(DBHelper.TABLE_NAME_Chara_Detail, contentValues));
            }
            subscriber.onCompleted();
        });
        for (String idString : idList) {
            RetrofitProvider.getInstance().create(CardService.class)
                    .getCardList(idString)
                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                    .doAfterTerminate(() -> {
                        if (newCardList.size() >= total) { // 完成下载，开始写入数据库
                            dataBaseOB.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                                    .subscribe(b -> { // 更新处理进度
                                        if (b) {
                                            if (solved < total - 1) {
                                                progress.set((float) ++solved / (float) total);
                                                progressTxt.set(getProgress());
                                            } else if (!updateManifest || truthVersion == null) {
                                                progress.set(1);
                                                progressTxt.set("100%");
                                            } else {
                                                updateManifest(truthVersion);
                                            }
                                        } else {
                                            progressTxt.set(mContext.getString(R.string.update_error));
                                        }
                                    }, ExceptionHandler::handleException);
                        }
                    })
                    .subscribe(newCardList::addAll, ExceptionHandler::handleException);

        }

    }
}
