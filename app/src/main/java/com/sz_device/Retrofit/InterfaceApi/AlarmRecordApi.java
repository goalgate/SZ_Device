package com.sz_device.Retrofit.InterfaceApi;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zbsz on 2018/1/10.
 */

public interface AlarmRecordApi {
    @FormUrlEncoded
    @POST("cjy_updata")
    Observable<ResponseBody> alarmRecord(@Field("dataType") String dataType, @Field("key") String key, @Field("jsonData") String jsonData);
}
