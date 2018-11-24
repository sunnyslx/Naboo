package com.idx.naboo.news.data;

import com.google.gson.annotations.SerializedName;

/**
 * news的第二层json数据(data下)
 * Created by darkmi on 3/19/18.
 */

public class NewsData {
    @SerializedName("content")
    private NewsContent newsContent;
    private String domain;

    public NewsContent getNewsContent() {
        return newsContent;
    }

    public String getDomain() {
        return domain;
    }
}
