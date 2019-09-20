package com.udacity.ak.localroots.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.ui.activity.MarketDetailActivity;
import com.udacity.ak.localroots.ui.activity.MarketListActivity;
import com.udacity.ak.localroots.ui.adapter.FavoriteAdapter;
import com.udacity.ak.localroots.viewmodel.FavoritesViewModel;
import com.udacity.ak.localroots.viewmodel.FavoritesViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment {

    @BindView(R.id.rv_favorites) RecyclerView mFavoritesRecyclerView;
    @BindView(R.id.tv_fav_title) TextView mFragmentTitle;

    private FavoritesViewModel mViewModel;
    private MarketRepository mRepository;
    private FavoriteAdapter mAdapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view model
        mRepository = MarketRepository.getInstance(this.getActivity().getApplication());
        FavoritesViewModelFactory factory = new FavoritesViewModelFactory(mRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(FavoritesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_favorites, container, false);
       ButterKnife.bind(this, view);

       mViewModel.getFavoriteMarketList().observe(this, marketList -> {
           mAdapter = new FavoriteAdapter(marketList,
                   new FavoriteAdapter.MarketClickListener() {
                       @Override
                       public void onClick(int itemIndex) {
                           //Market Detail Activity
                           Intent intent = new Intent(getActivity(), MarketDetailActivity.class);
                           intent.putExtra(MarketListActivity.MARKET_EXTRA, marketList.get(itemIndex));
                           startActivity(intent);
                       }

                       @Override
                       public void buttonOnClick(View v, int position) {
                           //delete favorite
                           String removeId =  marketList.get(position).getId();
                           mViewModel.removeFavorite(marketList.get(position).getId());

                       }
                   });
           if(mAdapter.getItemCount()>0) {
               mFragmentTitle.setVisibility(View.VISIBLE);
           }
           else {
               mFragmentTitle.setVisibility(View.GONE);
           }

           mFavoritesRecyclerView.setAdapter(mAdapter);
           mFavoritesRecyclerView.setHasFixedSize(true);
           mFavoritesRecyclerView.setNestedScrollingEnabled(false);

       });

       return view;
    }


}
