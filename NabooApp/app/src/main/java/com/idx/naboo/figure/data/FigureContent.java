package com.idx.naboo.figure.data;


import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/26/18.
 * 第三层json数据
 */

public class FigureContent {
    private String display;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("reply")
    private FigureReply mFigureReply;
    private String tts;
    private String type;

    public Semantic getSemantic() {
        return mSemantic;
    }

    @SerializedName("semantic")
    private Semantic mSemantic;

    public String getDisplay() {
        return display;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public FigureReply getFigureReply() {
        return mFigureReply;
    }

    public String getTts() {
        return tts;
    }

    public String getType() {
        return type;
    }
}
