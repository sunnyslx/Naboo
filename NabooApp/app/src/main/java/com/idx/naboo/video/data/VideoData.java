package com.idx.naboo.video.data;

import com.google.gson.annotations.SerializedName;


/**
 * json第二层json解析
 * Created by sunny on 18-3-14.
 */

public class VideoData {

    @SerializedName("content")
    private VideoContent content;
    @SerializedName("domain")
    private String domain;
    @SerializedName("intention")
    private String intention;
    @SerializedName("queryid")
    private String queryid;
    public VideoContent getContent() {
        return content;
    }

    public String getDomain() {
        return domain;
    }

    public String getIntention() {
        return intention;
    }

    public String getQueryid() {
        return queryid;
    }
}
