package com.idx.naboo.weather.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第六层json数据：Weather下 --- 生活建议
 * Created by sunny on 18-3-14.
 */

public class LifeSuggestion {
    @SerializedName("air")
    private Air air;
    @SerializedName("cw")
    private CarWash cw;
    @SerializedName("drsg")
    private Dress drsg;
    @SerializedName("uv")
    private UV uv;

    public Air getAir() {return air;}

    public CarWash getCw() {return cw;}

    public Dress getDrsg() {return drsg;}

    public UV getUv() {
        return uv;
    }
}
