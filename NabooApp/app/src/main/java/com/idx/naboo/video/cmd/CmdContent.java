package com.idx.naboo.video.cmd;

import com.google.gson.annotations.SerializedName;

/**
 * Created by franck on 18-5-5.
 */

public class CmdContent {
    @SerializedName("reply")
    private CmdReplay replay;

    public CmdReplay getReplay() {
        return replay;
    }
}
