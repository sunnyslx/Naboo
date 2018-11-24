package com.idx.naboo.takeout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.idx.naboo.R;
import com.idx.naboo.takeout.data.item.MenuList;
import com.idx.naboo.takeout.data.item.TakeoutMenu;
import com.idx.naboo.takeout.data.room.TakeoutDataSource;
import com.idx.naboo.takeout.data.room.TakeoutInjection;
import com.idx.naboo.takeout.data.room.TakeoutRepository;
import com.idx.naboo.takeout.data.room.TakeoutSeller;
import com.idx.naboo.utils.RoundImageUtils;

import java.util.List;
import java.util.Map;

/**
 * 外卖条目 Adapter
 * Created by danny on 4/16/18.
 */

public class TakeoutSellerAdapter extends RecyclerView.Adapter<TakeoutSellerViewHolder> {
    private static final String TAG=TakeoutSellerAdapter.class.getSimpleName();
    private Context mContext;
    private List<TakeoutMenu> mMenus;
    private String mSellerName;
    private List<MenuList> mItems;
    private OnItemClickListener mOnItemClickListener;
    private OnItemIncrementAndReduceListener mOnItemIncrementAndReduceListener;
    private TakeoutRepository mRepository;
    private static long lastTimeMillis;
    private static final long MIN_CLICK_INTERVAL = 1000;

    public void setItems(List<MenuList> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public TakeoutSellerAdapter(Context context, List<TakeoutMenu> takeoutMenus, String sellerName/*,List<MenuList> items*/) {
        mContext=context;
        mRepository = TakeoutInjection.getInstance(context);
        mMenus = takeoutMenus;
        mSellerName=sellerName;
//        mItems = items;
    }

    @Override
    public TakeoutSellerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TakeoutSellerViewHolder holder=new TakeoutSellerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.takeout_seller_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final TakeoutSellerViewHolder holder, final int position) {
        holder.mView.setTag(position);
        holder.mAdd.setTag(position);
        holder.mReduce.setTag(position);
        TakeoutMenu tom = mMenus.get(position);
        holder.mSort.setText(position + 1 + "");
        final String imageUrl=tom.getImage_url();
//        holder.mFood.setImageUrl(imageUrl);

        Glide.with(mContext).load(imageUrl)
                .transform(new RoundImageUtils(mContext, 10))
                .error(R.mipmap.takeout_order_item_food)
                .into(holder.mFood);

        final String foodName=tom.getName();
        holder.mFoodName.setText(foodName);
        final double price = tom.getPrice();
        holder.mPrice.setText("￥" + price);
        final double packing_fee=tom.getPacking_fee();

        final int activity_max_quantity=tom.getActivity_max_quantity();
        final double old_price=tom.getOriginal_price();
        holder.mReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "减去一件商品名字为: "+foodName);
                mRepository.findFood(foodName, new TakeoutDataSource.LoadSellerCallback() {
                    @Override
                    public void onSuccess(TakeoutSeller seller) {
                        Log.d(TAG, "已买过该商品减去一件: "+foodName);
                        int count=seller.count;
                        if (count >= 1) {
                            --count;
                            if (count > 0) {
                                holder.mCount.setVisibility(View.VISIBLE);
                                holder.mReduce.setVisibility(View.VISIBLE);
                                holder.mCount.setText(count + "");
                                mRepository.updateFoodCount(-1, foodName,null);
                            } else {
                                mRepository.deleteFood(foodName,null);
                                holder.mCount.setVisibility(View.INVISIBLE);
                                holder.mReduce.setVisibility(View.INVISIBLE);
                            }
                        }
                        mOnItemIncrementAndReduceListener.reduce();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "这是个bug,等下改");
                        Toast.makeText(mContext,"点击无效!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "添加一件商品名字为: "+foodName);
                mRepository.findFood(foodName, new TakeoutDataSource.LoadSellerCallback() {
                    @Override
                    public void onSuccess(TakeoutSeller seller) {
                        Log.d(TAG, "已经购买该商品再加一份: "+seller.food_name);
                        int count=seller.count;
                        if (count<300) {
                            ++count;
                            if (count >= 0) {
                                holder.mReduce.setVisibility(View.VISIBLE);
                                holder.mCount.setVisibility(View.VISIBLE);
                            }
                            holder.mCount.setText(count + "");
                            mRepository.updateFoodCount(1, foodName, null);

                            mOnItemIncrementAndReduceListener.increment();
                        }else {
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.takeout_add_more),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "还未购买该商品买一份: "+foodName);
                        //还未在该商家购买该商品，存数据库
                        TakeoutSeller seller=new TakeoutSeller();
                        seller.seller_name=mSellerName;
                        seller.food_name=foodName;
                        seller.food_image=imageUrl;
                        seller.count=1;
                        seller.price=price;
                        seller.packing_fee=packing_fee;
                        seller.old_price=old_price;
                        seller.activity_max_quantity=activity_max_quantity;
                        mRepository.insertTakeoutSeller(seller,null);

                        holder.mReduce.setVisibility(View.VISIBLE);
                        holder.mCount.setVisibility(View.VISIBLE);
                        holder.mCount.setText(1 + "");

                        mOnItemIncrementAndReduceListener.increment();
                    }
                });
            }
        });
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTimeEnabled()) {
                        int position = (int) holder.mView.getTag();
                        mOnItemClickListener.onClick(position);
                    }
                }
            });
        }

        if (mItems!=null && mItems.size()==mMenus.size()) {
            Map<Integer,Integer> map=mItems.get(position).getMap();
            if (map.get(1) != null && map.get(1) != 0) {
                holder.mCount.setVisibility(View.VISIBLE);
                holder.mReduce.setVisibility(View.VISIBLE);
                holder.mCount.setText(map.get(1) + "");
            } else {
                holder.mReduce.setVisibility(View.INVISIBLE);
                holder.mCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {return mMenus.size();}

    protected boolean isTimeEnabled() {
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - lastTimeMillis) > MIN_CLICK_INTERVAL) {
            lastTimeMillis = currentTimeMillis;
            return true;
        }
        return false;
    }

    //条目点击监听
    public interface OnItemClickListener {
        void onClick(int position);
//        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {this.mOnItemClickListener = onItemClickListener;}

    //数量点击监听
    public interface OnItemIncrementAndReduceListener {
        void reduce();

        void increment();
    }

    public void setOnItemIncrementAndReduceListener(OnItemIncrementAndReduceListener listener) {this.mOnItemIncrementAndReduceListener = listener;}
}
