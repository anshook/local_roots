package com.udacity.ak.localroots.data.service;


import com.udacity.ak.localroots.data.database.Market;

import java.util.ArrayList;
import java.util.List;

public class MarketSearchResponse {

    public List<Market> markets;
    private Throwable error;

    public MarketSearchResponse(List<Market> markets) {
        this.markets = markets;
        this.error = null;
    }

    public MarketSearchResponse(Throwable error) {
        this.error = error;
        this.markets = new ArrayList<>();
    }

    public List<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
