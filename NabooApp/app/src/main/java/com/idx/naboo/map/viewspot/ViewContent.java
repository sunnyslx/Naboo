package com.idx.naboo.map.viewspot;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据,Data下
 * Created by danny on 3/2/18.
 */

public class ViewContent {
    private String display;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("reply")
    private ViewReply reply;
    private String tts;
    private String type;

    public String getType() {return type;}

    public int getErrorCode() {return errorCode;}

    public String getTts() {return tts;}

    public String getDisplay() {return display;}

    public ViewReply getReply() {return reply;}
}
