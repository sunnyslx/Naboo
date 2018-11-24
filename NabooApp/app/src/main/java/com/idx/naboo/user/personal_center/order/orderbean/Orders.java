package com.idx.naboo.user.personal_center.order.orderbean;

import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by ryan on 18-5-12.
 * Email: Ryan_chan01212@yeah.net
 */

public class Orders {
    private String accountId;

    private List<Attributes_1> attributes ;

    private int commodityType;

    private String createTime;

    private List<Items> items ;

    private String merchantId;

    private String mobile;

    private String orderCode;

    private String orderName;

    private int status;

    private int statusGroup;

    private String statusGroupName;

    private String statusName;

    private int totalPrice;

    public void setAccountId(String accountId){
        this.accountId = accountId;
    }
    public String getAccountId(){
        return this.accountId;
    }
    public void setAttributes(List<Attributes_1> attributes){
        this.attributes = attributes;
    }
    public List<Attributes_1> getAttributes(){
        return this.attributes;
    }
    public void setCommodityType(int commodityType){
        this.commodityType = commodityType;
    }
    public int getCommodityType(){
        return this.commodityType;
    }
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return this.createTime;
    }
    public void setItems(List<Items> items){
        this.items = items;
    }
    public List<Items> getItems(){
        return this.items;
    }
    public void setMerchantId(String merchantId){
        this.merchantId = merchantId;
    }
    public String getMerchantId(){
        return this.merchantId;
    }
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    public String getMobile(){
        return this.mobile;
    }
    public void setOrderCode(String orderCode){
        this.orderCode = orderCode;
    }
    public String getOrderCode(){
        return this.orderCode;
    }
    public void setOrderName(String orderName){
        this.orderName = orderName;
    }
    public String getOrderName(){
        return this.orderName;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setStatusGroup(int statusGroup){
        this.statusGroup = statusGroup;
    }
    public int getStatusGroup(){
        return this.statusGroup;
    }
    public void setStatusGroupName(String statusGroupName){
        this.statusGroupName = statusGroupName;
    }
    public String getStatusGroupName(){
        return this.statusGroupName;
    }
    public void setStatusName(String statusName){
        this.statusName = statusName;
    }
    public String getStatusName(){
        return this.statusName;
    }
    public void setTotalPrice(int totalPrice){
        this.totalPrice = totalPrice;
    }
    public int getTotalPrice(){
        return this.totalPrice;
    }

}
