package com.idx.naboo.takeout.data.item;

/**
 * Created by danny on 4/18/18.
 */

public class TakeoutMenu {
    private String id;//商品ID
    private String name;//菜名字
    private String image_url;//菜图片
    private double rating;//评分
    private int stock;//库存
    private String takeoutNum;//购买份数
    private int sales;//月售份数
    private double packing_fee;//打包费
    private double original_price;//原价
    private double price;//现价
    private double activity_discount;//活动折扣
    private String activity_description;//活动描述
    private int activity_max_quantity;//活动限制的最大购买数量
    private String activity_name;//活动名称
    private int activity_quantity_condition;//第n份m折, 配合activity_discount使用
    private int activity_sum_condition;//满多少
    private String category;//商品类别
    private int checkout_mode;//购物车加购模式, 1: 正常, 2: 必选 (如果没有加购必选菜,无法下单)
    private String description;//商品描述
    private String icon_url;//图标地址
    private int is_essential;//是否必点, 必点分类下只要点一个 food 就好了, 如果必点商品售完, 不需要必点
    private int min_purchase;//最少起购分数, 默认: 1
    private int score;//分数

    public String getId() {return id;}

    public String getName() {return name;}

    public String getImage_url() {return image_url;}

    public double getRating() {return rating;}

    public int getStock() {return stock;}

    public String getTakeoutNum() {return takeoutNum;}

    public int getSales() {return sales;}

    public double getPacking_fee() {return packing_fee;}

    public double getOriginal_price() {return original_price;}

    public double getPrice() {return price;}

    public double getActivity_discount() {return activity_discount;}

    public String getActivity_description() {return activity_description;}

    public int getActivity_max_quantity() {return activity_max_quantity;}

    public String getActivity_name() {return activity_name;}

    public int getActivity_quantity_condition() {return activity_quantity_condition;}

    public int getActivity_sum_condition() {return activity_sum_condition;}

    public String getCategory() {return category;}

    public int getCheckout_mode() {return checkout_mode;}

    public String getDescription() {return description;}

    public String getIcon_url() {return icon_url;}

    public int getIs_essential() {return is_essential;}

    public int getMin_purchase() {return min_purchase;}

    public int getScore() {return score;}
}
