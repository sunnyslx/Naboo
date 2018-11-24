package com.idx.naboo.dish.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 18-3-23.
 */

public class Steps {
    @SerializedName("des")
    private String des;
    @SerializedName("pic")
    private String pic;
    @SerializedName("index")
    private String index;
    public String getDes() {
        return des;
    }

    public String getPic() {
        return pic;
    }

    public String getIndex() {
        return index;
    }
}
