package com.udacity.ak.localroots.ui.fragment;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.TooltipCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.ak.localroots.BuildConfig;
import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;
import com.udacity.ak.localroots.data.repository.MarketRepository;
import com.udacity.ak.localroots.utilities.AppConstants;
import com.udacity.ak.localroots.viewmodel.DetailViewModelFactory;
import com.udacity.ak.localroots.viewmodel.MarketDetailViewModel;
import com.udacity.ak.localroots.widget.LocalRootsWidget;

import butterknife.BindBool;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class DetailFragment extends Fragment {

    private static final String ARG_MARKET = "arg_market";
    private static final int tag_favorite = 1;
    private static final int tag_not_favorite = 0;

    @BindBool(R.bool.is_tablet) boolean mIsTablet;
    @BindString(R.string.blank_detail) String mTextNoInformation;
    @BindView(R.id.tv_marketname) TextView mMarketnameView;
    @BindView(R.id.tv_address) TextView mAddressView;
    @BindView(R.id.tv_schedule) TextView mScheduleView;
    @BindView(R.id.tv_products) TextView mProductsView;
    @BindView(R.id.fab_favorite) FloatingActionButton mFavoriteButton;
    @BindString(R.string.favorite_button_tooltip) String mFavoriteTooltop;
    @BindString(R.string.unfavorite_button_tooltip) String mUnFavoriteTooltop;

    private Market mMarket;
    private MarketDetailViewModel mViewModel;
    private MarketRepository mRepository;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Market market) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MARKET, market);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMarket = getArguments().getParcelable(ARG_MARKET);
        }

        // Set up view model
        mRepository = MarketRepository.getInstance(this.getActivity().getApplication());
        DetailViewModelFactory factory = new DetailViewModelFactory(mRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(MarketDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        if(mMarket == null) return view;

        mViewModel.getFavoriteMarketIds().observe(this, favIdList -> {
            boolean isFavorite = false;

            if(favIdList!=null && favIdList.contains(mMarket.getId())) {
                isFavorite = true;
            }
            else {
                isFavorite = false;
            }
            updateFavoriteButton(isFavorite);

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if((Integer)mFavoriteButton.getTag() == tag_favorite)//remove from favorites
                    {
                        updateFavoriteStatus(false);
                    }
                    else if((Integer)mFavoriteButton.getTag() == tag_not_favorite) //add to favorites
                    {
                        updateFavoriteStatus(true);
                    }
                }
            });

            populateUI();
            updateWidgets();
        });

        return view;
    }

    private void updateFavoriteStatus(boolean isFavorite) {
        if(isFavorite) {
            mViewModel.addFavorite(mMarket);
        }
        else {
            mViewModel.removeFavorite(mMarket.getId());
        }
        updateFavoriteButton(isFavorite);
    }

    private void updateFavoriteButton(boolean isFavorite) {
        if(isFavorite) {
            mFavoriteButton.setImageDrawable(ContextCompat.getDrawable(this.getActivity(), android.R.drawable.btn_star_big_on));
            mFavoriteButton.setTag(tag_favorite);
            if (Build.VERSION.SDK_INT >= 26) {
                mFavoriteButton.setTooltipText(mUnFavoriteTooltop);
            }
            else {
                TooltipCompat.setTooltipText(mFavoriteButton, mUnFavoriteTooltop);
            }
        }
        else {
            mFavoriteButton.setImageDrawable(ContextCompat.getDrawable(this.getActivity(), android.R.drawable.btn_star_big_off));
            mFavoriteButton.setTag(tag_not_favorite);
            if (Build.VERSION.SDK_INT >= 26) {
                mFavoriteButton.setTooltipText(mFavoriteTooltop);
            }
            else {
                TooltipCompat.setTooltipText(mFavoriteButton, mFavoriteTooltop);
            }
        }
    }

    private void updateWidgets() {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);

        sharedPreferences
                .edit()
                .putString(AppConstants.SP_KEY_MARKET_NAME, mMarket.getMarketname())
                .putString(AppConstants.SP_KEY_MARKET_SCHEDULE, mMarket.getSchedule())
                .apply();

        //Update the Widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), LocalRootsWidget.class));
        LocalRootsWidget localRootsWidget = new LocalRootsWidget();
        localRootsWidget.onUpdate(getActivity(), appWidgetManager, ids);
    }

    private void populateUI() {
        mMarketnameView.setText(mMarket.getMarketname());
        mAddressView.setText(replaceEmptyValue(mMarket.getAddress()));

        //Remove all html tags and separate multiple schedules by <br>
        String strSchedule = "";
        if(strSchedule != null) {
            strSchedule = mMarket.getSchedule()
                    .replaceAll("\\<.*?\\>", "")
                    .replace(";", "<br>").trim();
            //remove trailing <br>
            if (strSchedule.endsWith("<br>")) {
                strSchedule = strSchedule.substring(0, strSchedule.length() - 4);
            }
        }
        mScheduleView.setText(Html.fromHtml(replaceEmptyValue(strSchedule)));

        //Products
        String strProductList = replaceEmptyValue(mMarket.getProducts())
                .replace(";", "<br>");
        mProductsView.setText(Html.fromHtml(strProductList));
    }

    private String replaceEmptyValue(String value)
    {
        if(value==null || value.trim().equals(""))
            return mTextNoInformation;

        return value;
    }

}
