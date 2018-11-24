package com.idx.naboo.takeout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.idx.naboo.R;

/**
 * 外卖 ViewHolder
 * Created by danny on 4/16/18.
 */

public class TakeoutViewHorder extends RecyclerView.ViewHolder {
    public View mView;
    public TextView mSort;
    public ImageView mFood;
    public TextView mSeller;
    public RatingBar mStar;
    public TextView mDeliverSpent;

    public TakeoutViewHorder(View itemView) {
        super(itemView);
        mView=itemView;
        mSort= mView.findViewById(R.id.takeout_item_index);
        mFood= mView.findViewById(R.id.takeout_item_food);
        mSeller= mView.findViewById(R.id.takeout_item_seller);
        mDeliverSpent= mView.findViewById(R.id.takeout_item_deliver_spent);
        mStar= mView.findViewById(R.id.takeout_item_star);
    }
}
