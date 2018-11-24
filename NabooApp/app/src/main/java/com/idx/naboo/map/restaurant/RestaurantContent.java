package com.idx.naboo.map.restaurant;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据,Data下
 * Created by hayden on 3/2/18.
 */

public class RestaurantContent {
    private String display;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("reply")
    private RestaurantReply reply;
    private String tts;
    private String type;

    public String getType() {return type;}

    public int getErrorCode() {return errorCode;}

    public String getTts() {return tts;}

    public String getDisplay() {return display;}

    public RestaurantReply getReply() {return reply;}
}
