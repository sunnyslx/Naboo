package com.idx.naboo.map.restaurant;

import com.google.gson.annotations.SerializedName;

/**
 * 第一层json数据
 * Created by hayden on 3/2/18.
 */

public class ImoranRestaurantResponse {
    @SerializedName("data")
    private RestaurantData data;

    public RestaurantData getData() {return data;}
}
