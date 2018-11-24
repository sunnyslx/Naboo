package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/13.
 */

public class IotUpdatePositionParams extends BaseParams {
    private String uid;
    private String deviceId;
    private String position;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", getUid());
        hashMap.put("deviceId", getDeviceId());
        hashMap.put("position", getPosition());
        LogUtils.i("iot", "IotUpdatePositionParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }
}
