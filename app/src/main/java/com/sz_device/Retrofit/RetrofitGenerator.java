package com.sz_device.Retrofit;

import com.log.Lg;
import com.sz_device.Retrofit.InterfaceApi.AlarmCeaseApi;
import com.sz_device.Retrofit.InterfaceApi.AlarmRecordApi;
import com.sz_device.Retrofit.InterfaceApi.CheckOnlineApi;
import com.sz_device.Retrofit.InterfaceApi.CheckRecordApi;
import com.sz_device.Retrofit.InterfaceApi.CloseDoorRecordApi;
import com.sz_device.Retrofit.InterfaceApi.GetFingerPrintApi;
import com.sz_device.Retrofit.InterfaceApi.OpenDoorRecordApi;
import com.sz_device.Retrofit.InterfaceApi.QueryPersonInfoApi;
import com.sz_device.Retrofit.InterfaceApi.RegisterPersonApi;
import com.sz_device.Retrofit.InterfaceApi.StateRecordApi;
import com.sz_device.Retrofit.InterfaceApi.TestNetApi;
import com.sz_device.Retrofit.Request.ResquestModule.AlarmCeaseModule;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.exceptions.OnErrorNotImplementedException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


/**
 * Retrofit变量初始化
 * Created by SmileXie on 16/7/16.
 */
public class RetrofitGenerator {
    private static String TAG = "RetrofitGenerator";
    private static final String PREFS_NAME = "UserInfo";
    //private static final String Uri = "http://192.168.11.167:7001/VerifyService/services/";
    private static final String Uri = "http://192.168.11.165:8080/daWebservice/webservice/";


    private static Strategy strategy = new AnnotationStrategy();
    private static Serializer serializer = new Persister(strategy);

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

    private static TestNetApi testNetApi;

    private static CheckOnlineApi checkOnlineApi;

    private static StateRecordApi stateRecordApi;

    private static OpenDoorRecordApi openDoorRecordApi;

    private static CloseDoorRecordApi closeDoorRecordApi;

    private static GetFingerPrintApi getFingerprintIdApi;

    private static RegisterPersonApi registerPersonApi;

    private static QueryPersonInfoApi queryPersonInfoApi;

    private static CheckRecordApi checkRecordApi;

    private static AlarmRecordApi alarmRecordApi;

    private static AlarmCeaseApi alarmCeaseApi;

    private static <S> S createService(Class<S> serviceClass) {
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) {
                try {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Content-Type", "text/xml;charset=UTF-8")   // 对于SOAP 1.1， 如果是soap1.2 应是Content-Type: application/soap+xml; charset=utf-8
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                } catch (IOException e) {
                    Lg.e(TAG + "createService", e.toString());
                }
                return null;
            }
        });
        OkHttpClient client = okHttpClient.connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Uri).client(client).build();

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

        OkHttpClient client = okHttpClient.connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url).client(client).build();

        return retrofit.create(serviceClass);
    }

    public static TestNetApi getTestNetApi() {
        if (testNetApi == null) {
            testNetApi = createService(TestNetApi.class);
        }
        return testNetApi;
    }

    public static CheckOnlineApi getCheckOnlineApi() {
        if (checkOnlineApi == null) {
            checkOnlineApi = createService(CheckOnlineApi.class);
        }
        return checkOnlineApi;
    }

    public static StateRecordApi getStateRecordApi() {
        if (stateRecordApi == null) {
            stateRecordApi = createService(StateRecordApi.class);
        }
        return stateRecordApi;
    }

    public static OpenDoorRecordApi getOpenDoorRecordApi() {
        if (openDoorRecordApi == null) {
            openDoorRecordApi = createService(OpenDoorRecordApi.class);
        }
        return openDoorRecordApi;
    }

    public static CloseDoorRecordApi getCloseDoorRecordApi() {
        if (closeDoorRecordApi == null) {
            closeDoorRecordApi = createService(CloseDoorRecordApi.class);
        }
        return closeDoorRecordApi;
    }

    public static GetFingerPrintApi getFingerPrintApi() {
        if (getFingerprintIdApi == null) {
            getFingerprintIdApi = createService(GetFingerPrintApi.class);
        }
        return getFingerprintIdApi;
    }

    public static RegisterPersonApi getRegisterPersonApi() {
        if (registerPersonApi == null) {
            registerPersonApi = createService(RegisterPersonApi.class);
        }
        return registerPersonApi;
    }


    public static QueryPersonInfoApi getQueryPersonInfoApi() {
        if (queryPersonInfoApi == null) {
            queryPersonInfoApi = createService(QueryPersonInfoApi.class);
        }
        return queryPersonInfoApi;
    }

    public static CheckRecordApi getCheckRecordApi() {
        if (checkRecordApi == null) {
            checkRecordApi = createService(CheckRecordApi.class);
        }
        return checkRecordApi;
    }

    public static AlarmRecordApi getAlarmRecordApi() {
        if (alarmRecordApi == null) {
            alarmRecordApi = createService(AlarmRecordApi.class);
        }
        return alarmRecordApi;
    }

    public static AlarmCeaseApi getAlarmCeaseApi() {
        if (alarmCeaseApi == null) {
            alarmCeaseApi = createService(AlarmCeaseApi.class);
        }
        return alarmCeaseApi;
    }

}
