package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第一层json数据
 * Created by sunny on 18-3-14.
 */

public class ImoranResponse {
    @SerializedName("data")
    private DataWeather dataWeather;

    public DataWeather getDataWeather() {return dataWeather;}
}
