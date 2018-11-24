package com.idx.naboo.video.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 18-4-19.
 */

public class Semantic {
    @SerializedName("content_type")
    String[] content_type;

    public String[] getContent_type() {
        return content_type;
    }
}
