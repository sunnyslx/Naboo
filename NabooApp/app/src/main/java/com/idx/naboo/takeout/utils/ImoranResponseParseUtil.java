package com.idx.naboo.takeout.utils;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.music.data.ImoranResponseMusic;
import com.idx.naboo.service.Execute;
import com.idx.naboo.takeout.data.item.ImoranTMResponse;
import com.idx.naboo.takeout.data.takeout.ImoranTSResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Imoran 数据解析工具类
 * Created by danny on 4/16/18.
 */

public class ImoranResponseParseUtil {
    public static final String TAG= ImoranResponseParseUtil.class.getSimpleName();

    /**
     * 解析封装外卖数据
     *
     * @param response 数据源
     * @return 外卖对象
     */
    public static ImoranTSResponse parseTS(String response){
        Log.d(TAG, "parseTOS: 解析封装外卖数据");
        Gson gson = new Gson();
        ImoranTSResponse tos = gson.fromJson(response, ImoranTSResponse.class);
        return tos;
    }

    /**
     * 判断外卖数据是否为null
     *
     * @param takeout 外卖object
     * @return true-不为null false-为null
     */
    public static boolean isImoranTSNull(ImoranTSResponse takeout){
        boolean flag = false;
        if (takeout!=null && takeout.getData()!=null && takeout.getData().getTSContent()!=null) {
            flag = true;
        }
        return flag;
    }

    /**
     * 解析封装外卖条目数据
     *
     * @param response 数据源
     * @return 外卖条目对象
     */
    public static ImoranTMResponse parseTM(String response){
        Log.d(TAG, "parseTOS: 解析封装外卖数据");
        Gson gson = new Gson();
        ImoranTMResponse tom = gson.fromJson(response, ImoranTMResponse.class);
        return tom;
    }

    /**
     * 判断外卖条目数据是否为null
     *
     * @param takeout 外卖条目object
     * @return true-不为null false-为null
     */
    public static boolean isImoranTMNull(ImoranTMResponse takeout){
        boolean flag = false;
        if (takeout!=null && takeout.getData()!=null && takeout.getData().getContent()!=null) {
            flag = true;
        }
        return flag;
    }

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
                    if (account.contains("-")){
                        account=account.replace("-","");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return account;
    }

    /**
     * 解析json获取好友名字
     *
     * @param json 蓦然返回json数据  --  新版json数据
     * @return 好友账号
     */
    public static String getNewAccount(String json){
        String account="";
        JSONObject object=null;
        try {
            object=new JSONObject(json);
            JSONArray obj=object.getJSONObject("data").getJSONObject("content")
                    .getJSONObject("semantic").getJSONArray("CallTarget");
            if (obj!=null) {account = obj.getString(0);}
            if (account.contains("-")) {account = account.replace("-", "");}
            Log.d("数据", "getAccount: " + account);
        } catch (JSONException e) {
            try {
                JSONArray obj1=new JSONObject(json).getJSONObject("data").getJSONObject("content")
                        .getJSONObject("semantic").getJSONArray("TelePhoneNumber");
                if (obj1!=null) {account = obj1.getString(0);}
                if (account.contains("-")) {account = account.replace("-", "");}
                Log.d("数据", "getAccount: " + account);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        //置空Json
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        NabooApplication.getInstance().getBaseContext().sendBroadcast(intent);
        return account;
    }

}
