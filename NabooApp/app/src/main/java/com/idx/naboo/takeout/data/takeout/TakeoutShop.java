package com.idx.naboo.takeout.data.takeout;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第五层json数据-TOSReply
 * 外卖实体类
 * Created by danny on 4/16/18.
 */

public class TakeoutShop {
    @SerializedName("name")
    private String mRestaurantName;//餐厅名称 必有
    @SerializedName("rating")
    private double mRestaurantRating;//餐厅评分 必有
    @SerializedName("deliver_amount")
    private double mStartRrice;//起送价 必有
    @SerializedName("address")
    private String mRestaurantAddress;//餐厅地址 必有
    @SerializedName("user_distance")
    private int mDistance;//用户到商家的距离 必有
    @SerializedName("is_open")
    private boolean mIsOpen;//是否正在营业 必有
    @SerializedName("phone_list")
    private String[] mPhoneList;//联系电话 必有
    @SerializedName("activities")
    private List<TSActivity> mTSActivities;//店铺活动信息 必有
    @SerializedName("image_url")
    private String mImageUrl;//餐厅Logo地址 必有
    @SerializedName("recent_order_num")
    private int mRecentMonthOrder;//最近一个月订单量 必有
    @SerializedName("deliver_spent")
    private int mDeliverSpent;//2周内的平均送餐时间

    public String getRestaurantName() {return mRestaurantName;}

    public double getRestaurantRating() {return mRestaurantRating;}

    public double getStartRrice() {return mStartRrice;}

    public String getRestaurantAddress() {return mRestaurantAddress;}

    public int getDistance() {return mDistance;}

    public boolean isOpen() {return mIsOpen;}

    public String[] getPhoneList() {return mPhoneList;}

    public List<TSActivity> getTSActivities() {return mTSActivities;}

    public String getImageUrl() {return mImageUrl;}

    public int getRecentMonthOrder() {return mRecentMonthOrder;}

    public int getDeliverSpent() {return mDeliverSpent;}
}
