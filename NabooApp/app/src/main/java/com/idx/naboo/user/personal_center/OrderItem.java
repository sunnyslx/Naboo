package com.idx.naboo.user.personal_center;


/**
 * Created by ryan on 18-4-26.
 * Email: Ryan_chan01212@yeah.net
 */

public class OrderItem {
    private int id;
    private String name;
    private String order_pay;
    private int business_photo;
    private String pay_time;
    private String business_number;
    private String pay_money;
    private String pay_money_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder_pay() {
        return order_pay;
    }

    public void setOrder_pay(String order_pay) {
        this.order_pay = order_pay;
    }

    public int getBusiness_photo() {
        return business_photo;
    }

    public void setBusiness_photo(int business_photo) {
        this.business_photo = business_photo;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getBusiness_number() {
        return business_number;
    }

    public void setBusiness_number(String business_number) {
        this.business_number = business_number;
    }

    public String getPay_money() {
        return pay_money;
    }

    public void setPay_money(String pay_money) {
        this.pay_money = pay_money;
    }

    public String getPay_money_time() {
        return pay_money_time;
    }

    public void setPay_money_time(String pay_money_time) {
        this.pay_money_time = pay_money_time;
    }
}
