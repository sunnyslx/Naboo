package com.idx.naboo.takeout.data.takeout;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据-TSContent
 * Created by danny on 4/16/18.
 */

public class TSReply {
    @SerializedName("takeoutshop")
    private List<TakeoutShop> mTakeoutShops;

    public List<TakeoutShop> getTakeoutShops() {return mTakeoutShops;}
}
