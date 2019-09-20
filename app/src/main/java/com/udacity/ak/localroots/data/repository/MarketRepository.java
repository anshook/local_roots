package com.udacity.ak.localroots.data.repository;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.udacity.ak.localroots.data.database.FavoriteMarketDao;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.database.MarketDatabase;
import com.udacity.ak.localroots.data.database.MarketDetail;
import com.udacity.ak.localroots.data.service.MarketSearchResponse;
import com.udacity.ak.localroots.data.service.RestClient;
import com.udacity.ak.localroots.utilities.AppExecutors;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MarketRepository {

    private static final Object LOCK = new Object();
    private static MarketRepository rInstance;
    private AppExecutors mExecutors;
    private MarketDatabase marketDB;
    private FavoriteMarketDao favoriteMarketDao;
    private LiveData<List<Market>> listFavoriteMarkets = new MutableLiveData<>();
    private LiveData<List<String>> listFavoriteMarketIds = new MutableLiveData<>();
    private MutableLiveData<MarketSearchResponse> marketSearchResponse = new MutableLiveData<>();
    private List<Market> marketSearchResult;

    private MarketRepository(Application application) {
        mExecutors = AppExecutors.getInstance();
        marketDB = MarketDatabase.getDatabase(application);
        favoriteMarketDao = marketDB.favoriteMarketDao();
        loadFavoriteMarkets();
    }

    public synchronized static MarketRepository getInstance(
            Application application) {
        if (rInstance == null) {
            synchronized (LOCK) {
                rInstance = new MarketRepository(application);
            }
        }
        return rInstance;
    }

    public LiveData<List<Market>> getFavoriteMarkets() {
        return listFavoriteMarkets;
    }

    public LiveData<List<String>> getAllFavoriteIds() {
        mExecutors.diskIO().execute(() -> {
            listFavoriteMarketIds = favoriteMarketDao.getAllFavoriteIds();
        });
        return listFavoriteMarketIds;
    }

    public void loadFavoriteMarkets() {
        mExecutors.diskIO().execute(() -> {
            listFavoriteMarkets = favoriteMarketDao.getAllFavorites();
        });
    }

    public void deleteFavorite(String id) {
        mExecutors.diskIO().execute(() -> {
            favoriteMarketDao.deleteById(id);
        });
    }

    public void addFavorite(Market favoriteMarket) {
        mExecutors.diskIO().execute(() -> {
            long ret = favoriteMarketDao.insertFavorite(favoriteMarket);
        });
    }

    public MutableLiveData<MarketSearchResponse> getNearbyMarkets() {
        return marketSearchResponse;
    }

    public void clearNearbyMarkets() {
        marketSearchResponse.postValue(new MarketSearchResponse(new ArrayList<>()));
    }

    public void loadNearbyMarkets(String zipcode) {
        getMarketListObservable(zipcode).flatMapSingle(marketList ->
                        Observable.fromIterable(marketList)
                                .concatMapEager(marketObj -> getMarketDetailObservable(marketObj))
                                .toList()
        ).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<Market>>() {

                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(List<Market> markets) {
                    marketSearchResult = markets;
                }

                @Override
                public void onError(Throwable e) {
                    marketSearchResponse.postValue(new MarketSearchResponse(e));
                    Timber.e(e);
                }

                @Override
                public void onComplete() {
                    marketSearchResponse.postValue(new MarketSearchResponse(marketSearchResult));
                }
            });

    }

    public void loadNearbyMarkets(Double dLatitude, Double dLongitute) {
        getMarketListObservable(dLatitude, dLongitute).flatMapSingle(marketList ->
                Observable.fromIterable(marketList)
                        .flatMap(marketObj -> getMarketDetailObservable(marketObj))
                        .toList()
        ).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<Market>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Market> markets) {
                        marketSearchResult = markets;
                    }

                    @Override
                    public void onError(Throwable e) {
                        marketSearchResponse.postValue(new MarketSearchResponse(e));
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        marketSearchResponse.postValue(new MarketSearchResponse(marketSearchResult));
                    }
                });
    }

    /**
     * Retrofit call to fetch markets by zipcode
     */
    private Observable<List<Market>> getMarketListObservable(String zipcode) {
        return RestClient.getRestService(new TypeToken<List<Market>>() {}.getType()).lookupMarketsByZipcode(zipcode)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * Retrofit call to fetch markets by device location
     */
    private Observable<List<Market>> getMarketListObservable(Double dLatitude, Double dLongitude) {
        return RestClient.getRestService(new TypeToken<List<Market>>() {}.getType()).lookupMarketsByLocation(dLatitude, dLongitude)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * Retrofit call to get market details.
     * map() operator is used to change the return type to Market
     */
    private Observable<Market> getMarketDetailObservable(final Market market) {
        //Market may contain error with details. In that case do not get Market Details
        if(market.getId().equalsIgnoreCase("error")) {
            Observable<Market> observable = Observable.just(market);
            return observable;
        }
        return RestClient.getRestService(MarketDetail.class)
                .getMarketDetailsById(market.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<MarketDetail, Market>() {
                    @Override
                    public Market apply(MarketDetail marketDetail) throws Exception {
                        try {
                            String strMarketName = market.getMarketname();
                            String strMiles = strMarketName.substring(0, strMarketName.indexOf(' '));
                            String strName = strMarketName.substring(strMarketName.indexOf(' ') + 1);

                            String strLatitude = "";
                            String strLongitude = "";
                            String strGoogleLink = marketDetail.getGoogleLink();
                            String strMapQueryString = extractMapQueryString(strGoogleLink);
                            if (strMapQueryString != null) {
                                strLatitude = strMapQueryString.substring(2,
                                        strMapQueryString.indexOf(',')).trim();
                                strLongitude = strMapQueryString.substring(strMapQueryString.indexOf(',') + 1)
                                        .trim();
                            }

                            market.setMarketname(strName);
                            market.setMiles(strMiles);
                            market.setLatitude(strLatitude);
                            market.setLongitude(strLongitude);
                            market.setAddress(marketDetail.getAddress());
                            market.setSchedule(marketDetail.getSchedule());
                            market.setProducts(marketDetail.getProducts());
                        }
                        catch (Throwable e) {
                            Timber.e(e);
                        }
                        return market;
                    }

                });
    }

    /*
       Extract the query string with latitude and longitude from maps URL
       Sample URL:
       http:\/\/maps.google.com\/?q=37.6555, -122.427 (Location-Info)
     */
    private String extractMapQueryString(String strGoogleLink) {
        String pattern = "q=[-?\\d\\.]*\\,\\s?([-?\\d\\.]*)";
        String strDecoded = "";
        try {
            strDecoded = URLDecoder.decode(strGoogleLink, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e);
            return null;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(strDecoded);


        if (m.find())
        {
            return m.group();
        }
        return null;
    }

}
