package com.idx.naboo.map.restaurant;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hayden on 18-3-20.
 */

public class Restaurant {
    //餐厅名称
    private String name;
    //餐厅星级
    @SerializedName("overall_rating")
    private double rating;
    //人均消费
    private double price;
    //特色菜
    private String[] dish_tags;
    //店铺地址
    private String address;
    //联系电话
    private String telephone;
    //用户距离
    @SerializedName("user_distance")
    private int distance;
    //图片链接
    private String img_url;
    //维度
    private float latitude;
    //经度
    private float longitude;
    //店铺菜系
    private String food_type;
    //店铺菜图片
    @SerializedName("dish_info")
    private List<DishInfo> dishInfoList;

    public List<DishInfo> getDishInfoList() {
        return dishInfoList;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public double getPrice() {
        return price;
    }

    public String[] getDish_tags() {
        return dish_tags;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public int getDistance() {
        return distance;
    }

    public String getImg_url() {
        return img_url;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getFood_type() {
        return food_type;
    }
}
