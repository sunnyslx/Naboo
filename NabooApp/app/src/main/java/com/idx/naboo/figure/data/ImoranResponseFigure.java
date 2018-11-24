package com.idx.naboo.figure.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/26/18.
 */

public class ImoranResponseFigure {
    @SerializedName("data")
    private FigureData mFigureData;

    public FigureData getFigureData() {
        return mFigureData;
    }
}
