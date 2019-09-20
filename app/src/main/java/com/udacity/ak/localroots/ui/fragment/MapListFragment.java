package com.udacity.ak.localroots.ui.fragment;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.ui.activity.MarketListActivity;
import com.udacity.ak.localroots.viewmodel.ListViewModelFactory;
import com.udacity.ak.localroots.viewmodel.MarketListViewModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindBool;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MapListFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.fab_list) FloatingActionButton mFabList;
    @BindBool(R.bool.is_tablet) boolean mIsTablet;
    @BindDimen(R.dimen.map_padding) int mapPadding;
    MapFragment mapFragment;

    private MarketListViewModel mViewModel;
    private MarketRepository mRepository;

    private LatLng latLng;
    List<Market> mMarketList;
    private HashMap<Integer, MarkerOptions> mMarkerOptionsList;

    // Define a new interface OnMarketClickListener that triggers a callback in the host activity
    private MapListFragment.OnMarketClickListener mCallback;

    // OnMarketClickListener interface, calls a method in the host activity named onMarketSelected
    public interface OnMarketClickListener {
        void onMarketSelected(Market market);
    }

    private MapListFragment.OnViewTypeClickListener mViewTypeCallback;

    public interface OnViewTypeClickListener {
        void onSwitchListView();
    }

    // Override onAttach to make sure that the container activity has implemented the callback(s)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (MapListFragment.OnMarketClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMarketClickListener");
        }

        try {
            mViewTypeCallback = (MapListFragment.OnViewTypeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewTypeClickListener");
        }
    }

    public MapListFragment() {
        // Required empty public constructor
    }

    public static MapListFragment newInstance() {
        MapListFragment fragment = new MapListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view model
        mRepository = MarketRepository.getInstance(this.getActivity().getApplication());
        ListViewModelFactory factory = new ListViewModelFactory(mRepository);
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(MarketListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mViewModel.getMarketListForDisplay().observe(this, marketList -> {
            mMarketList = marketList;
            mMarkerOptionsList = getMarkerList(mMarketList);
            mapFragment = (MapFragment) this.getActivity().getFragmentManager().findFragmentById(R.id.map_marketlist);
            mapFragment.getMapAsync(this);

            setupFabForListView();
        });

        return view;
    }

    private HashMap<Integer,MarkerOptions> getMarkerList(List<Market> marketList) {
        HashMap<Integer,MarkerOptions> markerOptionsList = new HashMap<Integer,MarkerOptions>();

        Iterator iterator = marketList.iterator();
        Integer index = -1;
        while(iterator.hasNext()) {
            index = index + 1;
            Market market = (Market)iterator.next();
            MarkerOptions options = new MarkerOptions();
            double lat = Double.valueOf(market.getLatitude());
            double lng = Double.valueOf(market.getLongitude());
            if(isValidLocation(lat, lng)) {
                latLng = new LatLng(lat, lng);
                options.position(latLng);
                options.title(market.getMarketname());
                markerOptionsList.put(index,options);
            }
        }
        return markerOptionsList;
    }

    private boolean isValidLocation(double lat, double lng) {
        if(lat < -90 || lat > 90)
            return false;
        if(lng < -180 || lng > 180)
            return false;
        return true;
    }

    private void setupFabForListView() {
        if(!mIsTablet)
            mFabList.setVisibility(View.VISIBLE);
        mFabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewTypeCallback.onSwitchListView();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        HashMap.Entry<Integer, MarkerOptions> entry;
        MarkerOptions options;
        Integer tag;
        Iterator<HashMap.Entry<Integer, MarkerOptions>> itr = mMarkerOptionsList.entrySet().iterator();
        LatLngBounds.Builder builder = LatLngBounds.builder();
        while(itr.hasNext()) {
            entry = itr.next();
            options = (MarkerOptions)entry.getValue();
            tag = entry.getKey();
            googleMap.addMarker(options).setTag(tag);
            builder.include(options.getPosition());
        }
        if(mMarkerOptionsList.entrySet().size() > 0) {
            final LatLngBounds bounds = builder.build();

            boolean permissionGranted = ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (permissionGranted) {
                googleMap.setMyLocationEnabled(true);
            }
            try {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, mapPadding));
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int itemIndex = ((Integer)marker.getTag()).intValue();
                mCallback.onMarketSelected(mMarketList.get(itemIndex));

                return false;
            }
        });
      }
}
