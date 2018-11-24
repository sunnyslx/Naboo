package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/19.
 */

public class IotDelDeviceHeaderParams extends BaseParams {
    private String messageId;
    private String action;
    private String accessToken;
    private String type;
    private String version;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        hashMap.put("messageId", getMessageId());
        hashMap.put("action", getAction());
        hashMap.put("accessToken", getAccessToken());
        hashMap.put("type", getType());
        hashMap.put("version", getVersion());
        LogUtils.i("iot", "IotDelDeviceHeaderParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }
}
