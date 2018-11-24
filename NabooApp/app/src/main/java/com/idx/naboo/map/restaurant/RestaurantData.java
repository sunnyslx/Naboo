package com.idx.naboo.map.restaurant;

/**
 * 第二层json数据
 * Created by hayden on 3/2/18.
 */

public class RestaurantData {
    private RestaurantContent content;
    private String domain;
    private String intention;

    public String getIntention() {
        return intention;
    }

    public RestaurantContent getContent() {return content;}

    public String getDomain() {return domain;}
}
