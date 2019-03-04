package com.lazyeraser.imas.cgss.service;

import com.lazyeraser.imas.main.SStaticR;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/12/25.
 *
 */

public interface CGSSService {

    @GET("https://asset-starlight-stage.akamaized.net/dl/{version}/manifests/Android_AHigh_SHigh")
//    @Headers("X-Unity-Version:" + SStaticR.UNITY_VERSION)
    Observable<ResponseBody> getManifests(@Path("version") String version, @Header("X-Unity-Version") String unityVer);

    @GET("https://asset-starlight-stage.akamaized.net/dl/resources/Generic/{head}/{hash}")
//    @Headers("X-Unity-Version:" + SStaticR.UNITY_VERSION)
    Observable<ResponseBody> getResources(@Path("head") String head, @Path("hash") String hash, @Header("X-Unity-Version") String unityVer);

    @GET("http://346lab.org/dl/cgss_assets/{version}/manifests/Android_AHigh_SHigh")
    Observable<ResponseBody> getManifestsRP(@Path("version") String version);

    @GET("http://346lab.org/dl/cgss_assets/resources/Generic/{head}/{hash}")
    Observable<ResponseBody> getResourcesRP(@Path("head") String head, @Path("hash") String hash);
}
