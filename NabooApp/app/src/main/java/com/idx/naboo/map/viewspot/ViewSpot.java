package com.idx.naboo.map.viewspot;

import com.google.gson.annotations.SerializedName;

/**
 * 景点类的实体对象
 */

public class ViewSpot {
    //地址
    private String address;
    //维度
    private double latitude;
    //经度
    private double longitude;
    //名称
    private String name;
    //用户距离
    @SerializedName("user_distance")
    private int distance;
    //综合评分
    @SerializedName("overall_rating")
    private double rating;
    //景点级别
    private float grade;
    //景点电话
    private String telephone;
    //图片
    private String[] images;
    //描述
    private String introduction;
    //开放时间
    private String open_time;

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getOpenTime() {
        return open_time;
    }

    public int getDistance() {
        return distance;
    }

    public double getRating() {
        return rating;
    }

    public float getGrade() {
        return grade;
    }

    public String getTelephone() {
        return telephone;
    }

    public String[] getImages() {
        return images;
    }

    public String getIntroduction() {
        return introduction;
    }
}
