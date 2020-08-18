package com.sz_device.Retrofit.InterfaceApi;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ZJYJBApi {


    @FormUrlEncoded
    @POST("caijiyiDuijie/s/kaiguanmen")
    Observable<ResponseBody> withDataRr(@Field("dataType") String dataType, @Field("key") String key, @Field("jsonData") String jsonData);


    @FormUrlEncoded
    @POST("caijiyiDuijie/s/kaiguanmen")
    Observable<ResponseBody> queryPersonInfo(@Field("dataType") String dataType, @Field("key") String key, @Field("psonIdcard") String id);


}
