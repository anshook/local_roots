package com.udacity.ak.localroots.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.ui.activity.MainActivity;
import com.udacity.ak.localroots.ui.activity.MarketListActivity;
import com.udacity.ak.localroots.utilities.InternetCheck;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class SearchFragment extends Fragment
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.btn_search_by_loc) Button mButtonSearchByLoc;
    @BindView(R.id.btn_search_by_zip) ImageButton mButtonSearchByZip;
    @BindView(R.id.et_zipcode) EditText mTextZipcode;
    @BindString(R.string.no_connection) String mStringNoInternet;
    @BindString(R.string.error_invalid_zipcode) String mStringInvalidZipcode;
    @BindString(R.string.loc_permission_denied) String mStringLocPermissionDenied;
    @BindString(R.string.loc_permission_granted) String mStringLocPermissionGranted;

    private MarketRepository marketRepository;
    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude;
    private double mLongitude;
    private static final int LOCATION_REQUEST_CODE = 1;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marketRepository = MarketRepository.getInstance(this.getActivity().getApplication());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        mButtonSearchByZip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isValidZipCode()){
                    lookupMarketsByZipcode();
                }
            }
        });

        mButtonSearchByLoc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean permissionGranted = ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (permissionGranted) {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                            lookupMarketsByLocation();
                        }
                        else {
                            buildLocationCallback();
                        }
                    });
                }
                else //Permission is missing
                {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }
        });

        return view;
    }

    private void buildLocationCallback() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(location != null){
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        return;
                    }
                }
            };
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Snackbar.make(this.getView(), mStringLocPermissionGranted, Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    Snackbar.make(this.getView(), mStringLocPermissionDenied, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            }
        }
    }

    private Boolean isValidZipCode(){
        int zipcodeLength = getResources().getInteger(R.integer.zipcode_length);
        if( mTextZipcode.getText().toString().length() < zipcodeLength ) {
            mTextZipcode.setError(mStringInvalidZipcode);
            return false;
        }
        return true;
    }

    private void lookupMarketsByZipcode() {
        new InternetCheck((Boolean internet) -> {
            if (!internet) {
                Snackbar.make(this.getView(), mStringNoInternet, Snackbar.LENGTH_LONG)
                        .show();
            } else {
                marketRepository.loadNearbyMarkets(mTextZipcode.getText().toString());
                launchMarketListActivity(mTextZipcode.getText().toString());
            }
        });
    }

    private void lookupMarketsByLocation() {
        new InternetCheck((Boolean internet) -> {
            if (!internet) {
                Snackbar.make(this.getView(), mStringNoInternet, Snackbar.LENGTH_LONG)
                        .show();
            } else {
                marketRepository.loadNearbyMarkets(mLatitude, mLongitude);
                launchMarketListActivity("");
            }
        });
    }

    private void launchMarketListActivity(String area) {
        Intent intent = new Intent(this.getActivity(), MarketListActivity.class);
        intent.putExtra(MainActivity.SEARCH_EXTRA, area);
        startActivity(intent);
    }

}
