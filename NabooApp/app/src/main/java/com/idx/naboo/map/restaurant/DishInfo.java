package com.idx.naboo.map.restaurant;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hayden on 18-4-23.
 */

public class DishInfo {
    @SerializedName("image")
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }
}
