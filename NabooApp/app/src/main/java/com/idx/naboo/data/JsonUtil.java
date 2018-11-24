package com.idx.naboo.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayden on 18-3-14.
 */

public class JsonUtil {
    private static final String TAG = JsonData.class.getName();

    public static JsonData createJsonData(String json) {
        //第一步：拿到根节点
        try {
            JsonData jsonData = new JsonData();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");

            String intention = data.getString("intention");
            jsonData.setIntention(intention);

            String domain = data.getString("domain");
            jsonData.setDomain(domain);

            String queryId = data.getString("queryid");
            jsonData.setQueryId(queryId);

            String action = data.getString("action");
            jsonData.setAction(action);

            JSONObject content = data.getJSONObject("content");
            jsonData.setContent(content);

            String type = "";
            String tts = "";
            int errorCode = 0;
            try {
                type = content.getString("type");
                tts = content.getString("tts");
                errorCode = content.getInt("error_code");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jsonData.setType(type);
                jsonData.setTts(tts);
                jsonData.setErrorCode(errorCode);
            }

            Log.i(TAG, "辨析结果所属类别为：" + domain);
            return jsonData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonData setParkingOrPoi(JsonData jsonData) {
        List<Point> list = new ArrayList<>();
        JSONArray pointList = null;
        try {
            //继续解析
            if (jsonData.getType().equals("parking")) {
                Log.i(TAG, "是停车，点坐标已经集合完毕");
                JSONObject reply = jsonData.getContent().getJSONObject("reply");
                if (reply != null) {
                    pointList = reply.getJSONArray("parking");
                }
            } else if (jsonData.getType().equals("poi")) {
                Log.i(TAG, "是附近poi，点坐标已经集合完毕");
                JSONObject reply = jsonData.getContent().getJSONObject("reply");
                if (reply != null) {
                    pointList = reply.getJSONArray("poi");
                }
            }
            if (pointList != null) {
                String name = pointList.getJSONObject(0).getString("name");
                for (int i = 0; i < pointList.length(); i++) {
                    JSONObject park = pointList.getJSONObject(i);
                    Point point = new Point(park.getString("address"), park.getDouble("latitude")
                            , park.getDouble("longitude"), park.getString("name"), park.getInt("user_distance"));
                    list.add(point);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (pointList != null) {
                try {
                    for (int i = 0; i < pointList.length(); i++) {
                        JSONObject loc = pointList.getJSONObject(i).getJSONObject("loc");
                        Point point = new Point(loc.getString("address"), loc.getDouble("latitude")
                                , loc.getDouble("longitude"), loc.getString("name"), loc.getInt("user_distance"));
                        list.add(point);
                    }
                } catch (JSONException error) {
                    error.printStackTrace();
                }
            }
        }
        jsonData.setPointList(list);
        return jsonData;
    }
}
