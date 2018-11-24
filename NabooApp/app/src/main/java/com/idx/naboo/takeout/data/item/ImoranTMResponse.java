package com.idx.naboo.takeout.data.item;

import com.google.gson.annotations.SerializedName;

/**
 * 第一层json数据
 *  例:第五个--返回结果数据
 *
 * Created by danny on 4/18/18.
 */

public class ImoranTMResponse {
    @SerializedName("data")
    private TMData mTMData;

    public TMData getData() {return mTMData;}
}
