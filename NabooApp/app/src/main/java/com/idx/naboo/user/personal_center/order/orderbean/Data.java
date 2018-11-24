package com.idx.naboo.user.personal_center.order.orderbean;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Data {
    private String action;

    private Content content;

    private String domain;

    private String intention;

    private String query;

    private String queryid;

    private String terms;

    public void setAction(String action){
        this.action = action;
    }
    public String getAction(){
        return this.action;
    }
    public void setContent(Content content){
        this.content = content;
    }
    public Content getContent(){
        return this.content;
    }
    public void setDomain(String domain){
        this.domain = domain;
    }
    public String getDomain(){
        return this.domain;
    }
    public void setIntention(String intention){
        this.intention = intention;
    }
    public String getIntention(){
        return this.intention;
    }
    public void setQuery(String query){
        this.query = query;
    }
    public String getQuery(){
        return this.query;
    }
    public void setQueryid(String queryid){
        this.queryid = queryid;
    }
    public String getQueryid(){
        return this.queryid;
    }
    public void setTerms(String terms){
        this.terms = terms;
    }
    public String getTerms(){
        return this.terms;
    }
}
