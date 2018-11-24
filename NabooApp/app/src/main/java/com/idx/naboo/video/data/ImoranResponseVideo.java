package com.idx.naboo.video.data;

import com.google.gson.annotations.SerializedName;

/**
 * json第一次数据
 * Created by sunny on 18-3-14.
 */

public class ImoranResponseVideo {
    @SerializedName("data")
    private VideoData videoData;

    public VideoData getVideoData() {
        return videoData;
    }

    public void setVideoData(VideoData videoData) {
        this.videoData = videoData;
    }
}
