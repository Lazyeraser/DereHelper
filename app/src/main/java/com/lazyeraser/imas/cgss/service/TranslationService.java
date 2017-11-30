package com.lazyeraser.imas.cgss.service;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/9/19.
 */

public interface TranslationService {

    @POST("/api/v1/read_tl")
    Observable<Map<String, String>> getTranslations(@Body List<String> body);

}
