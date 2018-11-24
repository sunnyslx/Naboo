package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/19.
 */

public class IotPushMsgHeaderParams extends BaseParams {
    private String type;
    private String action;
    private String version;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("type", getType());
        hashMap.put("action", getAction());
        hashMap.put("version", getVersion());
        LogUtils.i("iot", "IotPushMsgHeaderParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }
}
