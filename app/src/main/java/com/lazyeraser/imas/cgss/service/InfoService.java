package com.lazyeraser.imas.cgss.service;

import com.lazyeraser.imas.cgss.entity.Info;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/12/12.
 */

public interface InfoService {

    @GET("info")
    Observable<Info> getGameInfo();

}
