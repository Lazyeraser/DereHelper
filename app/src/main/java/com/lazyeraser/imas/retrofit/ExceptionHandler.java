package com.lazyeraser.imas.retrofit;

import android.net.ParseException;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.Base;
import com.lazyeraser.imas.main.BaseActivity;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.adapter.rxjava.HttpException;


public class ExceptionHandler {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static void handleException(Throwable e) {
        ResponseThrowable ex;
        @StringRes int msg = R.string.network_error_0;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.message = "网络错误";
                    msg = R.string.network_error_0;
                    break;
            }
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponseThrowable(resultException, resultException.code);
            ex.message = resultException.message;

        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            msg = R.string.network_error_2;

        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, ERROR.NETWORK_ERROR);
            ex.message = "连接失败";
            msg = R.string.network_error_1;

        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            msg = R.string.network_error_3;

        } else if (e instanceof ConnectTimeoutException){
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            msg = R.string.network_error_4;

        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            msg = R.string.network_error_4;

        }
        else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.message = "未知错误";
            msg = R.string.network_error_5;

        }
        if (msg != R.string.network_error_5){
            Toast.makeText(Base.getTopContext(), msg, Toast.LENGTH_SHORT).show();
        }
        ex.printStackTrace();
    }


    /**
     * 约定异常
     */
    private class ERROR {
        /**
         * 未知错误
         */
        static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        static final int NETWORK_ERROR = 1002;
        /**
         * 协议出错
         */
        static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        static final int TIMEOUT_ERROR = 1006;
    }

    private static class ResponseThrowable extends Exception {

        int code;
        String message;

        ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }
    }

    private class ServerException extends RuntimeException {
        int code;
        String message;
    }
}

