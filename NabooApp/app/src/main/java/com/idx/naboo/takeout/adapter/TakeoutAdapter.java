package com.idx.naboo.takeout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.idx.naboo.R;
import com.idx.naboo.takeout.data.takeout.TakeoutShop;
import com.idx.naboo.utils.RoundImageUtils;

import java.util.List;

/**
 * 外卖 Adapter
 * Created by danny on 4/16/18.
 */

public class TakeoutAdapter extends RecyclerView.Adapter<TakeoutViewHorder> {
    private List<TakeoutShop> mTakeoutShops;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public TakeoutAdapter(Context context,List<TakeoutShop> takeoutShops) {
        mContext=context;
        mTakeoutShops = takeoutShops;
    }

    @Override
    public TakeoutViewHorder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TakeoutViewHorder(LayoutInflater.from(parent.getContext()).inflate(R.layout.takeout_item,parent,false));
    }

    @Override
    public void onBindViewHolder(TakeoutViewHorder holder, final int position) {
        TakeoutShop tos=mTakeoutShops.get(position);
        holder.mSort.setText(position+1+"");
//        holder.mFood.setImageUrl(tos.getImageUrl());

        Glide.with(mContext).load(tos.getImageUrl())
                .transform(new RoundImageUtils(mContext, 10))
                .error(R.mipmap.takeout_order_item_food)
                .into(holder.mFood);

        holder.mSeller.setText(tos.getRestaurantName());
        holder.mDeliverSpent.setText(tos.getDeliverSpent()+" 分钟");
        holder.mStar.setRating((int)tos.getRestaurantRating());
        if (mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {return mTakeoutShops.size();}

    public interface OnItemClickListener{
        void onClick( int position);
//        void onLongClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }
}
