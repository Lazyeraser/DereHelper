package com.lazyeraser.imas.cgss.service;

import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.entity.CardIndex;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/9/11.
 *
 */

public interface CardService {

    @GET("/api/v1/list/card_t?keys=id,chara_id,title")
    Observable<List<CardIndex>> getCardIdList();

    @GET("/api/v1/card_t/{ids}")
    Observable<List<Card>> getCardList(@Path("ids") String ids);


}
