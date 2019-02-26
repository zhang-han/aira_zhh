package com.mapscloud.download.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitManager {
    private static RetrofitManager instance;
    public static final String DOWNLOAD_URL = "http://202.107.245.40:12311/";

    private RetrofitManager(){

    }

    public static RetrofitManager getInstance(){
        if(instance == null){
            synchronized (RetrofitManager.class){
                if(instance == null){
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    private OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(10000,10000, TimeUnit.SECONDS))
                .retryOnConnectionFailure(true)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept","application/json")
                                .addHeader("Connection", "keep-alive")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        return client;
    }

    public DownloadService getDownloadService(){
        return getZipRetrofit().create(DownloadService.class);
    }

    public Retrofit getZipRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com/")
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public Retrofit getDownLoadData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DOWNLOAD_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public DownloadService getDownloadDataService(){
        return getDownLoadData().create(DownloadService.class);
    }

    public DownloadZipService getZipService(){
        return getZipRetrofit().create(DownloadZipService.class);
    }


}
