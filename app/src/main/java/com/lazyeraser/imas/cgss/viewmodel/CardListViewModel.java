package com.lazyeraser.imas.cgss.viewmodel;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.kelin.mvvmlight.command.ReplyCommand;
import com.kelin.mvvmlight.messenger.Messenger;
import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.view.CardDetailActivity;
import com.lazyeraser.imas.cgss.view.MainActivity;
import com.lazyeraser.imas.cgss.view.fragments.CardListFrag;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;
import com.lazyeraser.imas.retrofit.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/9/12.
 * card list main vm
 */

public class CardListViewModel extends BaseViewModel {

    // viewModel for card list
    public final ObservableList<CardViewModel> itemViewModel = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(com.lazyeraser.imas.derehelper.BR.viewModel, R.layout.item_list_card);

    private ObservableField<Map<Card, CardViewModel>> cardDataList = new ObservableField<>();

    // getAll detail activity
    public final ReplyCommand<Pair<Integer, View>> onListItemClickCommand = new ReplyCommand<>(pair -> {
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, pair.second.findViewById(R.id.card_icon), "card_icon");
        Bundle bundle = transitionActivityOptions.toBundle();
        if (bundle == null){
            bundle = new Bundle();
        }
        bundle.putString("theCard", JsonUtils.getJsonFromBean(itemViewModel.get(pair.first).card.get()));
        Intent intent = new Intent();
        intent.setClass(mContext, CardDetailActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(mContext, intent, bundle);
    });

    /*-------------commands for filter---------------*/
    private List<Integer> evoFilter = new ArrayList<>();
    private List<Integer> rareFilter = new ArrayList<>();
    private List<Integer> rareFilter_temp = new ArrayList<>();
    private List<String> typeFilter = new ArrayList<>();
    private List<Integer> skillFilter = new ArrayList<>();
    private List<Integer> getTypeFilter = new ArrayList<>(); // 获取方式/卡池类型筛选
    private Integer sortMethod;
    private Integer sortType;

    public final ReplyCommand<List<String>> onRareSelCommand = new ReplyCommand<>(strings -> {
        rareFilter_temp.clear();
        for (String string : strings) {
            rareFilter_temp.add(SStaticR.rarityMap_rev.get(string));
        }
        solveRareFilter();
    });

    public final ReplyCommand<List<Integer>> onEvoSelCommand = new ReplyCommand<>(integers -> {
        evoFilter = integers;
        solveRareFilter();
    });

    private void solveRareFilter(){
        rareFilter.clear();
        if (evoFilter.contains(0)){
            rareFilter.addAll(rareFilter_temp);
        }
        if (evoFilter.contains(1)){
            for (Integer integer : rareFilter_temp) {
                rareFilter.add(integer + 1);
            }
        }
    }

    public final ReplyCommand<List<Integer>> onGetTypeSelCommand = new ReplyCommand<>(integers -> getTypeFilter = integers);

    public final ReplyCommand<List<String>> onTypeSelCommand = new ReplyCommand<>(strings -> typeFilter = strings);


    public final ReplyCommand<List<String>> sortTypeCommand = new ReplyCommand<>(strings -> sortType = SStaticR.sortTypeMap_Card.get(strings.get(0)));

    public final ReplyCommand<List<Integer>> sortMethodCommand = new ReplyCommand<>(integers -> sortMethod = integers.get(0));

    public final ReplyCommand<List<String>> skillTypeCommand = new ReplyCommand<>(strings -> {
        skillFilter.clear();
        for (String skillTypeName : strings) {
            skillFilter.add(SStaticR.skillTypeMap.get(skillTypeName));
        }
    });

    public final ReplyCommand filterCardsCommand = new ReplyCommand(() ->{
        filterCards();
        Messenger.getDefault().sendNoMsg(CardListFrag.TOKEN_CLOSE_FILTER);

    });

    public final ReplyCommand resetFilterCommand = new ReplyCommand(this::initFilter);
    /*-------------commands for filter---------------*/


    public CardListViewModel(BaseActivity context) {
        super(context);
        loadData();
        /*if (!umi.getSP(SharedHelper.KEY_AUTO_DATA)){
            loadData();
        }*/
        cardDataList.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                if (cardDataList.get().size() > 0){
                    initFilter();
                    filterCards();
                }else {
                    umi.dismissLoading();
                }
                /*if (!umi.getSP(SharedHelper.KEY_ANALYTICS_ASKED)){
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext)
                            .setTitleText(mContext.getString(R.string.analytics_ask_title))
                            .setContentText(mContext.getString(R.string.analytics_ask_content))
                            .setConfirmText(mContext.getString(R.string.accept))
                            .setCancelText(mContext.getString(R.string.decline))
                            .setConfirmClickListener(dialog -> {
                                dialog.dismiss();
                                Utils.turnOnGA(mContext);
                                umi.spSave(SharedHelper.KEY_ANALYTICS_ASKED, "true");
                                umi.spSave(SharedHelper.KEY_ANALYTICS_ON, "true");
                            })
                            .setCancelClickListener(dialog -> {
                                dialog.dismiss();
                                umi.spSave(SharedHelper.KEY_ANALYTICS_ASKED, "true");
                            });
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                    sweetAlertDialog.show();
                }*/
            }
        });
        Messenger.getDefault().register(mContext, MainActivity.TOKEN_DATA_UPDATED, this::loadData);
    }

    private void initFilter(){
        Messenger.getDefault().sendNoMsg(CardListFrag.TOKEN_RESET_FILTER);
        rareFilter_temp.clear();
        typeFilter.clear();
        skillFilter.clear();
        evoFilter.clear();
        SStaticR.skillTypeMap.put(mContext.getString(R.string.skill_empty), Integer.MAX_VALUE);
        rareFilter_temp.add(SStaticR.rarityMap_rev.get("SSR"));
        rareFilter_temp.add(SStaticR.rarityMap_rev.get("SR"));
        typeFilter.addAll(SStaticR.typeMap.values());
        skillFilter.addAll(SStaticR.skillTypeMap.values());
        getTypeFilter.addAll(getTypeMap_UI.keySet());
        sortMethod = 0; // default desc
        sortType = 0; // default ID
        evoFilter.add(0);
        evoFilter.add(1);
        solveRareFilter();
    }

    private void loadData(){
        itemViewModel.clear();
        umi.showLoading();
//        prepareGetType();
        if (getTypeMap.size() == 4){
            loadData(mContext, cardDataList, null);
        }else {
            prepareGetType();
        }
    }


    static void loadData(BaseActivity mContext, ObservableField<Map<Card, CardViewModel>> target, List<String> cardIdList){
        Observable.just(DBHelper.with(mContext)
                .where(DBHelper.TABLE_NAME_Card, "json", "id", cardIdList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Map::values)
                .subscribe(js -> Observable.create((Observable.OnSubscribe<Map<Card, CardViewModel>>) subscriber -> {
                    if (js.size() == 0){
                        subscriber.onNext(new HashMap<Card, CardViewModel>());
                        subscriber.onCompleted();
                    }
                    StringBuilder cardsJsonBuilder = new StringBuilder();
                    cardsJsonBuilder.append("[");
                    for (String j : js) {
                        cardsJsonBuilder.append(j);
                        cardsJsonBuilder.append(",");
                    }
                    cardsJsonBuilder.delete(cardsJsonBuilder.length() - 1, cardsJsonBuilder.length());
                    cardsJsonBuilder.append("]");
                    List<Card> cards = JsonUtils.getArrayFromJson(cardsJsonBuilder.toString(), new TypeToken<List<Card>>(){});
                    Map<Card, CardViewModel> map = new HashMap<>();
                    if (SStaticR.skillTypeMap.size() < 5 || SStaticR.isJp){
                        if (!SStaticR.isJp){
                            for (Card card : cards) {
                                if (card.getSkill() != null){
                                    if (SStaticR.skillTypeMap.containsValue(card.getSkill().getSkill_type_id())){
                                        continue;
                                    }
                                    if (card.getSkill().getSkill_type().contains("missing string")){
                                        SStaticR.skillTypeMap.put(mContext.getString(R.string.type_unknown), Integer.MAX_VALUE - 1);
                                    }else {
                                        SStaticR.skillTypeMap.put(card.getSkill().getSkill_type(), card.getSkill().getSkill_type_id());
                                    }
                                }
                            }
                        }
                        for (String s : SStaticR.skillTypeMap.keySet()) {
                            SStaticR.skillTypeNameMap.put(SStaticR.skillTypeMap.get(s), s);
                        }
                    }
                    for (Card card : cards) {
                        map.put(card, new CardViewModel(mContext, card));
                    }
                    subscriber.onNext(map);
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(target::set),
                        ExceptionHandler::handleException);

    }

    private boolean checkSkillType(Card card){
        if (card.getSkill() == null){
            return skillFilter.contains(Integer.MAX_VALUE);
        }
        if (!SStaticR.skillTypeMap.values().contains(card.getSkill().getSkill_type_id())){
            return skillFilter.contains(Integer.MAX_VALUE - 1);
        }
        return skillFilter.contains(card.getSkill().getSkill_type_id());
    }

    private boolean checkRare(Card card){
        if (rareFilter.contains(card.getRarity().getRarity())){
            return true;
        }
        /*for (Integer integer : evoFilter) {
            if (card.getEvolution_id() != 0){
                if (rareFilter.contains(card.getRarity().getRarity())){
                    return true;
                }
            }else {
                if (rareFilter.contains(card.getRarity().getRarity() - integer)){
                    return true;
                }
            }
        }*/
        return false;
    }

    private boolean checkID(Card card){
        for (Integer i : getTypeFilter) {
            if (getTypeMap.get(i).contains(card.getSeries_id())){
                return true;
            }
        }
        /*for (Integer integer : evoFilter) {
            if (card.getEvolution_id() != 0){
                for (Integer i : getTypeFilter) {
                    if (getTypeMap.get(i).contains(card.getId()) && integer == 0){
                        return true;
                    }
                }
            }else {
                for (Integer i : getTypeFilter) {
                    if (getTypeMap.get(i).contains(card.getId() - integer)){
                        return true;
                    }
                }
            }
        }*/
        return false;
    }


    private void filterCards(){
        Observable<List<Integer>> co = Observable.create(subscriber -> {
            List<Integer> cardsToShow = new ArrayList<>();
            for (Card card : cardDataList.get().keySet()) {
                if (typeFilter.contains(card.getAttribute().toUpperCase()) && checkRare(card) && checkSkillType(card) && checkID(card)) {
                    cardsToShow.add(card.getId());
                }
            }
            subscriber.onNext(cardsToShow);
            subscriber.onCompleted();
        });
        co.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cards -> {
                    for (Card card : cardDataList.get().keySet()) {
                        CardViewModel vm = cardDataList.get().get(card);
                        if (cards.contains(card.getId())) {
                            // 符合条件 如不在当前显示的列表中则加入
                            if (!itemViewModel.contains(vm)){
                                itemViewModel.add(vm);
                            }
                        }else {
                            // 不符合 如果存在则remove
                            if (itemViewModel.contains(vm)){
                                itemViewModel.remove(vm);
                            }
                        }
                    }
                    Collections.sort(itemViewModel, (a, b) -> {
                        Card cardA = a.card.get();
                        Card cardB = b.card.get();
                        boolean desc = sortMethod == 0;
                        int valueA;
                        int valueB;
                        switch (sortType){
                            case 1:
                                valueA = cardA.getVisual_max() + cardA.getBonus_visual();
                                valueB = cardB.getVisual_max() + cardB.getBonus_visual();
                                break;
                            case 2:
                                valueA = cardA.getVocal_max() + cardA.getBonus_vocal();
                                valueB = cardB.getVocal_max() + cardB.getBonus_vocal();
                                break;
                            case 3:
                                valueA = cardA.getDance_max() + cardA.getBonus_dance();
                                valueB = cardB.getDance_max() + cardB.getBonus_dance();
                                break;
                            case 4:
                                valueA = cardA.getOverall_max() + cardA.getOverall_bonus();
                                valueB = cardB.getOverall_max() + cardB.getOverall_bonus();
                                break;
                            default: // also for type ID
                                valueA = cardA.getSeries_id()  - (100000 * SStaticR.typeMap_int.get(cardA.getAttribute().toLowerCase()));
                                valueB = cardB.getSeries_id() - (100000 * SStaticR.typeMap_int.get(cardB.getAttribute().toLowerCase()));
                                break;
                        }
                        return (desc ? 1 : -1) * (valueB == valueA ? 0 : valueB < valueA ? -1 : 1);
                    });
                    umi.dismissLoading();
                });


        // 排序

    }

    public static final Map<Integer, List<Integer>> getTypeMap = new HashMap<>(); // 0-常驻，1-限定，2-FES，3-活动
    public static final Map<Integer, String> getTypeMap_sql = new HashMap<>();
    public static final Map<Integer, Integer> getTypeMap_UI = new LinkedHashMap<>();
    static {
        getTypeMap_sql.put(0, "SELECT * from (SELECT DISTINCT reward_id as id from gacha_available where limited_flag = 0 UNION SELECT id from card_data where rarity < 2) as a");
        getTypeMap_sql.put(1, "SELECT DISTINCT b.reward_id as id from gacha_data a, gacha_available b where a.id = b.gacha_id and a.dicription like '%期間限定%' and b.limited_flag = 1 ");
        getTypeMap_sql.put(2, "SELECT DISTINCT b.reward_id as id from gacha_data a, gacha_available b where a.id = b.gacha_id and a.dicription like '%フェス%' and b.limited_flag = 1 ");
        getTypeMap_sql.put(3, "SELECT id from card_data where name not like '%＋%' and rarity > 2 and id not in (SELECT DISTINCT reward_id from gacha_available)");
        getTypeMap_UI.put(0, R.string.get_type_normal);
        getTypeMap_UI.put(1, R.string.get_type_limit);
        getTypeMap_UI.put(2, R.string.get_type_fes);
        getTypeMap_UI.put(3, R.string.get_type_event);
    }

    // 读取各卡池类型包含的card_id
    private void prepareGetType(){
        DBHelper.refresh(mContext, DBHelper.DB_NAME_master);
        for (Integer integer : getTypeMap_sql.keySet()) {
            try {
                Observable<List<Integer>> observable = Observable.just(DBHelper.with(mContext, DBHelper.DB_NAME_master)
                        .getBeanListByRaw(getTypeMap_sql.get(integer), Integer.class, "id"));
                observable.subscribeOn(Schedulers.io())
                        .subscribe(ids -> {
                            if (ids != null && ids.size() > 0){
                                getTypeMap.put(integer, ids);
                            }
                            if (getTypeMap.size() == 4){
                                loadData(mContext, cardDataList, null);
                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
