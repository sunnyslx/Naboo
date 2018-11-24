package net.imoran.tv.sdk.network;


import net.imoran.tv.sdk.network.requestdata.IResponseJudger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cuihonggui on 2017/11/20.
 */

public class DeviceListJudgerIml implements IResponseJudger {

    @Override
    public boolean judge(String data) {
        JSONObject responseHeader = null;
        try {
            responseHeader = new JSONObject(data);
            if ("200".equals(responseHeader.getString("ret"))) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
