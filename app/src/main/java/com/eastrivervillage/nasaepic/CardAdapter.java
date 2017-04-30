package com.eastrivervillage.nasaepic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by kannanba on 4/30/2017.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context context;
    private List<CardData> cardDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, lat;
        public ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.tv_date);
            lat = (TextView) itemView.findViewById(R.id.tv_lat);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
        }
    }

    public CardAdapter (Context context, List<CardData> cardDataList) {
        this.context = context;
        this.cardDataList = cardDataList;
    }

    @Override
    public CardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardAdapter.MyViewHolder holder, int position) {
        CardData cardData = cardDataList.get(position);

        holder.date.setText(cardData.date);
        holder.lat.setText(cardData.lat);
        Glide.with(context).load(cardData.thumbnail).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return cardDataList.size();
    }
}
