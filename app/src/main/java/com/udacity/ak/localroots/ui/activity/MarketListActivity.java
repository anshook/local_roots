package com.udacity.ak.localroots.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.data.service.MarketSearchResponse;
import com.udacity.ak.localroots.ui.adapter.MarketAdapter;
import com.udacity.ak.localroots.ui.fragment.DetailFragment;
import com.udacity.ak.localroots.ui.fragment.ListFragment;
import com.udacity.ak.localroots.ui.fragment.MapListFragment;
import com.udacity.ak.localroots.viewmodel.ListViewModelFactory;
import com.udacity.ak.localroots.viewmodel.MarketListViewModel;

import java.util.List;

import butterknife.BindBool;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketListActivity extends AppCompatActivity
        implements ListFragment.OnMarketClickListener, MapListFragment.OnMarketClickListener,
            OnMapReadyCallback, ListFragment.OnViewTypeClickListener,
            MapListFragment.OnViewTypeClickListener {

    public static final String MARKET_EXTRA = "market_extra";
    public static final String BAR_TITLE_EXTRA = "bar_title_extra";
    public static final String VIEWTYPE_LIST = "list";
    public static final String VIEWTYPE_MAP = "map";
    private static final String ACTION_BAR_TITLE = "saved_action_bar_title";

    @BindBool(R.bool.is_tablet) boolean mIsTablet;
    @BindDimen(R.dimen.map_padding) int mapPadding;
    @BindString(R.string.something_went_wrong) String mStringSomethingWentWrong;
    @BindString(R.string.no_results) String mStringNoResults;
    @BindString(R.string.result_title_default) String mStringTitleDefault;
    @BindString(R.string.result_title_prefix) String mStringTitlePrefix;
    @BindView(R.id.pb_loading) ProgressBar mProgressBar;
    @BindView(R.id.tv_message) TextView mMessageView;
    @BindView(R.id.iv_error) ImageView mImageError;

    MapFragment mapFragment;
    ListFragment listFragment;
    MapListFragment mapListFragment;

    private List<Market> mMarketList;
    private MarketListViewModel mViewModel;
    private MarketAdapter mMarketAdapter;
    private MarketRepository mRepository;
    private String mActionBarTitle;
    private String mSearchArea = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);
        ButterKnife.bind(this, this);

        if(getIntent().hasExtra(MainActivity.SEARCH_EXTRA)) {
            mSearchArea = getIntent().getStringExtra(MainActivity.SEARCH_EXTRA);
        }

        if(savedInstanceState == null) {
            if(mSearchArea.isEmpty())
                mActionBarTitle = mStringTitleDefault;
            else
                mActionBarTitle = mStringTitlePrefix + " " + mSearchArea;

            getSupportActionBar().setTitle(mActionBarTitle);
        }

        //display ProgressBar while the market results load
        mProgressBar.setVisibility(View.VISIBLE);

        mRepository = MarketRepository.getInstance(this.getApplication());
        ListViewModelFactory factory = new ListViewModelFactory(mRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(MarketListViewModel.class);

        mViewModel.getNearbyMarkets().observe(this, marketSearchResponse -> {
            mProgressBar.setVisibility(View.GONE);
            if(marketSearchResponse == null || marketSearchResponse.getError() != null)
            {
                showError(mStringSomethingWentWrong);
            }
            else {
                mMarketList = marketSearchResponse.getMarkets();
                String errorInResponse = errorInResponse(marketSearchResponse);

                if(mMarketList.size()==0){
                    showError(mStringNoResults);
                }
                else if (errorInResponse != null) {
                    showError(errorInResponse);
                }
                else{
                    hideErrorUI();
                    mViewModel.setMarketListForDisplay(mMarketList);
                    createFragments(savedInstanceState);
                }
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

    /*
    Market search API may send error details in teh result array.
    Sample error response: {"results":[{"id":"Error","marketname":"Didn't find that zip code."}]}
    */
    private String errorInResponse(MarketSearchResponse marketSearchResponse) {
        if(marketSearchResponse.getMarkets().size() == 1)
        {
            Market market = marketSearchResponse.getMarkets().get(0);
            if(market.getId().equalsIgnoreCase("error"))
                return market.getMarketname();
        }
        return null;
    }

    private void showError(String errorMessage) {
        mMessageView.setText(errorMessage);
        mMessageView.setVisibility(View.VISIBLE);
        mImageError.setVisibility(View.VISIBLE);

        //Hide Map Fragment in 3-pane layout
        if(mIsTablet) {
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_market);
            getFragmentManager().beginTransaction().hide(mapFragment).commit();
        }
    }

    private void hideErrorUI() {
        mMessageView.setVisibility(View.GONE);
        mImageError.setVisibility(View.GONE);

        //Show Map Fragment in 3-pane layout
        if(mIsTablet) {
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_market);
            getFragmentManager().beginTransaction().show(mapFragment).commit();
        }
    }


    private void createFragments(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            //List Fragment
            listFragment = ListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_container, listFragment)
                    .commit();

            if (mIsTablet) {
                //MapList Fragment
                mapListFragment =
                        MapListFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map_markets_container, mapListFragment)
                        .commit();

                //Map Fragment for the first Market in the result
                mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map_market);
                mViewModel.setMarketSelection(mMarketList.get(0));
                mapFragment.getMapAsync(this);

                //Detail Fragment for the first Market in the result
                DetailFragment detailFragment =
                        DetailFragment.newInstance(mMarketList.get(0));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, detailFragment)
                        .commit();
            }
        }
        else {
            if(mIsTablet) {
                mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map_market);
                mapFragment.getMapAsync(this);
            }
        }

    }

    @Override
    public void onMarketSelected(Market market) {
        if(!mIsTablet) {
            Intent intent = new Intent(this, MarketDetailActivity.class);
            intent.putExtra(MARKET_EXTRA, market);
            startActivity(intent);
        }
        else {
            mViewModel.setMarketSelection(market);
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_market);
            mapFragment.getMapAsync(this);

            DetailFragment detailFragment =
                    DetailFragment.newInstance(market);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, detailFragment)
                    .commit();
        }

    }

    private boolean isValidLocation(double lat, double lng) {
        if(lat < -90 || lat > 90)
            return false;
        if(lng < -180 || lng > 180)
            return false;
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Market market = mViewModel.getMarketSelection();
        LatLng latLng;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        MarkerOptions options = new MarkerOptions();
        double lat = Double.valueOf(market.getLatitude());
        double lng = Double.valueOf(market.getLongitude());
        if (isValidLocation(lat, lng)) {
            latLng = new LatLng(lat, lng);
            options.position(latLng);
            options.title(market.getMarketname());
            googleMap.addMarker(options);
            builder.include(options.getPosition());

            final LatLngBounds bounds = builder.build();
            googleMap.setLatLngBoundsForCameraTarget(bounds);
            googleMap.setMinZoomPreference(14.0f);
        }
    }

    @Override
    public void onSwitchMapView() {
        Intent intent = new Intent(this, MarketMapActivity.class);
        intent.putExtra(BAR_TITLE_EXTRA, mActionBarTitle);
        startActivity(intent);
    }

    @Override
    public void onSwitchListView() {
        //do nothing
    }
}
