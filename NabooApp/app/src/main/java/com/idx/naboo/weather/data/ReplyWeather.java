package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据：Content下
 * Created by sunny on 18-3-14.
 */

public class ReplyWeather {
    @SerializedName("weather")
    private List<Weather> weather;

    public List<Weather> getWeather() {return weather;}
}
