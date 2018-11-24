package com.idx.naboo.dish.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第五层json数据
 * Created by sunny on 18-3-21.
 */

public class Dish {
    @SerializedName("name")
    private String name;

    @SerializedName("material")
    private List<String> material;
    @SerializedName("side_material")
    private List<String> side_material;
    @SerializedName("steps")
    private List<Steps> steps;

    public String getName() {
        return name;
    }

    public List<String> getMaterial() {
        return material;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public List<String> getSide_material() {
        return side_material;
    }
}
