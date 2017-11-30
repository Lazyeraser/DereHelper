package com.lazyeraser.imas.cgss.service;

import com.lazyeraser.imas.cgss.entity.Chara;
import com.lazyeraser.imas.cgss.entity.CharaIndex;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by lazyeraser on 2017/9/19.
 */

public interface CharaService {

    @GET("/api/v1/list/char_t")
    Observable<List<CharaIndex>> getCharaIndex();

    @GET("/api/v1/char_t/{ids}")
    Observable<List<Chara>> getCharaList(@Path("ids") String ids);

}
