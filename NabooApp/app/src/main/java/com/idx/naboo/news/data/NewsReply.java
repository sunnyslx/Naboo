package com.idx.naboo.news.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * reply 下的第四层json
 * Created by darkmi on 3/19/18.
 */

public class NewsReply {
    @SerializedName("news_detail")
    private List<NewsDetail> newsDetail;

    public List<NewsDetail> getNewsDetail() {
        return newsDetail;
    }
}
