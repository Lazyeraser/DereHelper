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
import com.lazyeraser.imas.cgss.entity.TextData;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lazyeraser.imas.main.SStaticR.textDataList;

/**
 * Created by lazyeraser on 2017/9/19.
 */

public class CharaViewModel extends BaseViewModel {

    public final ObservableField<Chara> chara = new ObservableField<>();
//    public final ObservableField<String> charaIconUrl = new ObservableField<>();
    public final ObservableField<String> bloodType = new ObservableField<>();
    public final ObservableField<String> hand = new ObservableField<>();
    public final ObservableField<String> threeSize = new ObservableField<>();
    public final ObservableField<String> constellation = new ObservableField<>(); // 星座
    public final ObservableField<String> age = new ObservableField<>();
    public final ObservableField<String> hometown = new ObservableField<>();
    public final ObservableBoolean cardsVisible = new ObservableBoolean(false);

    /*cards of the chara*/
    public final ObservableList<CardViewModel> itemViewModel = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(com.lazyeraser.imas.derehelper.BR.viewModel, R.layout.item_single_card);

    private final ObservableField<Map<Card, CardViewModel>> cardDataList = new ObservableField<>();

    // click event
    public final ReplyCommand<Pair<Integer, View>> onListItemClickCommand = new ReplyCommand<>(pair -> {
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, pair.second.findViewById(R.id.card_icon), "card_icon");
        Bundle bundle = transitionActivityOptions.toBundle();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("theCard", JsonUtils.getJsonFromBean(itemViewModel.get(pair.first).card.get()));
        Intent intent = new Intent();
        intent.setClass(mContext, CardDetailActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(mContext, intent, bundle);
    });
    /*cards of the chara*/

    private final static HashMap<String, Integer> handTypeMap = new HashMap<>();

    static {
        handTypeMap.put("右", R.string.right);
        handTypeMap.put("左", R.string.left);
        handTypeMap.put("両", R.string.hand_both);
    }


    public String getRealText(int id, int category) {
        if (id > 1000 || category == 2){
            for (TextData textData : textDataList) {
                if (textData.category == category){
                    if ((id % 1000) == textData.index){
                        return textData.text;
                    }
                }
            }
            return String.valueOf(id);
        }else {
            return String.valueOf(id);
        }
    }

    private void setData(Chara chara) {
        bloodType.set(getRealText(chara.getBlood_type(), 3));
        hand.set(mContext.getString(handTypeMap.get(getRealText(chara.getHand(), 5))));
        threeSize.set(getRealText(chara.getBody_size_1(), 6) + "/" + getRealText(chara.getBody_size_2(), 6) + "/" + getRealText(chara.getBody_size_3(), 6));
        constellation.set(getRealText(chara.getConstellation(), 4)); // TODO translation from JP to CN & EN
        age.set(getRealText(chara.getAge(), 6) + mContext.getString(R.string.unit_age));
        hometown.set(getRealText(chara.getHome_town(), 2));
    }

    public CharaViewModel(BaseActivity mContext, String charaId) {
        super(mContext);
        Observable.just(DBHelper.with(mContext)
                .where(DBHelper.TABLE_NAME_Chara_Detail, "json", "id", Collections.singletonList(charaId)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> {
                    Chara chara = JsonUtils.getBeanFromJson(map.get(charaId), Chara.class);
                    init(mContext, chara);
                });
    }
    public CharaViewModel(BaseActivity mContext, Chara chara) {
        super(mContext);
        init(mContext, chara);
    }

    private void init(BaseActivity mContext, Chara chara){
        this.chara.set(chara);
//        charaIconUrl.set(String.format(SStaticR.charaIconUrl, chara.getChara_id()));
        if (mContext instanceof CharaDetailActivity) {
            if (textDataList == null || textDataList.size() == 0) {
                Observable.create(su -> {
                    try {
                        textDataList = DBHelper.with(mContext, DBHelper.DB_NAME_master).getBeanList("text_data", TextData.class);
                        su.onCompleted();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(() -> setData(chara))
                        .subscribe();
            }else {
                setData(chara);
            }
            // show chara's cards
//            if (!umi.getIntentString("fromCard").equals("true")) {
                cardsVisible.set(true);
                Observable.just(DBHelper.with(mContext)
                        .where(DBHelper.TABLE_NAME_Chara_Index, "json", "id", Collections.singletonList(String.valueOf(chara.getChara_id()))))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(map -> {
                            if (map.values().size() > 0) {
                                String json = map.get(String.valueOf(chara.getChara_id()));
                                CharaIndex charaIndex = JsonUtils.getBeanFromJson(json, CharaIndex.class);
                                List<String> cards = new ArrayList<>();
                                for (Integer integer : charaIndex.getCards()) {
                                    cards.add(String.valueOf(integer));
                                }
                                CardListViewModel.loadData(mContext, cardDataList, cards); // search cards by card_ids
                            }
                        });
                cardDataList.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                        itemViewModel.addAll(cardDataList.get().values());
                        Collections.sort(itemViewModel, (a, b) -> {
                            Card cardA = a.card.get();
                            Card cardB = b.card.get();
                            if (cardB.getId() < cardA.getId()) {
                                return 1;
                            } else {
                                return -1;
                            }
                        });
                    }
                });
//            }
        }
    }

}
