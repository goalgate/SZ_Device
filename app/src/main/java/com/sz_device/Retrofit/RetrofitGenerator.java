package com.sz_device.Retrofit;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz_device.Retrofit.InterfaceApi.AlarmRecordApi;
import com.sz_device.Retrofit.InterfaceApi.CheckRecordApi;
import com.sz_device.Retrofit.InterfaceApi.CloseDoorRecordApi;
import com.sz_device.Retrofit.InterfaceApi.DeleteFingerApi;
import com.sz_device.Retrofit.InterfaceApi.FingerLogApi;
import com.sz_device.Retrofit.InterfaceApi.GetTimeApi;
import com.sz_device.Retrofit.InterfaceApi.OpenDoorRecordApi;
import com.sz_device.Retrofit.InterfaceApi.QueryPersonInfoApi;
import com.sz_device.Retrofit.InterfaceApi.SaveVisitApi;
import com.sz_device.Retrofit.InterfaceApi.SearchFingerApi;
import com.sz_device.Retrofit.InterfaceApi.SearchICKBdApi;
import com.sz_device.Retrofit.InterfaceApi.StateRecordApi;
import com.sz_device.Retrofit.InterfaceApi.TestNetApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit变量初始化
 * Created by SmileXie on 16/7/16.
 */
public class RetrofitGenerator {
   /* private static String TAG = "RetrofitGenerator";
    private static final String PREFS_NAME = "UserInfo";
    private static final String Uri = "http://192.168.11.165:8080/daWebservice/webservice/";

    private static Strategy strategy = new AnnotationStrategy();
    private static Serializer serializer = new Persister(strategy);

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

    private static CommonApi commonApi;


    private static <S> S createService(Class<S> serviceClass) {
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "text/xml;charset=UTF-8")   // 对于SOAP 1.1， 如果是soap1.2 应是Content-Type: application/soap+xml; charset=utf-8
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SPUtils.getInstance(PREFS_NAME).getString("server")).client(client).build();
        return retrofit.create(serviceClass);
    }


    public <S> S createSer(Class<S> serviceClass, String url) {
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "text/xml;charset=UTF-8")   // 对于SOAP 1.1， 如果是soap1.2 应是Content-Type: application/soap+xml; charset=utf-8
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = okHttpClient.connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.MINUTES)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url).client(client).build();

        return retrofit.create(serviceClass);
    }

    public static CommonApi getCommonApi() {
        if (commonApi == null) {
            commonApi = createService(CommonApi.class);
        }
        return commonApi;
    }*/
    private static GetTimeApi getTimeApi;
    private static AlarmRecordApi alarmRecordApi;
    private static CheckRecordApi checkRecordApi;
    private static CloseDoorRecordApi closeDoorRecordApi;
    private static FingerLogApi fingerLogApi;
    private static OpenDoorRecordApi openDoorRecordApi;
    private static QueryPersonInfoApi queryPersonInfoApi;
    private static SearchFingerApi searchFingerApi;
    private static StateRecordApi stateRecordApi;
    private static TestNetApi testNetApi;
    private static SaveVisitApi saveVisitApi;
    private static DeleteFingerApi deleteFingerApi;
    private static SearchICKBdApi searchICKBdApi;
    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static <S> S createService(Class<S> serviceClass) {
        OkHttpClient client = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SPUtils.getInstance("config").getString("ServerId")).client(client).build();
        return retrofit.create(serviceClass);
    }

    public static GetTimeApi getTimeApi() {
        if (getTimeApi == null) {
            getTimeApi = createService(GetTimeApi.class);
        }
        return getTimeApi;
    }

    public static AlarmRecordApi getAlarmRecordApi() {
        if (alarmRecordApi == null) {
            alarmRecordApi = createService(AlarmRecordApi.class);
        }
        return alarmRecordApi;
    }

    public static CheckRecordApi getCheckRecordApi() {
        if (checkRecordApi == null) {
            checkRecordApi = createService(CheckRecordApi.class);
        }
        return checkRecordApi;
    }
    public static CloseDoorRecordApi getCloseDoorRecordApi() {
        if (closeDoorRecordApi == null) {
            closeDoorRecordApi = createService(CloseDoorRecordApi.class);
        }
        return closeDoorRecordApi;
    }

    public static FingerLogApi getFingerLogApi() {
        if (fingerLogApi == null) {
            fingerLogApi = createService(FingerLogApi.class);
        }
        return fingerLogApi;
    }

    public static OpenDoorRecordApi getOpenDoorRecordApi() {
        if (openDoorRecordApi == null) {
            openDoorRecordApi = createService(OpenDoorRecordApi.class);
        }
        return openDoorRecordApi;
    }

    public static QueryPersonInfoApi getQueryPersonInfoApi() {
        if (queryPersonInfoApi == null) {
            queryPersonInfoApi = createService(QueryPersonInfoApi.class);
        }
        return queryPersonInfoApi;
    }

    public static SearchFingerApi getSearchFingerApi() {
        if (searchFingerApi == null) {
            searchFingerApi = createService(SearchFingerApi.class);
        }
        return searchFingerApi;
    }
    public static StateRecordApi stateRecordApi() {
        if (stateRecordApi == null) {
            stateRecordApi = createService(StateRecordApi.class);
        }
        return stateRecordApi;
    }

    public static TestNetApi getTestNetApi() {
        if (testNetApi == null) {
            testNetApi = createService(TestNetApi.class);
        }
        return testNetApi;
    }

    public static SaveVisitApi getSaveVisitApi() {
        if (saveVisitApi == null) {
            saveVisitApi = createService(SaveVisitApi.class);
        }
        return saveVisitApi;
    }

    public static DeleteFingerApi getDeleteFingerApi() {
        if (deleteFingerApi== null) {
            deleteFingerApi = createService(DeleteFingerApi.class);
        }
        return deleteFingerApi;
    }
    public static SearchICKBdApi getSearchICKBdApi () {
        if (searchICKBdApi== null) {
            searchICKBdApi = createService(SearchICKBdApi.class);
        }
        return searchICKBdApi;
    }
}
