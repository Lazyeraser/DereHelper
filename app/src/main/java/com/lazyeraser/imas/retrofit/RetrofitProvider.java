package com.lazyeraser.imas.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lazyeraser.imas.main.SStaticR;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitProvider {

    private static OkHttpClient okHttpClient;
    private static Gson gson;
    private static Retrofit retrofit_json;
    private static Retrofit retrofit_normal;
    private static String baseUrl = SStaticR.API;

    private RetrofitProvider() {
    }

    public static Retrofit getInstance(){
        return getInstance(baseUrl, true);
    }

    public static Retrofit getInstance(boolean json){
        return getInstance(baseUrl, json);
    }

    public static Retrofit getInstance(String baseUrl, boolean json) {
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .build();
        }
        if (gson == null){
            gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ApiTypeAdapterFactory("result"))
                    .create();
        }
        if (!json){
            if (retrofit_normal == null) {
                retrofit_normal = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        /*.addConverterFactory(new Converter.Factory() {
                            @Override
                            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                                return new ToStringConverter();
                            }
                        })*/
                        .client(okHttpClient)
                        .build();
            }
        }else {
            if (retrofit_json == null) {
                retrofit_json = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(okHttpClient)
                        .build();
            }
        }

        return json ? retrofit_json : retrofit_normal;

    }
}
