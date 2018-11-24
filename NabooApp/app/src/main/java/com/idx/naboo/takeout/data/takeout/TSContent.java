package com.idx.naboo.takeout.data.takeout;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据-TSData
 * Created by danny on 4/16/18.
 */

public class TSContent {
    @SerializedName("display")
    private String mDisplay;
    @SerializedName("error_code")
    private int mErrorCode;
    @SerializedName("reply")
    private TSReply mTSReply;
    @SerializedName("tts")
    private String mTTS;
    @SerializedName("type")
    private String mType;

    public String getDisplay() {return mDisplay;}

    public int getErrorCode() {return mErrorCode;}

    public TSReply getTSReply() {return mTSReply;}

    public String getTTS() {return mTTS;}

    public String getType() {return mType;}
}
