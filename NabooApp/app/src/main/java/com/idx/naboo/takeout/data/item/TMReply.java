package com.idx.naboo.takeout.data.item;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据-TMContent
 * Created by danny on 4/18/18.
 */

public class TMReply {
    @SerializedName("takeoutmenu")
    private List<TakeoutMenu> mMenus;

    public List<TakeoutMenu> getMenus() {return mMenus;}
}
