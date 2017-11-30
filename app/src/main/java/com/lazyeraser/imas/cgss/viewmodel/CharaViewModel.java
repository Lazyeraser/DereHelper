package com.lazyeraser.imas.cgss.viewmodel;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.entity.Chara;
import com.lazyeraser.imas.cgss.entity.CharaIndex;
import com.lazyeraser.imas.cgss.utils.ConstellationHelper;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.view.CardDetailActivity;
import com.lazyeraser.imas.cgss.view.CharaDetailActivity;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;

import java.util.ArrayList;
import java.util.Collection;
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

public class CharaViewModel extends BaseViewModel{

    public final ObservableField<Chara> chara = new ObservableField<>();
    public final ObservableField<String> charaIconUrl = new ObservableField<>();
    public final ObservableField<String> bloodType = new ObservableField<>();
    public final ObservableField<String> hand = new ObservableField<>();
    public final ObservableField<String> threeSize = new ObservableField<>();
    public final ObservableField<String> constellation = new ObservableField<>(); // 星座
    public final ObservableBoolean cardsVisible = new ObservableBoolean(false);

    public final ObservableList<CardViewModel> itemViewModel = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(com.lazyeraser.imas.derehelper.BR.viewModel, R.layout.item_single_card);

    private final ObservableField<Map<Card, CardViewModel>> cardDataList = new ObservableField<>();

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

    private final static SparseArray<String> bloodTypeMap = new SparseArray<>();
    static {
        bloodTypeMap.put(2001, "A");
        bloodTypeMap.put(2002, "B");
        bloodTypeMap.put(2003, "AB");
        bloodTypeMap.put(2004, "O");
    }

    public CharaViewModel(BaseActivity mContext, Chara chara) {
        super(mContext);
        this.chara.set(chara);
        charaIconUrl.set(SStaticR.SERVER_URL_RES + "/icon_char/" + chara.getChara_id() + ".png");
        if (mContext instanceof CharaDetailActivity){
            bloodType.set(bloodTypeMap.get(chara.getBlood_type()));
            hand.set(mContext.getString(chara.getHand() == 3001 ? R.string.right : R.string.left));
            threeSize.set(chara.getBody_size_1() + "/" + chara.getBody_size_2() + "/" + chara.getBody_size_3());
            constellation.set(mContext.getString(ConstellationHelper.getConstellation(chara.getBirth_month(), chara.getBirth_day())));
            if (!umi.getIntentString("fromCard").equals("true")){
                cardsVisible.set(true);
                Observable.just(DBHelper.with(mContext)
                        .queryTable(DBHelper.TABLE_NAME_Chara_Index)
                        .column("json")
                        .where("id", Collections.singletonList(String.valueOf(chara.getChara_id()))))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(map ->{
                            if (map.values().size() > 0){
                                String json = map.get(String.valueOf(chara.getChara_id()));
                                CharaIndex charaIndex = JsonUtils.getBeanFromJson(json, CharaIndex.class);
                                List<String> cards = new ArrayList<>();
                                for (Integer integer : charaIndex.getCards()) {
                                    cards.add(String.valueOf(integer));
                                }
                                CardListViewModel.loadData(mContext, cardDataList, cards);
                            }
                        });
                cardDataList.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                        itemViewModel.addAll(cardDataList.get().values());
                        Collections.sort(itemViewModel, (a, b) -> {
                            Card cardA = a.card.get();
                            Card cardB = b.card.get();
                            if (cardB.getId() < cardA.getId()){
                                return 1;
                            }else {
                                return -1;
                            }
                        });
                    }
                });
            }
        }
    }

}
