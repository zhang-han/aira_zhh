package com.mapscloud.download.http;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;


public interface DownloadService {
    @Streaming
    @GET
    Observable<ResponseBody> getMapList(@Url String Url);

    @Streaming
    @GET("v1/packages")
    Observable<ResponseBody> getDownloadList(@Query("style") String styleName, @Query("packages") String packages);


    @Streaming
    @GET("v1/base")
    Observable<ResponseBody> getDownloadBaseMap(@Query("style") String styleName);

}
