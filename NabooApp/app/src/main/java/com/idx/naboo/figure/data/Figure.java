package com.idx.naboo.figure.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by darkmi on 3/26/18.
 */

public class Figure {
    @SerializedName("persons")
    private List<Personal> personal;

    public List<Personal> getPersonal() {
        return personal;
    }
}
