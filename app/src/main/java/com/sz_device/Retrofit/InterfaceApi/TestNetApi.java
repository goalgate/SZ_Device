package com.sz_device.Retrofit.InterfaceApi;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zbsz on 2018/1/10.
 */

public interface TestNetApi {
    @Headers({"Content-Type: multipart/form-data"})//类似于方法名
    @POST("cjy_updata")
    Observable<String> testNet(@Query("dataType") String dataType,@Query("key") String key);
}
