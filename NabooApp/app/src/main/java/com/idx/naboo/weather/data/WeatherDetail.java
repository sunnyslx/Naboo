package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第六层json数据：Weather下 --- 天气详情
 * Created by sunny on 18-3-14.
 */

public class WeatherDetail {
    @SerializedName("cond")
    private WeatherStateCode weatherStateCode;
    private String date;
    @SerializedName("tmp")
    private Temperature tmp;

    public WeatherStateCode getWeatherStateCode() {return weatherStateCode;}

    public String getDate() {return date;}

    public Temperature getTemperature() {return tmp;}
}
