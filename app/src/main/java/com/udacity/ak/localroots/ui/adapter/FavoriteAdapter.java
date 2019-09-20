package com.udacity.ak.localroots.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.data.database.Market;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<Market> mMarketList;
    private MarketClickListener mClickListener;

    public FavoriteAdapter(@NonNull List<Market> marketList, MarketClickListener clickListener){
        this.mMarketList = marketList;
        this.mClickListener = clickListener;
    }

    public interface MarketClickListener {
        abstract void onClick(int itemIndex);
        void buttonOnClick(View v, int position);
    }

    public void setClickListener(MarketClickListener marketClickListener) {
        this.mClickListener = marketClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Market market=mMarketList.get(position);
        String marketName = market.getMarketname();
        holder.mFavNameTextView.setText(marketName);
    }

    @Override
    public int getItemCount() {
        return mMarketList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_favorite_name) TextView mFavNameTextView;
        @BindView(R.id.iv_del_favorite) ImageView mImageDeleteFav;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            mImageDeleteFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.buttonOnClick(v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View itemView) {
            //button clicked
            if(itemView.getId() == mImageDeleteFav.getId()) {
                return;
            }
            mClickListener.onClick(getAdapterPosition());
        }

    }
}
