package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/19.
 */

public class IotDeviceContrParams extends BaseParams {
    private String uid;
    private String deviceId;
    private String action;
    private String value;
    private String time;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", getUid());
        hashMap.put("deviceId", getDeviceId());
        hashMap.put("action", getAction());
        hashMap.put("value", getValue());
        hashMap.put("time", getTime());
        LogUtils.i("iot", "IotDelDeviceParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }
}
