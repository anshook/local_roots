package com.udacity.ak.localroots.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.ui.adapter.MarketAdapter;
import com.udacity.ak.localroots.ui.fragment.MapListFragment;
import com.udacity.ak.localroots.viewmodel.ListViewModelFactory;
import com.udacity.ak.localroots.viewmodel.MarketListViewModel;

import java.util.List;

public class MarketMapActivity extends AppCompatActivity
        implements MapListFragment.OnMarketClickListener, MapListFragment.OnViewTypeClickListener{

    private static final String ACTION_BAR_TITLE = "saved_action_bar_title";

    private List<Market> mMarketList;
    private MarketListViewModel mViewModel;
    private MarketAdapter mMarketAdapter;
    private MarketRepository mRepository;
    String mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_map);

        if(getIntent().hasExtra(MarketListActivity.BAR_TITLE_EXTRA)) {
            mActionBarTitle = getIntent().getStringExtra(MarketListActivity.BAR_TITLE_EXTRA);
        }

        if(savedInstanceState == null) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }

        mRepository = MarketRepository.getInstance(this.getApplication());
        ListViewModelFactory factory = new ListViewModelFactory(mRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(MarketListViewModel.class);

        mViewModel.getNearbyMarkets().observe(this, marketSearchResponse -> {
            mMarketList = marketSearchResponse.getMarkets();
            mViewModel.setClearRepoOnDestroy(false);
            mViewModel.setMarketListForDisplay(mMarketList);
            //create fragments
            if (savedInstanceState == null) {
                MapListFragment mapListFragment =
                        MapListFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.map_markets_container, mapListFragment)
                        .commit();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mActionBarTitle = savedInstanceState.getString(ACTION_BAR_TITLE);
        getSupportActionBar().setTitle(mActionBarTitle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTION_BAR_TITLE, mActionBarTitle);
    }

    @Override
    public void onMarketSelected(Market market) {
        Intent intent = new Intent(this, MarketDetailActivity.class);
        intent.putExtra(MarketListActivity.MARKET_EXTRA, market);
        startActivity(intent);
    }

    @Override
    public void onSwitchListView() {
        Intent intent = new Intent(this, MarketListActivity.class);
        startActivity(intent);
    }
}
