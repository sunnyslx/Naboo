package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by bobge on 2018/4/13.
 */

public class IotSaveOrUpdateParams extends BaseParams {


    /**
     * uid : 1
     * key : XXXXXXxx
     * type : 0
     * name : 开灯
     * remark : 开灯语句触发
     * query : 开灯，打开灯，把灯打开
     * actionList : [{"deviceId":"222","action":"turnOn","value":"","delay":1}]
     */

    private String uid;
    private String key;
    private String type;
    private String name;
    private String remark;
    private String query;
    private List<ActionListBean> actionList;

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", getUid());
        hashMap.put("key", getKey());
        hashMap.put("type", getType());
        hashMap.put("name", getName());
        hashMap.put("remark", getRemark());
        hashMap.put("query", getQuery());
        hashMap.put("actionList", GsonObjectDeserializer.produceGson().toJson(getActionList()));
        LogUtils.i("iot", "IotSaveOrUpdateParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return hashMap;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<ActionListBean> getActionList() {
        return actionList;
    }

    public void setActionList(List<ActionListBean> actionList) {
        this.actionList = actionList;
    }

    public static class ActionListBean {
        /**
         * deviceId : 222
         * action : turnOn
         * value :
         * delay : 1
         */

        private String deviceId;
        private String action;
        private String value;
        private int delay;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

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

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }
    }
}
