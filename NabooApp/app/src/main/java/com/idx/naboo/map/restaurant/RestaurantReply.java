package com.idx.naboo.map.restaurant;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据：Content下
 * Created by hayden on 3/2/18.
 */

public class RestaurantReply {
    @SerializedName("restaurant")
    private List<Restaurant> locationList;

    public List<Restaurant> getLocationList() {
        return locationList;
    }
}
