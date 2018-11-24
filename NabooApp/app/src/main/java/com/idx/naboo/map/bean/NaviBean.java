package com.idx.naboo.map.bean;

import com.amap.api.navi.model.NaviLatLng;

/**
 * Created by hayden on 18-4-28.
 */

public class NaviBean {
    //起点名称
    private String startName;
    //途经点名称
    private String midName;
    //终点名称
    private String endName;
    //起点坐标
    private NaviLatLng startNaviLatlng;
    //途经点坐标
    private NaviLatLng midNaviLatlng;
    //终点坐标
    private NaviLatLng endNaviLatlng;

    public String getStartName() {
        return startName;
    }

    public String getMidName() {
        return midName;
    }

    public String getEndName() {
        return endName;
    }

    public NaviLatLng getStartNaviLatlng() {
        return startNaviLatlng;
    }

    public NaviLatLng getMidNaviLatlng() {
        return midNaviLatlng;
    }

    public NaviLatLng getEndNaviLatlng() {
        return endNaviLatlng;
    }

    public NaviBean(String startName, String midName, String endName, NaviLatLng startNaviLatlng, NaviLatLng midNaviLatlng, NaviLatLng endNaviLatlng) {
        this.startName = startName;
        this.midName = midName;
        this.endName = endName;
        this.startNaviLatlng = startNaviLatlng;
        this.midNaviLatlng = midNaviLatlng;
        this.endNaviLatlng = endNaviLatlng;
    }
}
