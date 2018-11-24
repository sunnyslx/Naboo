package com.idx.naboo.video.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json解析
 * Created by sunny on 18-3-14.
 */

public class VideoContent {
    @SerializedName("error_code")
    private String display;

    private int errorCode;

    private String tts;

    private String type;
    @SerializedName("reply")
    private VideoReply videoReply;
    @SerializedName("semantic")
    private Semantic semantic;
    public String getType() {
        return type;
    }

    public String getTts() {
        return tts;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDisplay() {
        return display;
    }

    public VideoReply getVideoReply() {
        return videoReply;
    }

    public Semantic getSemantic() {
        return semantic;
    }
}
