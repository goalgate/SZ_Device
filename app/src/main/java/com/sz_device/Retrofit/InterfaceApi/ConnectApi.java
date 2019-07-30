package com.sz_device.Retrofit.InterfaceApi;

import com.sz_device.AppInit;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    @POST("{prefix}")
    Observable<String> noData1(@Path ("prefix") String prefix,@Field("dataType") String dataType, @Field("key") String key);

    @FormUrlEncoded
    @POST("cjy_updata")
    Observable<ResponseBody> queryPersonInfo(@Field("dataType") String dataType, @Field("key") String key, @Field("id") String id);


//    @FormUrlEncoded
//    @POST("wbyCjy/s/updata")
//    Observable<String> withDataRs(@Field("dataType") String dataType, @Field("key") String key, @Field("jsonData") String jsonData);
//
//    @FormUrlEncoded
//    @POST("wbyCjy/s/updata")
//    Observable<ResponseBody> withDataRr(@Field("dataType") String dataType, @Field("key") String key, @Field("jsonData") String jsonData);
//
//    @FormUrlEncoded
//    @POST("wbyCjy/s/updata")
//    Observable<String> noData(@Field("dataType") String dataType, @Field("key") String key);
//
//    @FormUrlEncoded
//    @POST("wbyCjy/s/updata")
//    Observable<ResponseBody> queryPersonInfo(@Field("dataType") String dataType, @Field("key") String key, @Field("idcard") String id);

}

