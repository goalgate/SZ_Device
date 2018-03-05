package com.sz_device.Retrofit.InterfaceApi;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zbsz on 2018/3/2.
 */

public interface SearchICKBdApi {
    @POST("cjy_updata")
    Observable<ResponseBody> searchICKBd(@Query("dataType") String dataType, @Query("key") String key, @Query("jsonData") String jsonData);

}
