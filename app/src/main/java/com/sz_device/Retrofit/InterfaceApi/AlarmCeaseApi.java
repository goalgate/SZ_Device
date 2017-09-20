package com.sz_device.Retrofit.InterfaceApi;

import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Response.ResponseEnvelope;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by zbsz on 2017/9/18.
 */

public interface AlarmCeaseApi {
    @Headers({"Content-Type: text/xml;charset=UTF-8", "style:'document'"})//请求的Action，类似于方法名
    @POST("daszws")
    Observable<ResponseEnvelope> alarmCease(@Body RequestEnvelope requestEnvelope);
}
