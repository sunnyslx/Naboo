package com.idx.naboo.user.personal_center.order.orderbean;

import com.idx.naboo.weather.data.Summary;

import java.util.List;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Content {
    private String display;

    private List<Display_guide> display_guide ;

    private int error_code;

    private Reply reply;

    private Semantic semantic;

    private Summary summary;

    private int total_count;

    private String tts;

    private String type;

    public void setDisplay(String display){
        this.display = display;
    }
    public String getDisplay(){
        return this.display;
    }
    public void setDisplay_guide(List<Display_guide> display_guide){
        this.display_guide = display_guide;
    }
    public List<Display_guide> getDisplay_guide(){
        return this.display_guide;
    }
    public void setError_code(int error_code){
        this.error_code = error_code;
    }
    public int getError_code(){
        return this.error_code;
    }
    public void setReply(Reply reply){
        this.reply = reply;
    }
    public Reply getReply(){
        return this.reply;
    }
    public void setSemantic(Semantic semantic){
        this.semantic = semantic;
    }
    public Semantic getSemantic(){
        return this.semantic;
    }
    public void setSummary(Summary summary){
        this.summary = summary;
    }
    public Summary getSummary(){
        return this.summary;
    }
    public void setTotal_count(int total_count){
        this.total_count = total_count;
    }
    public int getTotal_count(){
        return this.total_count;
    }
    public void setTts(String tts){
        this.tts = tts;
    }
    public String getTts(){
        return this.tts;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
