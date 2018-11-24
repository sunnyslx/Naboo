package com.idx.naboo.music.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据,Data下
 * Created by danny on 3/2/18.
 */

public class ContentMusic {
    private String display;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("reply")
    private ReplyMusic reply;
    @SerializedName("tts")
    private String tts;
    private String type;

    public String getType() {return type;}

    public int getErrorCode() {return errorCode;}

    public String getTts() {return tts;}

    public String getDisplay() {return display;}

    public ReplyMusic getMusicReply() {return reply;}
}
