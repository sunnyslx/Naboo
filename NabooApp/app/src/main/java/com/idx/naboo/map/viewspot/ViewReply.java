package com.idx.naboo.map.viewspot;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据：Content下
 * Created by danny on 3/2/18.
 */

public class ViewReply {
    @SerializedName("viewspot")
    private List<ViewSpot> viewSpotList;

    public List<ViewSpot> getViewSpotList() {
        return viewSpotList;
    }
}
