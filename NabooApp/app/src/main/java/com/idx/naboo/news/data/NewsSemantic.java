package com.idx.naboo.news.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/30/18.
 */

public class NewsSemantic {
    @SerializedName("index_number")
    private int indexNumber;
    @SerializedName("NewsType")
    private String[] newsType;

    public String[] getNewsType() {
        return newsType;
    }

    public int getIndexNumber() {
        return indexNumber;
    }
}
