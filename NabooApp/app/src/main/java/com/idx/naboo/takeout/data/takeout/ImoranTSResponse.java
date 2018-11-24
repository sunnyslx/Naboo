package com.idx.naboo.takeout.data.takeout;

import com.google.gson.annotations.SerializedName;

/**
 * Imoran 返回 外卖数据
 * 第一层json数据
 * Created by danny on 4/16/18.
 */

public class ImoranTSResponse {
    @SerializedName("data")
    private TSData mTSData;

    public TSData getData() {return mTSData;}
}
