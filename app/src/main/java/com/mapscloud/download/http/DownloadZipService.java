package com.mapscloud.download.http;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mapscloud3 on 2018/5/22.
 */

public interface DownloadZipService {
    @Streaming
    @GET
    Observable<ResponseBody> getZip(@Url String Url);
}
