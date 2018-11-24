package com.idx.naboo.takeout.data.item;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据-TMData
 * Created by danny on 4/18/18.
 */

public class TMContent {
    @SerializedName("display")
    private String mDisplay;
    @SerializedName("error_code")
    private int mErrorCode;
    @SerializedName("reply")
    private TMReply mTMReply;
    @SerializedName("summary")
    private TMSummary mTMSummary;
    @SerializedName("tts")
    private String mTTS;
    @SerializedName("type")
    private String mType;

    public String getDisplay() {return mDisplay;}

    public int getErrorCode() {return mErrorCode;}

    public TMReply getTMReply() {return mTMReply;}

    public TMSummary getTMSummary() {return mTMSummary;}

    public String getTTS() {return mTTS;}

    public String getType() {return mType;}
}
