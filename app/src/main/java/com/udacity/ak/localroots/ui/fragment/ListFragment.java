package com.udacity.ak.localroots.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.ui.activity.MarketListActivity;
import com.udacity.ak.localroots.ui.activity.MarketMapActivity;
import com.udacity.ak.localroots.ui.adapter.MarketAdapter;
import com.udacity.ak.localroots.viewmodel.ListViewModelFactory;
import com.udacity.ak.localroots.viewmodel.MarketListViewModel;


import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {
    private MarketListViewModel mViewModel;
    private MarketAdapter mMarketAdapter;
    private MarketRepository mRepository;

    @BindView(R.id.fab_map) FloatingActionButton mFabMap;
    @BindView(R.id.rv_markets) RecyclerView mMarketsRecyclerView;
    @BindBool(R.bool.is_tablet) boolean mIsTablet;

    // Define a new interface OnMarketClickListener that triggers a callback in the host activity
    private OnMarketClickListener mCallback;

    // OnMarketClickListener interface, calls a method in the host activity named onMarketSelected
    public interface OnMarketClickListener {
        void onMarketSelected(Market market);
    }

    private OnViewTypeClickListener mViewTypeCallback;

    public interface OnViewTypeClickListener {
        void onSwitchMapView();
    }

    // Override onAttach to make sure that the container activity has implemented the callback(s)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnMarketClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMarketClickListener");
        }

        try {
            mViewTypeCallback = (OnViewTypeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewTypeClickListener");
        }
    }

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
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
        View view =  inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        mViewModel.getMarketListForDisplay().observe(this, marketList -> {
            mMarketAdapter = new MarketAdapter(marketList,
                    new MarketAdapter.MarketClickListener() {
                        @Override
                        public void onClick(int itemIndex) {
                            mCallback.onMarketSelected(marketList.get(itemIndex));
                        }
                    });
            mMarketsRecyclerView.setAdapter(mMarketAdapter);
            mMarketsRecyclerView.setHasFixedSize(true);
            mMarketsRecyclerView.setNestedScrollingEnabled(false);
            setupFabForMapView();
        });

        return view;
    }

    private void setupFabForMapView() {
        if(!mIsTablet)
            mFabMap.setVisibility(View.VISIBLE);
        mFabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewTypeCallback.onSwitchMapView();
            }
        });
    }
}
