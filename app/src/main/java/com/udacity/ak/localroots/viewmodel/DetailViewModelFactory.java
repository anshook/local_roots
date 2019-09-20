package com.udacity.ak.localroots.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.ak.localroots.data.repository.MarketRepository;


public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MarketRepository mRepository;

    public DetailViewModelFactory(MarketRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MarketDetailViewModel(mRepository);
    }
}