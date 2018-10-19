package com.sz_device.Retrofit.InterfaceApi;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ConnectApi {
    @FormUrlEncoded
    @POST("cjy_updata")
    Observable<String> withDataRs(@Field("dataType") String dataType, @Field("key") String key, @Field("jsonData") String jsonData);

    @FormUrlEncoded
    @POST("cjy_updata")
    Observable<ResponseBody> withDataRr(@Field("dataType") String dataType, @Field("key") String key, @Field("jsonData") String jsonData);

    @FormUrlEncoded
    @POST("cjy_updata")
    Observable<String> noData(@Field("dataType") String dataType, @Field("key") String key);

    @FormUrlEncoded
    @POST("cjy_updata")
    Observable<ResponseBody> queryPersonInfo(@Field("dataType") String dataType, @Field("key") String key, @Field("id") String id);
}

