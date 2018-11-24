package com.idx.naboo.news.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/19/18.
 */

public class NewsDetail {
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("abstracts")
    private String abstracts;
    @SerializedName("publish_date")
    private String publishDate;
    @SerializedName("source")
    private String source;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getSource() {
        return source;
    }
}
