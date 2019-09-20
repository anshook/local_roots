package com.udacity.ak.localroots.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.ui.fragment.DetailFragment;

import butterknife.BindDimen;
import butterknife.ButterKnife;

public class MarketDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Market mMarket;

    @BindDimen(R.dimen.map_padding) int mapPadding;
    MapFragment mapFragment;
    String mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);
        ButterKnife.bind(this, this);

        if(getIntent().hasExtra(MarketListActivity.MARKET_EXTRA)) {
            mMarket = (Market) getIntent().getParcelableExtra(MarketListActivity.MARKET_EXTRA);
        }

        if(mMarket != null)
            getSupportActionBar().setTitle(mMarket.getMarketname());

        if(savedInstanceState == null) {

            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_market);
            mapFragment.getMapAsync(this);

            DetailFragment detailFragment =
                    DetailFragment.newInstance(mMarket);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, detailFragment)
                    .commit();
        }

        if(savedInstanceState != null) {
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_market);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);

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
        LatLng latLng;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        MarkerOptions options = new MarkerOptions();
        double lat = Double.valueOf(mMarket.getLatitude());
        double lng = Double.valueOf(mMarket.getLongitude());
        if (isValidLocation(lat, lng)) {
            latLng = new LatLng(lat, lng);
            options.position(latLng);
            options.title(mMarket.getMarketname());
            googleMap.addMarker(options);
            builder.include(options.getPosition());

            final LatLngBounds bounds = builder.build();
            googleMap.setLatLngBoundsForCameraTarget(bounds);
            googleMap.setMinZoomPreference(14.0f);
        }
    }
}
