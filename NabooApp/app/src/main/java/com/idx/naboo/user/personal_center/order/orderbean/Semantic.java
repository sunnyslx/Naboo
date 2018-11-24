package com.idx.naboo.user.personal_center.order.orderbean;

import java.util.List;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Semantic {
    private List<String> intent ;

    public void setString(List<String> intent){
        this.intent = intent;
    }
    public List<String> getString(){
        return this.intent;
    }
}
