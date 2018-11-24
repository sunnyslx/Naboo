package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/13.
 */

public class IotUpdateParams extends BaseParams {
    private String uid;
    private String deviceId;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", getUid());
        hashMap.put("deviceId", getDeviceId());
        hashMap.put("name", getName());
        LogUtils.i("iot", "IotUpdateParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }
}
