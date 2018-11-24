package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第五层json数据：ReplyWeather下 --- weather总况
 * Created by sunny on 18-3-14.
 */

public class Weather {
    private String aqi;
    private String city;
    private String date;
    private String describe;
    @SerializedName("aqi_detail")
    private AqiQuality aqiQuality;
    @SerializedName("suggestion")
    private LifeSuggestion suggestion;
    @SerializedName("weather_detail")
    private WeatherDetail weatherDetail;

    public WeatherDetail getWeatherDetail() {return weatherDetail;}

    public AqiQuality getAqiQuality() {return aqiQuality;}

    public LifeSuggestion getSuggestion() {return suggestion;}

    public String getAqi() {return aqi;}

    public String getCity() {return city;}

    public String getDate() {return date;}

    public String getDescribe() {return describe;}

    @Override
    public String toString() {
        return "WeatherBasic{" +
                "aqi='" + aqi + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
