package com.idx.naboo.dish.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据
 * Created by sunny on 18-3-21.
 */

public class DishReply {
    @SerializedName("dish")
    private List<Dish> dishe;

    public List<Dish> getDishe() {
        return dishe;
    }
}
