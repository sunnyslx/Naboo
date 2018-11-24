package com.idx.naboo.video.cmd;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 18-5-5.
 */

public class MoranResponseCmd {
    @SerializedName("data")
    private CmdData cmdData;

    public CmdData getCmdData() {
        return cmdData;
    }
}
