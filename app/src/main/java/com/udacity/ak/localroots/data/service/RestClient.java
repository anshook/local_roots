package com.udacity.ak.localroots.data.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.database.MarketDetail;

import java.lang.reflect.Type;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RestClient {
    private static final String BASE_URL = "https://search.ams.usda.gov/farmersmarkets/v1/data.svc/";
    private static Retrofit retrofit_marketlist = null;
    private static Retrofit retrofit_marketdetail = null;
    private static Retrofit retrofit_gsonconverter = null;

    private static RxJava2CallAdapterFactory rxAdapter = null;

    private RestClient() {}

    private static Converter.Factory createGsonConverter(Type type, Object typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        Gson gson = gsonBuilder.create();

        return GsonConverterFactory.create(gson);
    }

    public static Retrofit getRetrofit(Type type) {

        if(type.equals(MarketDetail.class)) {
            if (retrofit_marketdetail == null) {
                rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
                retrofit_marketdetail = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(createGsonConverter(type, new MarketDetailDeserializer()))
                        .addCallAdapterFactory(rxAdapter)
                        .build();
            }
            return retrofit_marketdetail;
        }
        else {
            if (retrofit_marketlist == null) {
                rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
                retrofit_marketlist = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(createGsonConverter(type, new MarketListDeserializer()))
                        .addCallAdapterFactory(rxAdapter)
                        .build();
            }
            return retrofit_marketlist;
        }

    }

    public static RestInterface getRestService(Type type)
    {
        return getRetrofit(type).create(RestInterface.class);
    }

}
