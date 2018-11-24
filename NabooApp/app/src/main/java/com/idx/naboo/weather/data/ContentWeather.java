package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第三层json数据,Data下
 * Created by sunny on 18-3-14.
 */

public class ContentWeather {
    private String display;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("reply")
    private ReplyWeather reply;
    @SerializedName("tts")
    private String tts;
    private String type;
    @SerializedName("summary")
    private Summary summary;
    public String getType() {return type;}

    public int getErrorCode() {return errorCode;}

    public String getTts() {return tts;}

    public String getDisplay() {return display;}

    public ReplyWeather getReply() {return reply;}

    public Summary getSummary() {
        return summary;
    }
}
