package com.sz_device.Retrofit;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz_device.AppInit;
import com.sz_device.Config.WYY_Config;
import com.sz_device.Retrofit.InterfaceApi.ConnectApi;
import com.sz_device.Retrofit.InterfaceApi.WYYConnectApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit变量初始化
 * Created by SmileXie on 16/7/16.
 */
public class RetrofitGenerator {
    private static ConnectApi connectApi;

    private static WYYConnectApi wyyConnectApi;

    private ConnectApi testConnectApi;

    private WYYConnectApi testWYYConnectApi;

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static <S> S createService(Class<S> serviceClass) {
        OkHttpClient client = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json; charset=UTF-8")
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SPUtils.getInstance("config").getString("ServerId")).client(client).build();
        return retrofit.create(serviceClass);
    }

    private <S> S createService(Class<S> serviceClass, String url) {
        OkHttpClient client = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url).client(client).build();
        return retrofit.create(serviceClass);
    }

    public ConnectApi getConnectApi(String url) {
        if (testConnectApi == null) {
            testConnectApi = createService(ConnectApi.class, url);
        }
        return testConnectApi;
    }

    public static ConnectApi getConnectApi() {
        if (connectApi == null) {
            connectApi = createService(ConnectApi.class);
        }
        return connectApi;
    }

    public WYYConnectApi getWyyConnectApi(String url) {
        if (testWYYConnectApi == null) {
            testWYYConnectApi = createService(WYYConnectApi.class, url);
        }
        return testWYYConnectApi;
    }

    public static WYYConnectApi getWyyConnectApi() {
        if (wyyConnectApi == null) {
            wyyConnectApi = createService(WYYConnectApi.class);
        }
        return wyyConnectApi;
    }
}
