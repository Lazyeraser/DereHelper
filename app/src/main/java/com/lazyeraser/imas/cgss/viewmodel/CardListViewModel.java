package com.lazyeraser.imas.cgss.viewmodel;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.Handler;
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

    // go detail activity
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
    private Integer sortMethod;
    private Integer sortType;

    public final ReplyCommand<List<String>> onRareSelCommand = new ReplyCommand<>(strings -> {
        rareFilter.clear();
        for (String string : strings) {
            rareFilter.add(SStaticR.rarityMap_rev.get(string));
        }
    });

    public final ReplyCommand<List<String>> onTypeSelCommand = new ReplyCommand<>(strings -> typeFilter = strings);

    public final ReplyCommand<List<Integer>> onEvoSelCommand = new ReplyCommand<>(integers -> isEvo = integers.get(0) == 1);

    public final ReplyCommand<List<String>> sortTypeCommand = new ReplyCommand<>(strings -> sortType = SStaticR.sortTypeMap.get(strings.get(0)));

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
        cardDataList.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                if (cardDataList.get().size() > 0){
                    initFilter();
                    filterCards();
                }
            }
        });
        Messenger.getDefault().register(mContext, MainActivity.TOKEN_DATA_UPDATED, this::loadData);
    }

    private void initFilter(){
        rareFilter.clear();
        typeFilter.clear();
        skillFilter.clear();
        SStaticR.skillTypeMap.put(mContext.getString(R.string.empty_skill), Integer.MAX_VALUE);
        rareFilter.add(SStaticR.rarityMap_rev.get("SSR"));
        rareFilter.add(SStaticR.rarityMap_rev.get("SR"));
        typeFilter.addAll(SStaticR.typeMap.values());
        skillFilter.addAll(SStaticR.skillTypeMap.values());
        sortMethod = 0; // default desc
        sortType = 0; // default ID
        Messenger.getDefault().sendNoMsg(CardListFrag.TOKEN_RESET_FILTER);
    }

    private void loadData(){
        itemViewModel.clear();
        umi.showLoading();
        loadData(mContext, cardDataList, null);
    }


    public static void loadData(BaseActivity mContext, ObservableField<Map<Card, CardViewModel>> target, List<String> cardIdList){
        Observable.just(DBHelper.with(mContext)
                .queryTable(DBHelper.TABLE_NAME_Card)
                .column("json")
                .where("id", cardIdList))
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
                    boolean fillSkill = SStaticR.skillTypeMap.size() == 0;
                    for (Card card : cards) {
                        map.put(card, new CardViewModel(mContext, card));
                        if (fillSkill && card.getSkill() != null){
                            SStaticR.skillTypeMap.put(card.getSkill().getSkill_type(), card.getSkill().getSkill_type_id());
                        }
                    }
                    subscriber.onNext(map);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(target::set), ExceptionHandler::handleException);

    }

    private boolean checkSkillType(Card card){
        if (card.getSkill() == null){
            return skillFilter.contains(Integer.MAX_VALUE);
        }
        return skillFilter.contains(card.getSkill().getSkill_type_id());
    }

    private void filterCards(){
        // 过滤
        for (Card card : cardDataList.get().keySet()) {
            CardViewModel vm = cardDataList.get().get(card);
            if (typeFilter.contains(card.getAttribute().toUpperCase()) && rareFilter.contains(card.getRarity().getRarity() - (isEvo ? 1 : 0))
                    && checkSkillType(card)) {
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

}
