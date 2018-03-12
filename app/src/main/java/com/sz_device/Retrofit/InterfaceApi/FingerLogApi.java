package com.sz_device.Retrofit.InterfaceApi;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by zbsz on 2018/1/10.
 */

public interface FingerLogApi {
    @Headers({"Content-Type: multipart/form-data"})//类似于方法名
    @POST("cjy_updata")
    Observable<String> fingerLog(@Query("dataType") String dataType,@Query("key") String key, @Query("jsonData") String jsonData);
}
