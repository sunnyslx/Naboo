package net.imoran.tv.sdk.network;

import net.imoran.tv.sdk.network.requestdata.IResponseJudger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bobge on 2017/7/12.
 * 用于动态判断返回的json数据是否正确
 */
public class UserInfoJudgerIml implements IResponseJudger {

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
