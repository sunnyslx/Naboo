package com.idx.naboo.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hayden on 18-3-20.
 */

public class Point {
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

    public Point(String address, double latitude, double longitude, String name, int distance) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.distance = distance;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

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

    public int getDistance() {
        return distance;
    }
}
