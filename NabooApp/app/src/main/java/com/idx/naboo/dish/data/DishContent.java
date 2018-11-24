package com.idx.naboo.dish.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据
 * Created by sunny on 18-3-21.
 */

public class DishContent {
    @SerializedName("reply")
    private DishReply dishReply;

    private String tts;
    private String type;
    @SerializedName("error_code")
    private String errorCode;
    private String display;

    public DishReply getDishReply() {
        return dishReply;
    }

    public String getTts() {
        return tts;
    }

    public String getType() {
        return type;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDisplay() {
        return display;
    }
}
