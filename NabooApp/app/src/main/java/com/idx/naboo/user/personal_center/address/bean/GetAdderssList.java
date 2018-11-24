package com.idx.naboo.user.personal_center.address.bean;

import net.imoran.tv.sdk.network.param.BaseParams;

import java.util.HashMap;

/**
 * Created by ryan on 18-5-3.
 * Email: Ryan_chan01212@yeah.net
 */

public class GetAdderssList extends BaseParams {

    private String mo;

    public String getMo() {
        return mo;
    }

    public void setMo(String mo) {
        this.mo = mo;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap();
        params.put("mobile",getMo());
        return params;
    }

}
