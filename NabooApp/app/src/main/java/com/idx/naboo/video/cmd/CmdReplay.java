package com.idx.naboo.video.cmd;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 18-5-5.
 */

public class CmdReplay {
    @SerializedName("fast_forward")
    private String[] fastForward;

    public String[] getFastForward() {
        return fastForward;
    }
}
