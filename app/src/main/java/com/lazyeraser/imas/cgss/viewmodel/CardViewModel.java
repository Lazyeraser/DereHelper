package com.lazyeraser.imas.cgss.viewmodel;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.service.TranslationService;
import com.lazyeraser.imas.cgss.utils.DBHelper;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.view.CardDetailActivity;
import com.lazyeraser.imas.cgss.view.CharaDetailActivity;
import com.lazyeraser.imas.cgss.view.FullScreenImageActivity;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;
import com.lazyeraser.imas.main.SStaticR;
import com.lazyeraser.imas.retrofit.ExceptionHandler;
import com.lazyeraser.imas.retrofit.RetrofitProvider;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/9/12.
 */

public class CardViewModel extends BaseViewModel {

    public final ObservableField<Card> card = new ObservableField<>();
//    public final ObservableField<String> cardIconUrl = new ObservableField<>();
//    public final ObservableField<String> cardSpreadUrl = new ObservableField<>();
    public final ObservableField<String> cardTitle = new ObservableField<>();
    public final ObservableField<String> cardRare = new ObservableField<>();
    public final ObservableField<String> skill = new ObservableField<>();
    public final ObservableField<String> skillChance = new ObservableField<>();
    public final ObservableField<String> skillLength = new ObservableField<>();
//    public final ObservableField<String> charaIconUrl = new ObservableField<>();



    private Map<String, String> translationMap;
    private List<String> stringsToTranslate;
    private Card originCard;

    public final ReplyCommand onBigPicClick = new ReplyCommand(() -> {
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, mContext.findViewById(R.id.big_pic), "big_pic");
        Bundle bundle = transitionActivityOptions.toBundle();
        if (bundle == null){
            bundle = new Bundle();
        }
        bundle.putString("picUrl", card.get().getSpread_image_ref());
        Intent intent = new Intent();
        intent.setClass(mContext, FullScreenImageActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(mContext, intent, bundle);
    });

    public final ReplyCommand onIdolClick = new ReplyCommand(() -> {
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, mContext.getBView(R.id.chara_icon), "chara_icon");
        Bundle bundle = transitionActivityOptions.toBundle();
        if (bundle == null){
            bundle = new Bundle();
        }
        bundle.putString("theChara", JsonUtils.getJsonFromBean(card.get().getChara()));
        bundle.putString("fromCard", "true");
        Intent intent = new Intent();
        intent.setClass(mContext, CharaDetailActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(mContext, intent, bundle);
    });

    public final ObservableBoolean tran = new ObservableBoolean(umi.getSP(SharedHelper.KEY_DEFAULT_TRAN) && !SStaticR.isJp); // 是否翻译

    public final ReplyCommand<Boolean> onTranSwitchCheck = new ReplyCommand<>(this::onTranSwitchChanged);

    private void onTranSwitchChanged(boolean check){
        /*if (!(mContext instanceof CardDetailActivity)){
            return;
        }*/
        tran.set(check);
        if (check){
            if (stringsToTranslate.size() > 0){
//                umi.showLoading();
                Observable<Map<String, String>> translation = RetrofitProvider.getInstance()
                        .create(TranslationService.class)
                        .getTranslations(stringsToTranslate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle());
                translation.subscribe(stringStringMap -> {
                    translationMap.putAll(stringStringMap);
//                    stringsToTranslate.clear();
                    translateTheCard();
                    umi.dismissLoading();
                    Observable.create(subscriber -> {
                        for (String key : stringStringMap.keySet()) {
                            stringsToTranslate.remove(key);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("origin", key);
                            contentValues.put("translate", stringStringMap.get(key));
                            DBHelper.with(mContext).insertData(DBHelper.TABLE_NAME_Translation, contentValues);
                        }
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }).subscribeOn(Schedulers.io())
                            .compose(((ActivityLifecycleProvider) mContext).bindToLifecycle())
                            .subscribe(b -> {
                                // do nothing
                            });
                }, ExceptionHandler::handleException);
            }else {
                translateTheCard();
            }
        }else {
            card.set(originCard);
            cardTitle.set("[" + Utils.emptyLessString(mContext, card.get().getTitle()) + "]");
        }
    }

    private void translateTheCard(){
        Card theCard = JsonUtils.getBeanFromJson(JsonUtils.getJsonFromBean(card.get()), Card.class);
        if (!TextUtils.isEmpty(theCard.getChara().getTranslated())){
            theCard.setName_only(theCard.getChara().getTranslated());
        }
        if (translationMap.containsKey(theCard.getTitle())) {
            theCard.setTitle(translationMap.get(theCard.getTitle()));
        }
        if (translationMap.containsKey(theCard.getSkill().getSkill_name())) {
            theCard.getSkill().setSkill_name(translationMap.get(theCard.getSkill().getSkill_name()));
        }
        if (translationMap.containsKey(theCard.getLead_skill().getName())) {
            theCard.getLead_skill().setName(translationMap.get(theCard.getLead_skill().getName()));
        }
        card.set(theCard);
        cardTitle.set("[" + Utils.emptyLessString(mContext, card.get().getTitle()) + "]");
    }



    public CardViewModel(BaseActivity context, Card theCard) {
        super(context);
        theCard.setName_only(theCard.getName_only().replace("＋", ""));
        init(theCard, context);
        /*try {
            Observable.just(DBHelper.with(mContext, DBHelper.DB_NAME_master).getBean("card_data", Card.class, "id", String.valueOf(theCard.getId())))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                        Card card = (Card)data;
                        if (theCard.getSkill() != null)
                            card.setSkill(theCard.getSkill());
                        card.setChara(theCard.getChara());
                        card.setRarity(theCard.getRarity());
                        if (theCard.getLead_skill() != null)
                        card.setLead_skill(theCard.getLead_skill());
                        init(card, context);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private void init(Card theCard, Context context){
        card.set(theCard);
        originCard = theCard;
//        theCard.getLead_skill().getName()
//        cardIconUrl.set(SStaticR.SERVER_URL_RES + "/icon_card/" + this.card.get().getId() + ".png");
//        cardSpreadUrl.set(SStaticR.SERVER_URL_RES + "/spread/" + this.card.get().getId() + ".png");
        cardTitle.set("[" + Utils.emptyLessString(context, card.get().getTitle()) + "]");
        cardRare.set(SStaticR.rarityMap.get(card.get().getRarity().getRarity()));

        if (context instanceof CardDetailActivity){
//            charaIconUrl.set(SStaticR.SERVER_URL_RES + "/icon_char/" + this.card.get().getChara_id() + ".png");

            List<String> stringsNeedTranslation = new ArrayList<>();
            if (theCard.getSkill() != null){
                String skillCB = String.valueOf(theCard.getSkill().getProc_chance().get(0) / 100) +
                        "% ~ " +
                        theCard.getSkill().getProc_chance().get(1) / 100 +
                        "%";
                skillChance.set(skillCB);
                String skillLB = String.valueOf(theCard.getSkill().getEffect_length().get(0) / 100) +
                        "s ~ " +
                        theCard.getSkill().getEffect_length().get(1) / 100 +
                        "s";
                skillLength.set(skillLB);
                if (!TextUtils.isEmpty(theCard.getSkill().getSkill_name())){
                    stringsNeedTranslation.add(theCard.getSkill().getSkill_name());
                }
            }
            if (theCard.getLead_skill() != null){
                if (!TextUtils.isEmpty(theCard.getLead_skill().getName())){
                    stringsNeedTranslation.add(theCard.getLead_skill().getName());
                }
            }

            if (!TextUtils.isEmpty(theCard.getTitle())){
                stringsNeedTranslation.add(theCard.getTitle());
            }


            stringsToTranslate = new ArrayList<>();
            Observable<Map<String, String>> translationData = Observable.create(subscriber -> {
                subscriber.onNext(DBHelper.with(mContext)
                        .where(DBHelper.TABLE_NAME_Translation, "translate", "origin", stringsNeedTranslation));
                subscriber.onCompleted();
            });
            translationData.subscribe(map -> {
                if (map != null && map.size() > 0){
                    translationMap = map;
                    for (String s : stringsNeedTranslation) {
                        if (!translationMap.keySet().contains(s)){
                            stringsToTranslate.add(s);
                        }
                    }
                }else {
                    translationMap = new HashMap<>();
                    stringsToTranslate.addAll(stringsNeedTranslation);
                }
            });
        }else {
            if (theCard.getSkill() == null){
                skill.set(mContext.getString(R.string.skill_empty));
            }else {
                // 组装技能信息
                StringBuilder skillB = new StringBuilder();
                skillB.append(theCard.getSkill().getCondition());
                skillB.append("s/");
                switch (theCard.getSkill().getProc_chance().get(0)){
                    case 3000:
                        skillB.append(mContext.getString(R.string.low));
                        break;
                    case 3500:
                        skillB.append(mContext.getString(R.string.middle));
                        break;
                    case 4000:
                        skillB.append(mContext.getString(R.string.high));
                        break;
                    default:
                        skillB.append(mContext.getString(R.string.other));
                        break;
                }
                skillB.append("/");
                if (theCard.getSkill().getValue() > 100){
                    skillB.append(theCard.getSkill().getValue() - 100);
                    if (theCard.getSkill().getValue_2() <= 100){
                        skillB.append("%/");
                    }else {
                        skillB.append("%|");
                        skillB.append(theCard.getSkill().getValue_2() - 100);
                        skillB.append("%/");
                    }
                }
                String skillType = SStaticR.skillTypeNameMap.containsKey(theCard.getSkill().getSkill_type_id()) ?
                        SStaticR.skillTypeNameMap.get(theCard.getSkill().getSkill_type_id()) : mContext.getString(R.string.type_unknown);
                skillB.append(skillType);
                skill.set(skillB.toString());
            }
        }
    }



}
