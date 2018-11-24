package com.idx.naboo.user.personal_center.order.orderbean;

import java.util.List;

/**
 * Created by ryan on 18-5-22.
 * Email: Ryan_chan01212@yeah.net
 */

public class Attributes_1 {
    private int id;

    private String name;

    private List<String> values ;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setString(List<String> values){
        this.values = values;
    }
    public List<String> getString(){
        return this.values;
    }
}
