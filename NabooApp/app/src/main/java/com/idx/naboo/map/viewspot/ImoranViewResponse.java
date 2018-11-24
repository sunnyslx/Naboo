package com.idx.naboo.map.viewspot;

import com.google.gson.annotations.SerializedName;

/**
 * 第一层json数据
 * Created by danny on 3/2/18.
 */

public class ImoranViewResponse {
    @SerializedName("data")
    private ViewData data;

    public ViewData getData() {return data;}
}
