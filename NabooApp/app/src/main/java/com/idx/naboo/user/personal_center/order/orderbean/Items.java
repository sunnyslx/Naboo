package com.idx.naboo.user.personal_center.order.orderbean;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Items {
    private int count;

    private String name;

    private int price;

    public void setCount(int count){
        this.count = count;
    }
    public int getCount(){
        return this.count;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public int getPrice(){
        return this.price;
    }

}