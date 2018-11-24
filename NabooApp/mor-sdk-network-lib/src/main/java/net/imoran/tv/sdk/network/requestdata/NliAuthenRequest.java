package net.imoran.tv.sdk.network.requestdata;

import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Moran on 2017/3/27.
 * 鉴权参数
 */

public class NliAuthenRequest {
    private static final String TAG = "NLIRequest";
    private static final String REQUEST_TAG = "json";

    private String type;
    private String query;
    private String service;
    private String key;
    private String deviceid;
    private Double longitude;
    private Double latitude;
    private String userid;
    private String accountid;
    private String queryid;
    private Gson mGson;
    private String mBaseUrl;
    private Object postData;
    private String actionname;
    private String sessionid;
    private String orderid;
    private String json;
    private String contextid;

    private final String verNum = "3.0";
    private final String tag_service = "service";
    private final String tag_query = "query";
    private final String tag_key = "key";
    private final String tag_deviceId = "deviceid";
    private final String tag_long = "longitude";
    //    private final String tag_long = "long";
//    private final String tag_lat = "lat";
    private final String tag_lat = "latitude";
    private final String tag_type = "type";
    private final String tag_userid = "userid";
    private final String tag_accountid = "accountid";
    private final String tag_queryid = "queryid";
    private final String tag_version = "ver";
    private final String tag_action_name = "actionname";
    private final String tag_json = "json";
    private final String tag_timestamp = "timestamp";


    private NliAuthenRequest(Builder builder) {
        setType(builder.type);
        setQuery(builder.query);
        setService(builder.service);
        setKey(builder.key);
        setDeviceid(builder.deviceid);
        setLongitude(builder.longitude);
        setLatitude(builder.latitude);
        setUserid(builder.userid);
        setAccountid(builder.accountid);
        setQueryid(builder.queryid);
        setmBaseUrl(builder.mBaseUrl);
        setPostData(builder.postData);
        setActionname(builder.actionName);
        setSessionid(builder.sessionid);
        setOrderid(builder.orderid);
    }

    public String getParams() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(tag_accountid, getAccountid());
        map.put(tag_action_name, getActionname());
        map.put(tag_deviceId, getDeviceid());
        map.put(tag_json, getjson());
        map.put(tag_key, getKey());
        map.put(tag_lat, String.valueOf(latitude));
        map.put(tag_long, String.valueOf(longitude));
        try {
            map.put(tag_query, URLEncoder.encode(getQuery(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put(tag_queryid, getQueryid());
        map.put(tag_service, getService());
        map.put(tag_timestamp, String.valueOf(System.currentTimeMillis()) + "000");
        map.put(tag_type, getType());
        map.put(tag_userid, getUserid());
        map.put(tag_version, verNum);

        String params = null;
        try {
            params = AuthenUtil.genSign(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }


    public String getContextid() {
        if (contextid == null) {
            return "";
        }
        return contextid;
    }


    public HashMap<String, String> postParams() {
        HashMap<String, String> map = new HashMap<>();
        map.put(tag_service, getService());
        map.put(tag_query, getQuery());
        map.put(tag_key, getKey());
        map.put(tag_deviceId, getDeviceid());
        map.put(tag_long, String.valueOf(getLongitude()));
        map.put(tag_lat, String.valueOf(getLatitude()));
        map.put(tag_type, getType());
        map.put(tag_userid, getUserid());
        map.put(tag_accountid, getAccountid());
        map.put(tag_queryid, getQueryid());
        map.put(tag_version, verNum);
        map.put(tag_action_name, getActionname());
        map.put(tag_json, getjson());

        return map;
    }

    public class JsonObj {
        public String json;
    }

    public RequestBody generateRequestBody() {

        JsonObj j = new JsonObj();
        j.json = json;

        String params = "";

        if (postData != null) {
            params = mGson.toJson(j);
        }

        Log.d(TAG, "generateRequestBody, params: " + params);
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(REQUEST_TAG, params);
        return builder.build();
    }

    public NliAuthenRequest() {
        this.mGson = new Gson();
        mBaseUrl = "Sale.handler" + "?" + getParams();
    }

    public String getActionname() {
        if (actionname == null) {
            return "";
        }
        return actionname;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }

    public String getSessionid() {
        if (sessionid == null) {
            return "";
        }
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getOrderid() {
        if (orderid == null) {
            return "";
        }
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getjson() {
        if (json != null) {
            return json;
        } else {
            return "";
        }
    }

    public void setjson(String json) {
        this.json = json;
    }

    public String getmBaseUrl() {
        return mBaseUrl;
    }

    public void setmBaseUrl(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    public String getType() {
        if (type == null) {
            return "";
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuery() {
        if (query == null) {
            return "";
        }
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getService() {
        if (service == null) {
            return "";
        }
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeviceid() {
        if (deviceid == null) {
            return "";
        }
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getUserid() {
        if (userid == null) {
            return "";
        }
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAccountid() {
        if (accountid == null) {
            return "";
        }
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getQueryid() {
        if (queryid == null) {
            return "";
        }
        return queryid;
    }

    public void setQueryid(String queryid) {
        this.queryid = queryid;
    }

    public Object getPostData() {
        return postData;
    }

    public void setPostData(Object postData) {
        this.postData = postData;
    }

    public static final class Builder {
        private String type;
        private String query;
        private String service;
        private String key;
        private String deviceid;
        private Double longitude;
        private Double latitude;
        private String userid;
        private String accountid;
        private String queryid;
        private String mBaseUrl;
        private Object postData;
        private String actionName;
        private String sessionid;
        private String orderid;
        private String json;

        public Builder() {
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder setService(String service) {
            this.service = service;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setDeviceid(String deviceid) {
            this.deviceid = deviceid;
            return this;
        }

        public Builder setLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setUserid(String userid) {
            this.userid = userid;
            return this;
        }

        public Builder setAccountid(String accountid) {
            this.accountid = accountid;
            return this;
        }

        public Builder setQueryid(String queryid) {
            this.queryid = queryid;
            return this;
        }

        public Builder setmBaseUrl(String mBaseUrl) {
            this.mBaseUrl = mBaseUrl;
            return this;
        }

        public Builder setPostData(Object postData) {
            this.postData = postData;
            return this;
        }

        public Builder setActionName(String actionName) {
            this.actionName = actionName;
            return this;
        }

        public Builder setSessionid(String sessionid) {
            this.sessionid = sessionid;
            return this;
        }

        public Builder setOrderid(String orderid) {
            this.orderid = orderid;
            return this;
        }

        public Builder setJson(String json) {
            this.json = json;
            return this;
        }

        public NliAuthenRequest build() {
            return new NliAuthenRequest(this);
        }
    }
}
