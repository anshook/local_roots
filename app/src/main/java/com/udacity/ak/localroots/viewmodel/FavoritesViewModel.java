package com.udacity.ak.localroots.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;

import java.util.List;

public class FavoritesViewModel extends ViewModel {
    private MarketRepository mRepository;
    private LiveData<List<Market>> mlistFavoriteMarkets;

    public FavoritesViewModel(MarketRepository marketRepository) {
        mRepository = marketRepository;
        mlistFavoriteMarkets = mRepository.getFavoriteMarkets();
    }

    public LiveData<List<Market>> getFavoriteMarketList() {
        return mlistFavoriteMarkets;
    }

    public void removeFavorite(String id) {
        mRepository.deleteFavorite(id);
    }
}
