package com.idx.naboo.takeout.data.takeout;

import com.google.gson.annotations.SerializedName;

/**
 * 第六层json数据-TakeoutShop
 * 店铺活动信息
 * Created by danny on 4/16/18.
 */

public class TSActivity {
    @SerializedName("begin_date")
    private String mBeginDate;//开始日期
    @SerializedName("end_date")
    private String mEndDate;//结束日期
    @SerializedName("name")
    private String mName;//活动名称
    @SerializedName("description")
    private String mDescription;//活动内容

    public String getBeginDate() {return mBeginDate;}

    public String getEndDate() {return mEndDate;}

    public String getName() {return mName;}

    public String getDescription() {return mDescription;}
}
