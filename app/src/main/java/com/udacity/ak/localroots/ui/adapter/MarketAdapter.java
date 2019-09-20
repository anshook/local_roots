package com.udacity.ak.localroots.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    private List<Market> mMarketList;
    private MarketClickListener mClickListener;

    public MarketAdapter(@NonNull List<Market> marketList, MarketClickListener clickListener){
        this.mMarketList = marketList;
        this.mClickListener = clickListener;
    }

    public interface MarketClickListener {
        abstract void onClick(int itemIndex);
    }

    public void setClickListener(MarketClickListener marketClickListener) {
        this.mClickListener = marketClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Market market=mMarketList.get(position);
        String marketName = market.getMarketname();
        holder.mMarketNameTextView.setText(marketName);
        String miles = market.getMiles();
        holder.mMilesTextView.setText(miles + " " + holder.mStringMiles);
    }

    @Override
    public int getItemCount() {
        return mMarketList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_name) TextView mMarketNameTextView;
        @BindView(R.id.tv_miles) TextView mMilesTextView;
        @BindString(R.string.text_miles) String mStringMiles;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mClickListener.onClick(getAdapterPosition());
        }

    }
}
