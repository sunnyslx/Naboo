package com.idx.naboo.map.bean;

/**
 * Created by hayden on 18-4-25.
 */

/**
 * 路径规划选则方案的数据类
 */
public class RouteMessageBean {
    //方式
    private String style;
    //路径选择策略
    private String routeType;
    //路径总长度
    private String routeDistance;
    //路径总耗时
    private String routeTime;

    public RouteMessageBean(String style, String routeType, String routeDistance, String routeTime) {
        this.style = style;
        this.routeType = routeType;
        this.routeDistance = routeDistance;
        this.routeTime = routeTime;
    }

    public String getStyle() {
        return style;
    }

    public String getRouteType() {
        return routeType;
    }

    public String getRouteDistance() {
        return routeDistance;
    }

    public String getRouteTime() {
        return routeTime;
    }
}
