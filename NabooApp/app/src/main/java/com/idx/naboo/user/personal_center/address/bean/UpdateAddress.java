package com.idx.naboo.user.personal_center.address.bean;

import android.text.TextUtils;
import android.util.Log;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;

import java.util.HashMap;

/**
 * 更新地址
 * Created by ryan on 18-4-21.
 * Email: Ryan_chan01212@yeah.net
 */

public class UpdateAddress extends BaseParams {


    private UserInfoBean userInfoBean;
    private String createid;
    private String uid;
    private String addressid;


    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

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

    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
    }

    @Override
    public HashMap<String, String> getParams() {

        HashMap<String, String> params = new HashMap();
        if(TextUtils.isEmpty(GsonObjectDeserializer.produceGson().toJson(this.getUserInfoBean()))) {
            Log.e("params", "RegisterParams getParams empty");
            return null;
        } else {
            params.put("json", GsonObjectDeserializer.produceGson().toJson(this.getUserInfoBean()));
            params.put("uid", getUid());
            params.put("createid",getCreateid());
            params.put("addressid",getAddressid());
            return params;
        }
    }

}





