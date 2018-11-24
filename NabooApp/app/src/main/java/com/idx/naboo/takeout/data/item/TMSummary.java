package com.idx.naboo.takeout.data.item;

import java.util.List;

/**
 * 第四层json数据-TMContent
 * Created by danny on 4/18/18.
 */

public class TMSummary {
    private int deliver_spent;//配送时间
    private double agent_fee;//配送费
    private double deliver_amount;//起送价
    private int is_bookable;//是否支持预定
    private int is_dist_rst;//是否蜂鸟专送餐厅
    private double latitude;//经纬度
    private double longitude;
    private double no_agent_fee_total;//超过该值免运费
    private String restaurant_address;//地址
    private String restaurant_name;//名字
    private List<String> deliver_times;//预定时间选项
    private List<String> phone_list;//商家电话
    private List<String> serving_time;//营业时间
    private List<Activities> activities;//优惠活动

    public List<Activities> getActivities() {return activities;}

    public void setActivities(List<Activities> activities) {this.activities = activities;}


    public int getDeliver_spent() {return deliver_spent;}

    public double getAgent_fee() {return agent_fee;}

    public double getDeliver_amount() {return deliver_amount;}

    public int getIs_bookable() {return is_bookable;}

    public int getIs_dist_rst() {return is_dist_rst;}

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

    public double getNo_agent_fee_total() {return no_agent_fee_total;}

    public String getRestaurant_address() {return restaurant_address;}

    public String getRestaurant_name() {return restaurant_name;}

    public List<String> getDeliver_times() {return deliver_times;}

    public List<String> getPhone_list() {return phone_list;}

    public List<String> getServing_time() {return serving_time;}
}
