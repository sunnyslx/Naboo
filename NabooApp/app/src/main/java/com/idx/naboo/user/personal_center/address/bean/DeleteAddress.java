package com.idx.naboo.user.personal_center.address.bean;

import net.imoran.tv.sdk.network.param.BaseParams;

import java.util.HashMap;

/**
 * Created by ryan on 18-4-21.
 * Email: Ryan_chan01212@yeah.net
 */

public class DeleteAddress extends BaseParams {

    private String addressid;
    private String uid;


    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
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
            params.put("addressid",getAddressid());
            return params;
        }
    }

