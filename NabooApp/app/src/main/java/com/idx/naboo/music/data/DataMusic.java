package com.idx.naboo.music.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第二层json数据
 * Created by sunny on 3/2/18.
 */

public class DataMusic {
    @SerializedName("content")
    private ContentMusic content;
    private String domain;
    @SerializedName("intention")
    private String intention;
    @SerializedName("queryid")
    private String queryid;
    public ContentMusic getMusicContent() {return content;}

    public String getDomain() {return domain;}

    public String getIntention() {
        return intention;
    }

    public String getQueryid() {
        return queryid;
    }
}
