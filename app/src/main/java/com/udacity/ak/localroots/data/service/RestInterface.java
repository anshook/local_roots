package com.udacity.ak.localroots.data.service;


import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.database.MarketDetail;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestInterface {
    @GET("zipSearch")
    Observable<List<Market>> lookupMarketsByZipcode(@Query("zip") String zipcode);

    @GET("locSearch")
    Observable<List<Market>> lookupMarketsByLocation(@Query("lat") Double latitude,
                                                  @Query("lng") Double longitude);

    @GET("mktDetail")
    Observable<MarketDetail> getMarketDetailsById(@Query("id") String id);

}
