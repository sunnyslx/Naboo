package com.idx.naboo.takeout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.idx.naboo.R;

/**
 * 外卖条目 ViewHolder
 * Created by danny on 4/16/18.
 */

public class TakeoutSellerViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public TextView mSort;
    public ImageView mFood;
    public TextView mFoodName;
    public TextView mPrice;
    public TextView mCount;
    public ImageButton mReduce;
    public ImageButton mAdd;

    public TakeoutSellerViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        mSort= mView.findViewById(R.id.takeout_seller_item_index);
        mFood= mView.findViewById(R.id.takeout_seller_item_food);
        mFoodName= mView.findViewById(R.id.takeout_seller_item_food_name);
        mPrice= mView.findViewById(R.id.takeout_seller_item_price);
        mCount= mView.findViewById(R.id.takeout_seller_item_count);
        mReduce=mView.findViewById(R.id.takeout_seller_item_reduce);
        mAdd=mView.findViewById(R.id.takeout_seller_item_add);
    }
}
