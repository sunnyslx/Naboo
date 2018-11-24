package com.idx.naboo.music.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第一层json数据
 * Created by danny on 3/2/18.
 */

public class ImoranResponseMusic {
    @SerializedName("data")
    private DataMusic data;

    public DataMusic getMusicData() {return data;}
}
