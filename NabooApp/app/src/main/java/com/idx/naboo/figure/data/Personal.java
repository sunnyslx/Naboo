package com.idx.naboo.figure.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/26/18.
 */

public class Personal {
    private String name;
    private String country;
    private String birthday;
    private String birthplace;
    @SerializedName("baike_info")
    private String baikeInfo;
    @SerializedName("brief")
    private String brief;
    @SerializedName("iconaddress_l")
    private String pic;

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getBaikeInfo() {
        return baikeInfo;
    }

    public String getBrief() {
        return brief;
    }

    public String getPic() {
        return pic;
    }
}
