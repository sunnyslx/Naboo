package com.idx.naboo.user.personal_center.address.bean;

import net.imoran.tv.sdk.network.param.BaseParams;

import java.util.HashMap;

/**
 * Created by ryan on 18-4-21.
 * Email: Ryan_chan01212@yeah.net
 */

public class QueryAddress extends BaseParams {
    private String createid;
    private String uid;




    public String getCreateid() {
        return createid;
    }

    public void setCreateid(String createid) {
        this.createid = createid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public HashMap<String, String> getParams() {

        HashMap<String, String> params = new HashMap();
            params.put("uid", getUid());
            params.put("createid",getCreateid());
            return params;
        }

}
