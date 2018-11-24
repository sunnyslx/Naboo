package com.idx.naboo.takeout.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.idx.naboo.R;
import com.idx.naboo.takeout.data.room.TakeoutDataSource;
import com.idx.naboo.takeout.data.room.TakeoutInjection;
import com.idx.naboo.takeout.data.room.TakeoutRepository;
import com.idx.naboo.takeout.data.room.TakeoutSeller;
import com.idx.naboo.utils.RoundImageUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 购物车 Adapter
 *
 * Created by danny on 4/24/18.
 */


public class TakeoutCarAdapter extends BaseAdapter {
    private static final String TAG = TakeoutCarAdapter.class.getSimpleName();
    private Context mContext;
    private TakeoutRepository mRepository;
    private List<TakeoutSeller> mSellers;
    private OnItemIncrementAndReduceListener mOnItemIncrementAndReduceListener;
    private int c1;
    private int c2;

    public TakeoutCarAdapter(Context context, List<TakeoutSeller> sellers) {
        mContext=context;
        mRepository= TakeoutInjection.getInstance(context);
        mSellers = sellers;
    }

    @Override
    public int getCount() {return mSellers.size();}

    @Override
    public Object getItem(int position) {return mSellers.get(position);}

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TakeoutSeller takeoutSeller=mSellers.get(position);
        final Holder holder;
        if (convertView == null) {
            //引入ViewHolder提升ListView的效率
            holder = new Holder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.takeout_car_item, parent, false);

            holder.mIndex = convertView.findViewById(R.id.takeout_car_item_index);
            holder.mFood = convertView.findViewById(R.id.takeout_car_item_food);
            holder.mFoodName = convertView.findViewById(R.id.takeout_car_item_food_name);
            holder.mReduce = convertView.findViewById(R.id.takeout_car_item_reduce);
            holder.mCount = convertView.findViewById(R.id.takeout_car_item_count);
            holder.mIncrement = convertView.findViewById(R.id.takeout_car_item_increment);
            holder.mPrice = convertView.findViewById(R.id.takeout_car_item_price);
            holder.mOldPrice = convertView.findViewById(R.id.takeout_car_item_old_price);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        //赋值
        holder.mIndex.setText(position+1+"");
//        holder.mFood.setImageUrl(seller.food_image);

        Glide.with(mContext).load(takeoutSeller.food_image)
                .transform(new RoundImageUtils(mContext, 10))
                .error(R.mipmap.takeout_order_item_food)
                .into(holder.mFood);

        final String foodName=takeoutSeller.food_name;
        holder.mFoodName.setText(foodName);
        holder.mReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "减去一件商品，当商品数量为0时，移除条目: "+position);
                mRepository.findFood(foodName, new TakeoutDataSource.LoadSellerCallback() {
                    @Override
                    public void onSuccess(TakeoutSeller seller) {
                        c1=0;
                        Log.d(TAG, "已买过该商品减去一件: "+foodName);
                        int count=seller.count;
                        if (count >= 1) {
                            --count;
                            c1=count;
                            if (count > 0) {
                                Log.d(TAG, "onSuccess: 已买过该商品减去一件");
                                holder.mCount.setVisibility(View.VISIBLE);
                                holder.mReduce.setVisibility(View.VISIBLE);
                                holder.mCount.setText(count + "");
                                mRepository.updateFoodCount(-1, foodName,null);
                            } else {
                                Log.d(TAG, "onSuccess: 不买该商品了");
                                mRepository.deleteFood(foodName,null);
                                if (mOnItemIncrementAndReduceListener!=null){mOnItemIncrementAndReduceListener.remove(position);}
                            }
                        }
                        takeoutSeller.count=count;
                    }

                    @Override
                    public void onError() {}
                });
                if (mOnItemIncrementAndReduceListener!=null){
                    Log.d(TAG, "onClick: "+c1--+takeoutSeller.activity_max_quantity);
                    if (takeoutSeller.activity_max_quantity!=0) {
                        if (c1 < takeoutSeller.activity_max_quantity) {
                            Log.d(TAG, "onClick: "+takeoutSeller.price);
                            mOnItemIncrementAndReduceListener.reduce(takeoutSeller.price, takeoutSeller.packing_fee);
                        } else {
                            Log.d(TAG, "onClick: "+takeoutSeller.old_price);
                            mOnItemIncrementAndReduceListener.reduce(takeoutSeller.old_price, takeoutSeller.packing_fee);
                        }
                    }else {
                        Log.d(TAG, "onClick: "+takeoutSeller.price);
                        mOnItemIncrementAndReduceListener.reduce(takeoutSeller.price, takeoutSeller.packing_fee);
                    }
                }
            }
        });
        int count=takeoutSeller.count;
        holder.mCount.setText(count+"");
        holder.mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepository.findFood(foodName, new TakeoutDataSource.LoadSellerCallback() {
                    @Override
                    public void onSuccess(TakeoutSeller seller) {
                        c2=0;
                        Log.d(TAG, "已经购买该商品再加一份: "+seller.food_name);
                        int count=seller.count;
                        if (count<300) {
                            ++count;
                            c2 = count;
                            if (count >= 0) {
                                holder.mReduce.setVisibility(View.VISIBLE);
                                holder.mCount.setVisibility(View.VISIBLE);
                            }
                            holder.mCount.setText(count + "");
                            mRepository.updateFoodCount(1, foodName, null);
                            takeoutSeller.count = count;
                        }else {
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.takeout_add_more),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {}
                });
                if (mOnItemIncrementAndReduceListener!=null){
                    Log.d(TAG, "onClick: "+c1--+takeoutSeller.activity_max_quantity);
                    if (c2!=0) {
                        if (takeoutSeller.activity_max_quantity != 0) {
                            if (c2 < takeoutSeller.activity_max_quantity) {
                                Log.d(TAG, "onClick: " + takeoutSeller.price);
                                mOnItemIncrementAndReduceListener.increment(takeoutSeller.price, takeoutSeller.packing_fee);
                            } else {
                                Log.d(TAG, "onClick: " + takeoutSeller.old_price);
                                mOnItemIncrementAndReduceListener.increment(takeoutSeller.old_price, takeoutSeller.packing_fee);
                            }
                        } else {
                            Log.d(TAG, "onClick: " + takeoutSeller.price);
                            mOnItemIncrementAndReduceListener.increment(takeoutSeller.price, takeoutSeller.packing_fee);
                        }
                    }else {
                        Toast.makeText(mContext,mContext.getResources().getString(R.string.takeout_add_more),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        double price=takeoutSeller.price;
        double old_price=takeoutSeller.old_price;
        DecimalFormat df = new DecimalFormat("######0.00");
//        double realPrice=count*price;
        if (old_price!=0) {
            holder.mOldPrice.setVisibility(View.VISIBLE);
            holder.mPrice.setText("￥" + df.format(price));
            holder.mOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mOldPrice.setText("第"+(takeoutSeller.activity_max_quantity+1)+"份起￥" + df.format(old_price));
        }else {
            holder.mOldPrice.setVisibility(View.GONE);
            holder.mPrice.setText("￥" + df.format(price));
        }
        return convertView;
    }

    class Holder{
        TextView mIndex;
        ImageView mFood;
        TextView mFoodName;
        ImageButton mReduce;
        TextView mCount;
        ImageButton mIncrement;
        TextView mPrice;
        TextView mOldPrice;
    }

    //数量点击监听
    public interface OnItemIncrementAndReduceListener {
        void reduce(double price, double package_fee);

        void increment(double price, double package_fee);

        void remove(int position);
    }

    public void setOnItemIncrementAndReduceListener(OnItemIncrementAndReduceListener listener) {this.mOnItemIncrementAndReduceListener = listener;}
}
