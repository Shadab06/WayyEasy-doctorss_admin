package com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiControllers {

//    public static final String BASE_URL = "https://api.wayyeasy.in/api/";
    public static final String BASE_URL = "http://192.168.0.100:2001/api/";
    public static ApiControllers clientObject;
    public static Retrofit retrofit;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build();

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    ApiControllers() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
