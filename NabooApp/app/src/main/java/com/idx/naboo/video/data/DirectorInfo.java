package com.idx.naboo.video.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sunny on 18-3-26.
 */

public class DirectorInfo implements Serializable {
    @SerializedName("label")
    private String label;
    @SerializedName("name")
    private String  name;
    @SerializedName("pic")
    private String pic;

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getPic() {
        return pic;
    }
}
