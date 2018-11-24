package com.idx.naboo.video.cmd;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 18-5-5.
 */

public class CmdData {
    @SerializedName("content")
    private CmdContent cmdContent;

    public CmdContent getCmdContent() {
        return cmdContent;
    }
}
