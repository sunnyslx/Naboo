package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/13.
 */

public class IotSecenDeleteParams extends BaseParams {
    private String uid;
    private String sceneId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", getUid());
        hashMap.put("sceneId", getSceneId());
        LogUtils.i("iot", "IotSecenDeleteParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }
}
