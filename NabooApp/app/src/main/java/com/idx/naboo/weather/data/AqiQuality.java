package com.idx.naboo.weather.data;

/**
 * 第六层json数据：Weather下 --- 空气质量
 * Created by sunny on 18-3-14.
 */

public class AqiQuality {
    private String qlty;
    private String pm25;
    private String pm10;
    private String no2;
    private String so2;
    private String o3;
    private String co;
    private String aqi;

    public String getQlty() {return qlty;}

    public String getPm25() {
        return pm25;
    }

    public String getPm10() {
        return pm10;
    }

    public String getNo2() {
        return no2;
    }

    public String getSo2() {
        return so2;
    }

    public String getO3() {
        return o3;
    }

    public String getCo() {
        return co;
    }

    public String getAqi() {
        return aqi;
    }
}
