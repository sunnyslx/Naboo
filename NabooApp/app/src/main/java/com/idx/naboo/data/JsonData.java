package com.idx.naboo.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hayden on 18-3-15.
 */

public class JsonData {
    //意图
    private String intention;
    //种类
    private String domain;
    //详细类别
    private String type;

    private String queryId;

    private String action;

    //code
    private int errorCode;
    //语音
    private String tts;
    //对象
    private JSONObject content;

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    //周边POI对象
    private List<Point> pointList;

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    private JSONArray musicMode;
    private String mode;

    public JsonData() {
    }

    public JSONArray getMusicMode() {
        return musicMode;
    }

    public void setMusicMode(JSONArray musicMode) {
        this.musicMode = musicMode;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
