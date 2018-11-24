package com.idx.naboo.news.data;

import com.google.gson.annotations.SerializedName;

/**
 * content下第三层json
 * Created by darkmi on 3/19/18.
 */

public class NewsContent {
    private String display;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("reply")
    private NewsReply newsReply;
    @SerializedName("semantic")
    private NewsSemantic semantic;
    private String tts;
    private String type;

    public String getDisplay() {
        return display;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public NewsReply getNewsReply() {
        return newsReply;
    }

    public NewsSemantic getSemantic() {
        return semantic;
    }

    public String getTts() {
        return tts;
    }

    public String getType() {
        return type;
    }
}
