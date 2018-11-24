package com.idx.naboo.figure.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/26/18.
 * 第二层json data下的数据
 */

public class FigureData {
    @SerializedName("content")
    private FigureContent mFigureContent;
    private String domain;

    public FigureContent getFigureContent() {
        return mFigureContent;
    }

    public String getDomain() {
        return domain;
    }
}
