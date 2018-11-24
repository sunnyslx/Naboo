package com.idx.naboo.user.personal_center.address;

import java.util.List;

/**
 * Created by ryan on 18-4-13.
 * Email: Ryan_chan01212@yeah.net
 */

public class AllAddress {

    private int ret;
    private String msg;
    private List<DataBean> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

}
