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

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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
    private boolean isEvo = false;

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
    private List<Integer> rareFilter = new ArrayList<>();
    private List<String> typeFilter = new ArrayList<>();
    private List<Integer> skillFilter = new ArrayList<>();
    private List<Integer> getTypeFilter = new ArrayList<>(); // 获取方式/卡池类型筛选
    private Integer sortMethod;
    private Integer sortType;

    public final ReplyCommand<List<String>> onRareSelCommand = new ReplyCommand<>(strings -> {
        rareFilter.clear();
        for (String string : strings) {
            rareFilter.add(SStaticR.rarityMap_rev.get(string));
        }
    });

    public final ReplyCommand<List<Integer>> onGetTypeSelCommand = new ReplyCommand<>(integers -> getTypeFilter = integers);

    public final ReplyCommand<List<String>> onTypeSelCommand = new ReplyCommand<>(strings -> typeFilter = strings);

    public final ReplyCommand<List<Integer>> onEvoSelCommand = new ReplyCommand<>(integers -> isEvo = integers.get(0) == 1);

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

        if (!umi.getSP(SharedHelper.KEY_AUTO_DATA)){
            loadData();
        }
        cardDataList.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                if (cardDataList.get().size() > 0){
                    initFilter();
                    filterCards();
                }else {
                    umi.dismissLoading();
                }
            }
        });
        Messenger.getDefault().register(mContext, MainActivity.TOKEN_DATA_UPDATED, this::loadData);
    }

    private void initFilter(){
        rareFilter.clear();
        typeFilter.clear();
        skillFilter.clear();
        SStaticR.skillTypeMap.put(mContext.getString(R.string.skill_empty), Integer.MAX_VALUE);
        rareFilter.add(SStaticR.rarityMap_rev.get("SSR"));
        rareFilter.add(SStaticR.rarityMap_rev.get("SR"));
        typeFilter.addAll(SStaticR.typeMap.values());
        skillFilter.addAll(SStaticR.skillTypeMap.values());
        getTypeFilter.addAll(getTypeMap_UI.keySet());
        sortMethod = 0; // default desc
        sortType = 0; // default ID
        Messenger.getDefault().sendNoMsg(CardListFrag.TOKEN_RESET_FILTER);
    }

    private void loadData(){
        itemViewModel.clear();
        umi.showLoading();
        prepareGetType();
        loadData(mContext, cardDataList, null);
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
                    if (SStaticR.skillTypeMap.size() == 0 || SStaticR.isJp){
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



    private void filterCards(){
        List<Integer> idList = new ArrayList<>();
        for (Integer integer : getTypeFilter) {
            idList.addAll(getTypeMap.get(integer));
        }
        // 过滤
        for (Card card : cardDataList.get().keySet()) {
            CardViewModel vm = cardDataList.get().get(card);
            if (typeFilter.contains(card.getAttribute().toUpperCase()) && rareFilter.contains(card.getRarity().getRarity() - (isEvo ? 1 : 0))
                    && checkSkillType(card) && idList.contains(card.getId() - (isEvo ? 1 : 0))) {
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

        // 排序
        Collections.sort(itemViewModel, (a, b) -> {
            Card cardA = a.card.get();
            Card cardB = b.card.get();
            boolean desc = sortMethod == 0;
            switch (sortType){
                case 1:
                    if ((cardB.getVisual_max() + cardB.getBonus_visual()) < (cardA.getVisual_max() + cardA.getBonus_visual())){
                        return desc ? -1 : 1;
                    }else {
                        return desc ? 1 : -1;
                    }
                case 2:
                    if ((cardB.getVocal_max() + cardB.getBonus_vocal()) < (cardA.getVocal_max() + cardA.getBonus_vocal())){
                        return desc ? -1 : 1;
                    }else {
                        return desc ? 1 : -1;
                    }
                case 3:
                    if ((cardB.getDance_max() + cardB.getBonus_dance()) < (cardA.getDance_max() + cardA.getBonus_dance())){
                        return desc ? -1 : 1;
                    }else {
                        return desc ? 1 : -1;
                    }
                case 4:
                    if ((cardB.getOverall_max() + cardB.getOverall_bonus()) < (cardA.getOverall_max() + cardA.getOverall_bonus())){
                        return desc ? -1 : 1;
                    }else {
                        return desc ? 1 : -1;
                    }
                default: // also for type ID
                    if ((cardB.getId() - (100000 * SStaticR.typeMap_int.get(cardB.getAttribute().toLowerCase()))) < (cardA.getId()  - (100000 * SStaticR.typeMap_int.get(cardA.getAttribute().toLowerCase())))){
                        return desc ? -1 : 1;
                    }else {
                        return desc ? 1 : -1;
                    }
            }
        });
        umi.dismissLoading();
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
        for (Integer integer : getTypeMap_sql.keySet()) {
            try {
                Observable<List<Integer>> observable = Observable.just(DBHelper.with(mContext, DBHelper.DB_NAME_master)
                        .getBeanListByRaw(getTypeMap_sql.get(integer), Integer.class, "id"));
                observable.subscribeOn(Schedulers.io())
                        .subscribe(ids -> getTypeMap.put(integer, ids));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
