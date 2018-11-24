package com.idx.naboo.user.personal_center.order.orderbean;

import java.util.List;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Reply {
    private List<Order_list> order_list ;

    public void setOrder_list(List<Order_list> order_list){
        this.order_list = order_list;
    }
    public List<Order_list> getOrder_list(){
        return this.order_list;
    }
}
