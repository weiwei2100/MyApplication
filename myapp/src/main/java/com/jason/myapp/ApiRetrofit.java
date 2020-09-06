package com.jason.myapp;



import android.util.Log;
import com.google.gson.Gson;

import com.orhanobut.logger.Logger;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

/**
 * File descripition:   创建Retrofit
 *
 * @author lp
 * @date 2018/6/19
 */

public class ApiRetrofit {
    private String TAG = "ApiRetrofit %s";

    private static ApiRetrofit mApiRetrofit;
    private Retrofit retrofit;
    private ApiServer apiServer;

    private Gson gson;
    private static final int DEFAULT_TIMEOUT = 15;

    public static String mBaseUrl = BaseContent.baseUrl;

    private static BaseView mBaseView = null;

    private static volatile Type mType = Type.BASE;

    public enum Type {
        FILE,
        BASE,
        BASE_URL,
    }

    public Type getType() {
        return mType;
    }

    public static void setType(Type type) {
        mType = type;
    }

    /**
     * 文件处理
     *
     * @param httpClientBuilder
     */
    public void initFileClient(OkHttpClient.Builder httpClientBuilder) {
        /**
         * 处理文件下载进度展示所需
         */
        httpClientBuilder.addNetworkInterceptor(new ProgressInterceptor());
    }




    public ApiRetrofit() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);//错误重联

                initFileClient(httpClientBuilder);

        retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())//添加json转换框架(正常转换框架)
//                .addConverterFactory(MyGsonConverterFactory.create(buildGson()))//添加json自定义（根据需求，此种方法是拦截gson解析所做操作）
                //支持RxJava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClientBuilder.build())
                .build();
        apiServer = retrofit.create(ApiServer.class);

    }




    /**
     * 默认使用方式
     *
     * @return
     */
    public static ApiRetrofit getInstance() {


        return initRetrofit();
    }

    /**
     * 文件下载使用方式
     *
     * @param baseView
     * @return
     */
    public static ApiRetrofit getFileInstance(BaseView baseView) {
        setType(Type.FILE);
        mBaseView = baseView;
        mBaseUrl = BaseContent.baseUrl + "file/";

        return initRetrofit();
    }



    private static ApiRetrofit initRetrofit() {

                mApiRetrofit = new ApiRetrofit();
                return mApiRetrofit;

    }


    public ApiServer getApiService() {
        return apiServer;
    }


    /**
     * 请求访问quest    打印日志
     * response拦截器
     */
    public class JournalInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            try {
                long startTime = System.currentTimeMillis();
                Response response = chain.proceed(request);
                if (response == null) {
                    return chain.proceed(request);
                }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                MediaType mediaType = response.body().contentType();
                String content = response.body().string();

                Logger.wtf(TAG, "----------Request Start----------------");
                Logger.e(TAG, "| " + request.toString() + "===========" + request.headers().toString());
                Logger.json(content);
                Logger.e(content);
                Logger.wtf(TAG, "----------Request End:" + duration + "毫秒----------");

                return response.newBuilder()
                        .body(ResponseBody.create(mediaType, content))
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                return chain.proceed(request);
            }
        }
    }

    /**
     * 添加  请求头
     */

    public class HeadUrlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
//                    .addHeader("Content-Type", "text/html; charset=UTF-8")
//                    .addHeader("Vary", "Accept-Encoding")
//                    .addHeader("Server", "Apache")
//                    .addHeader("Pragma", "no-cache")
//                    .addHeader("Cookie", "add cookies here")
//                    .addHeader("_identity",  cookie_value)
                    .build();
            return chain.proceed(request);
        }
    }


    /**
     * 文件下载进度拦截
     */
    public class ProgressInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (mBaseView != null) {
                Response response = chain.proceed(request);
                return response.newBuilder().body(new ProgressResponseBody(response.body(),
                        new ProgressResponseBody.ProgressListener() {
                            @Override
                            public void onProgress(long totalSize, long downSize) {
                                int progress = (int) (downSize * 100 / totalSize);
                                if (mBaseView != null) {
                                    mBaseView.onProgress(progress);
                                    Log.e(TAG,"文件下载速度 === " + progress);
                                }
                            }
                        })).build();
            } else {
                return chain.proceed(request);
            }
        }
    }

    /**
     * 获取HTTP 添加公共参数的拦截器
     * 暂时支持get、head请求&Post put patch的表单数据请求
     *
     * @return
     */
    public class HttpParamsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (request.method().equalsIgnoreCase("GET") || request.method().equalsIgnoreCase("HEAD")) {
                HttpUrl httpUrl = request.url().newBuilder()
                        .addQueryParameter("version", "1.1.0")
                        .addQueryParameter("devices", "android")
                        .build();
                request = request.newBuilder().url(httpUrl).build();
            } else {
                RequestBody originalBody = request.body();
                if (originalBody instanceof FormBody) {
                    FormBody.Builder builder = new FormBody.Builder();
                    FormBody formBody = (FormBody) originalBody;
                    for (int i = 0; i < formBody.size(); i++) {
                        builder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                    }
                    FormBody newFormBody = builder
                            .addEncoded("version", "1.1.0")
                            .addEncoded("devices", "android")
                            .build();
                    if (request.method().equalsIgnoreCase("POST")) {
                        request = request.newBuilder().post(newFormBody).build();
                    } else if (request.method().equalsIgnoreCase("PATCH")) {
                        request = request.newBuilder().patch(newFormBody).build();
                    } else if (request.method().equalsIgnoreCase("PUT")) {
                        request = request.newBuilder().put(newFormBody).build();
                    }

                } else if (originalBody instanceof MultipartBody) {

                }

            }
            return chain.proceed(request);
        }
    }

//    /**
//     * 获得HTTP 缓存的拦截器
//     *
//     * @return
//     */
//    public class HttpCacheInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            // 无网络时，始终使用本地Cache
//            if (!NetWorkUtils.isConnected()) {
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//            }
//            Response response = chain.proceed(request);
//            if (NetWorkUtils.isConnected()) {
//                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
//                String cacheControl = request.cacheControl().toString();
//                return response.newBuilder()
//                        .header("Cache-Control", cacheControl)
//                        .removeHeader("Pragma")
//                        .build();
//            } else {
//                // 无网络时，设置超时为4周
//                int maxStale = 60 * 60 * 24 * 28;
//                return response.newBuilder()
//                        //这里的设置的是我们的没有网络的缓存时间，想设置多少就是多少。
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                        .removeHeader("Pragma")
//                        .build();
//            }
//        }
//    }

    /**
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     */

    /**
     * 特殊返回内容  处理方案
     */
    public class MockInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Gson gson = new Gson();
            Response response = null;
            Response.Builder responseBuilder = new Response.Builder()
                    .code(200)
                    .message("")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .addHeader("content-type", "application/json");
            Request request = chain.request();
            if (request.url().toString().contains(BaseContent.baseUrl)) { //拦截指定地址
                String responseString = "{\n" +
                        "\t\"success\": true,\n" +
                        "\t\"data\": [{\n" +
                        "\t\t\"id\": 6,\n" +
                        "\t\t\"type\": 2,\n" +
                        "\t\t\"station_id\": 1,\n" +
                        "\t\t\"datatime\": 1559491200000,\n" +
                        "\t\t\"factors\": [{\n" +
                        "\t\t\t\"id\": 11,\n" +
                        "\t\t\t\"history_id\": 6,\n" +
                        "\t\t\t\"station_id\": 1,\n" +
                        "\t\t\t\"factor_id\": 6,\n" +
                        "\t\t\t\"datatime\": 1559491200000,\n" +
                        "\t\t\t\"value_check\": 2.225,\n" +
                        "\t\t\t\"value_span\": 5.0,\n" +
                        "\t\t\t\"value_standard\": 4.0,\n" +
                        "\t\t\t\"error_difference\": -1.775,\n" +
                        "\t\t\t\"error_percent\": -44.38,\n" +
                        "\t\t\t\"accept\": false\n" +
                        "\t\t}, {\n" +
                        "\t\t\t\"id\": 12,\n" +
                        "\t\t\t\"history_id\": 6,\n" +
                        "\t\t\t\"station_id\": 1,\n" +
                        "\t\t\t\"factor_id\": 7,\n" +
                        "\t\t\t\"datatime\": 1559491200000,\n" +
                        "\t\t\t\"value_check\": 1.595,\n" +
                        "\t\t\t\"value_span\": 0.5,\n" +
                        "\t\t\t\"value_standard\": 1.6,\n" +
                        "\t\t\t\"error_difference\": -0.005,\n" +
                        "\t\t\t\"error_percent\": -0.31,\n" +
                        "\t\t\t\"accept\": true\n" +
                        "\t\t}, {\n" +
                        "\t\t\t\"id\": 13,\n" +
                        "\t\t\t\"history_id\": 6,\n" +
                        "\t\t\t\"station_id\": 1,\n" +
                        "\t\t\t\"factor_id\": 8,\n" +
                        "\t\t\t\"datatime\": 1559491200000,\n" +
                        "\t\t\t\"value_check\": 8.104,\n" +
                        "\t\t\t\"value_span\": 20.0,\n" +
                        "\t\t\t\"value_standard\": 8.0,\n" +
                        "\t\t\t\"error_difference\": 0.104,\n" +
                        "\t\t\t\"error_percent\": 1.3,\n" +
                        "\t\t\t\"accept\": true\n" +
                        "\t\t},null]\n" +
                        "\t}],\n" +
                        "\t\"additional_data\": {\n" +
                        "\t\t\"totalPage\": 0,\n" +
                        "\t\t\"startPage\": 1,\n" +
                        "\t\t\"limit\": 30,\n" +
                        "\t\t\"total\": 0,\n" +
                        "\t\t\"more_items_in_collection\": false\n" +
                        "\t},\n" +
                        "\t\"related_objects\": [{\n" +
                        "\t\t\"id\": 6,\n" +
                        "\t\t\"name\": \"氨氮\",\n" +
                        "\t\t\"unit\": \"mg/L\",\n" +
                        "\t\t\"db_field\": \"nh3n\",\n" +
                        "\t\t\"qa_ratio\": true\n" +
                        "\t}, {\n" +
                        "\t\t\"id\": 7,\n" +
                        "\t\t\"name\": \"总磷\",\n" +
                        "\t\t\"unit\": \"mg/L\",\n" +
                        "\t\t\"db_field\": \"tp\",\n" +
                        "\t\t\"qa_ratio\": true\n" +
                        "\t}, {\n" +
                        "\t\t\"id\": 8,\n" +
                        "\t\t\"name\": \"总氮\",\n" +
                        "\t\t\"unit\": \"mg/L\",\n" +
                        "\t\t\"db_field\": \"tn\",\n" +
                        "\t\t\"qa_ratio\": true\n" +
                        "\t}, {\n" +
                        "\t\t\"id\": 9,\n" +
                        "\t\t\"name\": \"CODMn\",\n" +
                        "\t\t\"unit\": \"mg/L\",\n" +
                        "\t\t\"db_field\": \"codmn\",\n" +
                        "\t\t\"qa_ratio\": true\n" +
                        "\t}],\n" +
                        "\t\"request_time\": \"2019-06-05T16:40:14.915+08:00\"\n" +
                        "}";
                responseBuilder.body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()));//将数据设置到body中
                response = responseBuilder.build(); //builder模式构建response
            } else {
                response = chain.proceed(request);
            }
            return response;
        }
    }

}
