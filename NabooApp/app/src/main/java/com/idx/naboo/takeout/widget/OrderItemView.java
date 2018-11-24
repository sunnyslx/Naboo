package com.idx.naboo.takeout.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idx.naboo.R;

/**
 * 订单中外卖条目的view
 * Created by danny on 4/26/18.
 */

public class OrderItemView extends LinearLayout {
    private TextView mIndex;
    private TextView mFoodName;
    private TextView mCount;
    private TextView mPrice;

    public OrderItemView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.takeout_order_item, this, true);
        mIndex = findViewById(R.id.takeout_order_item_index);
        mFoodName = findViewById(R.id.takeout_order_item_food_name);
        mCount = findViewById(R.id.takeout_order_item_count);
        mPrice = findViewById(R.id.takeout_order_item_price);
    }

    public OrderItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OrderItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setText(String index,String foodName,String count,String price){
        mIndex.setText(index);
        mFoodName.setText(foodName);
        mCount.setText(count);
        mPrice.setText(price);
    }
}
