package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第七层json数据：WeatherDetail下 --- 天气状况码
 *      codeDay:白天  codeNight:晚上
 * Created by sunny on 18-3-14.
 */

public class WeatherStateCode {
    @SerializedName("code_d")
    private String codeDay;
    @SerializedName("code_n")
    private String codeNight;
    @SerializedName("txt_d")
    private String txt_d;
    @SerializedName("txt_n")
    private String txt_n;
    public String getCodeNight() {return codeNight;}

    public String getCodeDay() {return codeDay;}

    public String getTxt_d() {
        return txt_d;
    }

    public String getTxt_n() {
        return txt_n;
    }
}
