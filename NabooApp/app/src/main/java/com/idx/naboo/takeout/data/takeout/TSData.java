package com.idx.naboo.takeout.data.takeout;

import com.google.gson.annotations.SerializedName;

/**
 * 第二层json数据
 * Created by danny on 4/16/18.
 */

public class TSData {
    @SerializedName("content")
    private TSContent mTSContent;
    @SerializedName("domain")
    private String mDomain;
    private String queryid;
    private String intention;

    public String getIntention() {return intention;}
    public String getQueryid() {return queryid;}
    public TSContent getTSContent() {return mTSContent;}
    public String getDomain() {return mDomain;}
}
