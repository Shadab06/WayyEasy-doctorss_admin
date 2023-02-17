package com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiControllers {

    public static final String url = "https://api.wayyeasy.in/api/";
    //    public static final String url = "http://192.168.0.102:2001/api/";
    public static ApiControllers clientObject;
    public static Retrofit retrofit;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    ApiControllers() {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized ApiControllers getInstance() {
        if (clientObject == null)
            clientObject = new ApiControllers();
        return clientObject;
    }

    public ApiSet getApi() {
        return retrofit.create(ApiSet.class);
    }
}
