package com.idx.naboo.user.personal_center.order.orderbean;

import java.util.List;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Order_list {
    private boolean login_flag;

    private int orderTypeFlag;

    private List<Orders> orders ;

    public void setLogin_flag(boolean login_flag){
        this.login_flag = login_flag;
    }
    public boolean getLogin_flag(){
        return this.login_flag;
    }
    public void setOrderTypeFlag(int orderTypeFlag){
        this.orderTypeFlag = orderTypeFlag;
    }
    public int getOrderTypeFlag(){
        return this.orderTypeFlag;
    }
    public void setOrders(List<Orders> orders){
        this.orders = orders;
    }
    public List<Orders> getOrders(){
        return this.orders;
    }


}
