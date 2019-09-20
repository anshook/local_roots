package com.udacity.ak.localroots.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;

import java.util.List;

public class MarketDetailViewModel extends ViewModel {
    private MarketRepository mRepository;
    private LiveData<List<String>> mListFavoriteMarketIds;

    public MarketDetailViewModel(MarketRepository marketRepository) {
        mRepository = marketRepository;
        mListFavoriteMarketIds = mRepository.getAllFavoriteIds();
    }

    public Market getMarket(int marketIndex) {
        List<Market> marketList = mRepository.getNearbyMarkets().getValue().getMarkets();
        if(marketList.size() >= marketIndex)
            return marketList.get(marketIndex);
        else
            return null;
    }

    public LiveData<List<String>> getFavoriteMarketIds() {
        return mListFavoriteMarketIds;
    }

    public void addFavorite(Market favorite) {
        mRepository.addFavorite(favorite);
    }

    public void removeFavorite(String id) {
        mRepository.deleteFavorite(id);
    }
}
