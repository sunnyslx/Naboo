package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第二层json数据
 * Created by sunny on 18-3-14.
 */

public class DataWeather {
    @SerializedName("content")
    private ContentWeather content;
    private String domain;
    @SerializedName("intention")
    private String intention;
    @SerializedName("queryid")
    private String queryid;
    public ContentWeather getContent() {return content;}

    public String getDomain() {return domain;}

    public String getIntention() {
        return intention;
    }

    public String getQueryid() {
        return queryid;
    }
}
