package com.idx.naboo.user.personal_center.address.bean;

import android.text.TextUtils;
import android.util.Log;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;

import java.util.HashMap;

/**
 * 增加地址
 * Created by ryan on 18-4-20.
 * Email: Ryan_chan01212@yeah.net
 */

public class AddAddress extends BaseParams {

    private UserInfoBean userInfoBean;
    private String createid;
    private String uid;

    public AddAddress() {
    }

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

    public String getUuid() {
        return uid;
    }

    public void setUuid(String uid) {
        this.uid = uid;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap();
        if(TextUtils.isEmpty(GsonObjectDeserializer.produceGson().toJson(this.getUserInfoBean()))) {
            Log.e("params", "RegisterParams getParams empty");
            return null;
        } else {
            params.put("json", GsonObjectDeserializer.produceGson().toJson(this.getUserInfoBean()));
            params.put("uid", getUuid());
            params.put("createid",getCreateid());
            return params;
        }
    }

}
