package com.idx.naboo.videocall.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析通讯录工具类
 * Created by danny on 3/27/18.
 */

public class FriendNameObtainUtils {

    /**
     * 解析json获取好友名字
     *
     * @param json 蓦然返回json数据
     * @return 好友账号
     */
    public static String getAccount(String json){
        String account="";
        JSONObject object=null;
        try {
            object=new JSONObject(json);
            JSONArray obj=object.getJSONObject("data").getJSONObject("content")
                    .getJSONObject("reply").getJSONArray("phone_contact");
            if (obj!=null) {
                JSONObject o = obj.getJSONObject(0);
                if (o != null) {
                    account = o.getString("phone_contact_name");
                    Log.d("数据", "getAccount: " + account);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return account;
    }
}
