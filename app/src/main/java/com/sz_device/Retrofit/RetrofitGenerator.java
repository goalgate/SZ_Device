package com.sz_device.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz_device.AppInit;
import com.sz_device.Retrofit.InterfaceApi.ShaoXingApi;
import com.sz_device.Retrofit.InterfaceApi.ConnectApi;
import com.sz_device.Retrofit.InterfaceApi.GDYZBConnectApi;
import com.sz_device.Retrofit.InterfaceApi.HNMBYApi;
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
 */
public class RetrofitGenerator {

    private static int outTime = 60;

    private static ConnectApi connectApi;

    private static WYYConnectApi wyyConnectApi;

    private static HNMBYApi hnmbyApi;

    private static ShaoXingApi commonApi;

    private static GDYZBConnectApi gdyzbConnectApi;

    private ConnectApi testConnectApi;

    private WYYConnectApi testWYYConnectApi;

    private HNMBYApi testHnmbyApi;

    private ShaoXingApi testCommonApi;

    private GDYZBConnectApi testGDYZBConnectApi;

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static <S> S createService(Class<S> serviceClass) {
        OkHttpClient client = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(outTime, TimeUnit.SECONDS)
                .readTimeout(outTime, TimeUnit.SECONDS)
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
                .baseUrl(AppInit.getInstrumentConfig().getServerId()).client(client).build();
//                .baseUrl(SPUtils.getInstance("config").getString("ServerId")).client(client).build();
        return retrofit.create(serviceClass);
    }

    private <S> S createService(Class<S> serviceClass, String url) {
        OkHttpClient client = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(outTime, TimeUnit.SECONDS)
                .readTimeout(outTime, TimeUnit.SECONDS)
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

    public HNMBYApi getHnmbyApi(String url) {
        if (testHnmbyApi == null) {
            testHnmbyApi = createService(HNMBYApi.class, url);
        }
        return testHnmbyApi;
    }

    public static HNMBYApi getHnmbyApi() {
        if (hnmbyApi == null) {
            hnmbyApi = createService(HNMBYApi.class);
        }
        return hnmbyApi;
    }

    public static ShaoXingApi getShaoXingApi() {
        if (commonApi == null) {
            commonApi = createService(ShaoXingApi.class);
        }
        return commonApi;
    }

    public ShaoXingApi getShaoXingApi(String url) {
        if (testCommonApi == null) {
            testCommonApi = createService(ShaoXingApi.class, url);
        }
        return testCommonApi;
    }

    public static GDYZBConnectApi getGdyzbConnectApi() {
        if (gdyzbConnectApi == null) {
            gdyzbConnectApi = createService(GDYZBConnectApi.class);
        }
        return gdyzbConnectApi;
    }

    public GDYZBConnectApi getGdyzbConnectApi(String url) {
        if (testGDYZBConnectApi == null) {
            testGDYZBConnectApi = createService(GDYZBConnectApi.class, url);
        }
        return testGDYZBConnectApi;
    }
}
