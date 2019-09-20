package com.udacity.ak.localroots.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.data.service.MarketSearchResponse;

import java.util.List;

public class MarketListViewModel extends ViewModel {
    private MarketRepository mRepository;
    private LiveData<MarketSearchResponse> mSearchResponse;
    private MutableLiveData<List<Market>> marketListForDisplay = new MutableLiveData<>();
    private Market marketSelection;
    private boolean clearRepoOnDestroy = true;

    public MarketListViewModel(MarketRepository marketRepository) {
        mRepository = marketRepository;
        mSearchResponse = mRepository.getNearbyMarkets();
    }

    public LiveData<MarketSearchResponse> getNearbyMarkets() { return mSearchResponse;}


    public MutableLiveData<List<Market>> getMarketListForDisplay() {
        return marketListForDisplay;
    }

    public void setMarketListForDisplay(List<Market> marketList) {
        marketListForDisplay.setValue(marketList);
    }

    public void setMarketSelection(Market market) {
        marketSelection = market;
    }

    public Market getMarketSelection() {
        return marketSelection;
    }

    public void setClearRepoOnDestroy(boolean flag) {
        clearRepoOnDestroy = flag;
    }

    public boolean getClearRepoOnDestroy() {
        return clearRepoOnDestroy;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(clearRepoOnDestroy)
            mRepository.clearNearbyMarkets();
    }
}
