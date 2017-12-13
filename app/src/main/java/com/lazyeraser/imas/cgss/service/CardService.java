package com.lazyeraser.imas.cgss.service;

import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.entity.CardIndex;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/9/11.
 *
 */

public interface CardService {

    @GET("list/card_t?keys=id,evolution_id")
    Observable<List<CardIndex>> getCardIdList();

    @GET("card_t/{ids}")
    Observable<List<Card>> getCardList(@Path("ids") String ids);

    @GET("https://storages.game.starlight-stage.jp/dl/10032950/manifests/iOS_AHigh_SHigh")
    @Headers("X-Unity-Version:5.4.5p1")
    Observable<String> getCards();

    @GET("https://storages.game.starlight-stage.jp/dl/resources/Generic//44662c9a897c43a8bd0ea0488e806ad3")
    @Headers("X-Unity-Version:5.4.5p1")
    Observable<String> getMaster();
}
