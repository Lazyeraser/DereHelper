package com.lazyeraser.imas.cgss.service;

import com.lazyeraser.imas.main.SStaticR;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/12/25.
 *
 */

public interface CGSSService {

    @GET("https://storages.game.starlight-stage.jp/dl/{version}/manifests/Android_AHigh_SHigh")
    @Headers("X-Unity-Version:" + SStaticR.UNITY_VERSION)
    Observable<ResponseBody> getManifests(@Path("version") String version);

    @GET("https://storages.game.starlight-stage.jp/dl/resources/Generic/{hash}")
    @Headers("X-Unity-Version:" + SStaticR.UNITY_VERSION)
    Observable<ResponseBody> getResources(@Path("hash") String hash);

    @GET("http://346lab.org/dl/cgss_assets/resources/Generic/{hash}")
//    @Headers("X-Unity-Version:" + SStaticR.UNITY_VERSION)
    Observable<ResponseBody> getResourcesRP(@Path("hash") String hash);
}
