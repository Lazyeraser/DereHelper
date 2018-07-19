package com.lazyeraser.imas.cgss.service;

import com.google.gson.JsonObject;
import com.lazyeraser.imas.cgss.entity.Info;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/12/12.
 */

public interface InfoService {

    @GET("info")
    Observable<Info> getGameInfo();

    @POST("http://crash.lazyeraser.ml/lazyeraser/info/android/crash")
    Observable<Object> postCrashInfo(@Query("code") String code, @Body String info);

    @GET
    Observable<JsonObject> getJson(@Url String url);

}
