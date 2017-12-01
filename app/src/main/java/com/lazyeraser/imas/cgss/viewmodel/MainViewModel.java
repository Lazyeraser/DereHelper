package com.lazyeraser.imas.cgss.viewmodel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.util.SparseArray;

import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.entity.CardIndex;
import com.lazyeraser.imas.cgss.entity.Chara;
import com.lazyeraser.imas.cgss.entity.CharaIndex;
import com.lazyeraser.imas.cgss.service.CardService;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.retrofit.ExceptionHandler;
import com.lazyeraser.imas.retrofit.RetrofitProvider;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/9/18.
 */

public class MainViewModel extends BaseViewModel {


    private List<String> nowIds_card;
    private List<String> newIds_card = new ArrayList<>();

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
        checkDataUpdate();
    }

    public void checkDataUpdate(){
        umi.showLoading();
        newIds_card.clear();
        //cards
        Observable<List<String>> obNow_card = Observable.create(subscriber -> {
            subscriber.onNext(DBHelper.with(mContext)
                    .queryTable(DBHelper.TABLE_NAME_Card)
                    .column("id")
                    .go());
            subscriber.onCompleted();
        });

        obNow_card
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                .subscribe(strings -> {
                    nowIds_card = strings;
                    RetrofitProvider.getInstance().create(CardService.class)
                            .getCardIdList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                            .doAfterTerminate(this::checkData)
                            .subscribe(idList -> {
                                for (CardIndex cardIndex : idList) {
                                    if (!nowIds_card.contains(String.valueOf(cardIndex.id))){
                                        newIds_card.add(String.valueOf(cardIndex.id));
                                    }
                                    if (!nowIds_card.contains(String.valueOf(cardIndex.id + 1))){
                                        newIds_card.add(String.valueOf(cardIndex.id + 1));
                                    }
                                }
                            }, ExceptionHandler::handleException);
                });
    }

    private void checkData(){

        total = newIds_card.size();
        if (total > 0){
            umi.dismissLoading();
            haveUpdate.set(true);
            agree.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable observable, int i) {
                    if (agree.get()){
                        agree.set(false);
                        upDateDB();
                        solved = 0;
                    }
                }
            });
        }else {
            upToDate.set(true);
        }

    }

    @SuppressLint("UseSparseArrays")
    private void upDateDB(){
        Utils.mPrint("Start updating");
        isShowProgress.set(true);

        StringBuilder ids = new StringBuilder();
        List<String> idList = new ArrayList<>();
        int i = 0;
        for (String newId : newIds_card) {
            ids.append(newId);
            ids.append(",");
            if (i < 50){
                i ++;
            }else {
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

            for (String json : DBHelper.with(mContext)
                    .queryTable(DBHelper.TABLE_NAME_Chara_Index)
                    .column("json")
                    .go()) {
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

                if (oldChara){ // 旧偶像有新卡
                    charaIndexMap_update.put(card.getChara_id(), charaIndexMap_exist.get(card.getChara_id()));
                    total ++;
                }

                if (!oldChara && !charaIndexMap_new.containsKey(card.getChara_id())){
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
                    if (oldChara){
                        charaIndexMap_update.get(card.getChara_id()).getCards().add(card.getId());
                    }else {
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
                        if (newCardList.size() == total){ // 完成下载，开始写入数据库
                            dataBaseOB.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                                    .subscribe(b -> { // 更新处理进度
                                        progress.set((float)++solved / (float) total);
                                        progressTxt.set(solved + "/" + total);
                                    });
                        }
                    })
                    .subscribe(newCardList::addAll, ExceptionHandler::handleException);

        }

    }
}
