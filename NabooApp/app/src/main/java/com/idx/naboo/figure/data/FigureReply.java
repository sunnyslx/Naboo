package com.idx.naboo.figure.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by darkmi on 3/26/18.
 * 第四层json数据
 */

public class FigureReply {

    @SerializedName("person_info")
    private List<Figure> figure;

    @SerializedName("people_detail_relation")
    private List<RelationFigure> relation;

    @SerializedName("people_detail")
    private List<DetailFigure> detail;

    @SerializedName("person")
    private List<Person> people;

    @SerializedName("default")
    private List<DefaultFigure> defaultFigure;

    public List<Person> getPeople() {
        return people;
    }

    public List<DefaultFigure> getDefaultFigure() {
        return defaultFigure;
    }

    public List<DetailFigure> getDetail() {
        return detail;
    }

    public List<RelationFigure> getRelation() {
        return relation;
    }

    public List<Figure> getFigure() {
        return figure;
    }
}
