package com.lazyeraser.imas.retrofit;

import com.lazyeraser.imas.cgss.utils.LZ4Helper;
import com.lazyeraser.imas.cgss.utils.Utils;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Converter;


public final class ToStringConverter implements Converter<ResponseBody, String> {
    @Override
    public String convert(ResponseBody value) throws IOException {
        Utils.mPrint("try:" + String.valueOf(LZ4Helper.uncompressCGSSDB(value.bytes())));
        return value.string();
    }
}