package com.idx.naboo.dish.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第二层json数据
 * Created by sunny on 18-3-21.
 */

public class DishData {
    @SerializedName("content")
    private DishContent dishContent;
    private String domain;
    @SerializedName("intention")
    private String intention;
    @SerializedName("queryid")
    private String queryid;
    public String getDomain() {
        return domain;
    }

    public DishContent getDishContent() {
        return dishContent;
    }

    public String getIntention() {
        return intention;
    }

    public String getQueryid() {
        return queryid;
    }
}
