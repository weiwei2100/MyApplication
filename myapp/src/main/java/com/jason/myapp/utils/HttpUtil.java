package com.jason.myapp.utils;

import android.util.Log;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by lizhiqi on 16/1/14.
 */
public class HttpUtil {

    private static String TAG = "okHttp";

    private static final String SCHEME = "http";
    public final static int CONNECT_TIMEOUT = 10;
    public final static int READ_TIMEOUT = 10;
    public final static int WRITE_TIMEOUT = 10;

    public static String executePost(String host, int port, String path, Map<String, Object> params) {

        try {

            if (params == null || params.size() == 0) {
                return null;
            }


            String url = SCHEME + "://" + host + ":" + port + path;
            Log.d(TAG, "request url:" + url);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Log.d(TAG, "request parameters:" + params.toString());

            /**
             * 创建请求的参数body
             */
            FormBody.Builder builder = new FormBody.Builder();

            /**
             * 遍历key
             */
            for (Map.Entry<String, Object> entry : params.entrySet()) {

                Log.d(TAG, "Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());

            }

            RequestBody body = builder.build();
            Request request = new Request.Builder().url(url).post(body).build();

            Response response = okHttpClient.newCall(request).execute();
            Log.d(TAG, "The success of the request?:" + response.isSuccessful());

            if (response.isSuccessful()) {
                //call string auto close body
                String result = response.body().string();
                Log.d(TAG, "request success data:" + result);
                return result;
            } else {
                Log.d(TAG, "failure");
                return "request failure";
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "request Exception:" + PrintExceptionMessage.getStackTraceInfo(e));
            throw new RuntimeException(e);

        }


    }

    public static String executePostWithTimeOut(String host, int port, String path, Map<String, Object> params,
                                                int timeout) throws IOException {


        try {

            if (params == null || params.size() == 0) {
                return null;
            }


            String url = SCHEME + "://" + host + ":" + port + path;
            Log.d(TAG, "request url:" + url);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout / 3000, TimeUnit.SECONDS)
                    .writeTimeout(timeout / 3000, TimeUnit.SECONDS)
                    .readTimeout(timeout / 3000, TimeUnit.SECONDS)
                    .build();

            Log.d(TAG, "request parameters:" + params.toString());

            /**
             * 创建请求的参数body
             */
            FormBody.Builder builder = new FormBody.Builder();

            /**
             * 遍历key
             */
            for (Map.Entry<String, Object> entry : params.entrySet()) {

//                Log.d(TAG, "Key = " + entry.getKey() + ", Value = "
//                        + entry.getValue());
                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());

            }

            RequestBody body = builder.build();
            Request request = new Request.Builder().url(url).post(body).build();

            Response response = okHttpClient.newCall(request).execute();
            Log.d(TAG, "The success of the request?:" + response.isSuccessful());

            if (response.isSuccessful()) {
                //call string auto close body
                String result = response.body().string();
                Log.d(TAG, "request success data:" + result);
                return result;
            } else {
                Log.d(TAG, "failure");
                return "request failure";
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "request Exception:" + PrintExceptionMessage.getStackTraceInfo(e));
            throw new RuntimeException(e);

        }
    }

    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public static String executeGetDataFromNet(String url) throws Exception {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //2 将Request封装为Call
        Call call = okHttpClient.newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                Log.d(TAG, "request success data:" + result);
                return result;
            } else {
                Log.d(TAG, "failure");
                return "request failure";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int executeGetResponseCodeFromNet(String url) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //2 将Request封装为Call
        Call call = okHttpClient.newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
            return response.code();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
