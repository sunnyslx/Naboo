package com.idx.naboo.takeout.data.item;

import com.google.gson.annotations.SerializedName;

/**
 * 第二层json数据
 * Created by danny on 4/18/18.
 */

public class TMData {
    @SerializedName("content")
    private TMContent mTMContent;
    @SerializedName("domain")
    private String mDomain;
    @SerializedName("intention")
    private String mIntention;
    private String queryid;

    public String getIntention() {return mIntention;}
    public TMContent getContent() {return mTMContent;}
    public String getDomain() {return mDomain;}
    public String getQueryid() {return queryid;}
}
