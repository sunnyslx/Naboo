package com.idx.naboo.video.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * json第五层数据解析
 * Created by sunny on 18-3-14.
 */

public class Movie implements Serializable{
    @SerializedName("title")
    private String title;
    @SerializedName("actor")
    private String[] actor;
    @SerializedName("actor_info")
    private List<ActorInfo> actorInfo;
    @SerializedName("director")
    private String[] director;
    @SerializedName("director_info")
    private List<DirectorInfo> directorInfo;
    @SerializedName("duration")
    private String duration;
    @SerializedName("grade")
    private String grade;
    @SerializedName("play_url")
    private String playUrl;
    @SerializedName("movie_id")
    private String movieId;
    @SerializedName("iconaddress")
    private String iconaddress;
    @SerializedName("horizontal_pic")
    private String horizontal_pic;
    @SerializedName("story")
    private String story;
    @SerializedName("total_episode")
    private String total_episode;
    public String getTitle() {
        return title;
    }

    public String[] getActor() {
        return actor;
    }

    public List<ActorInfo> getActorInfo() {
        return actorInfo;
    }

    public String[] getDirector() {
        return director;
    }

    public List<DirectorInfo> getDirectorInfo() {
        return directorInfo;
    }

    public String getDuration() {
        return duration;
    }

    public String getGrade() {
        return grade;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getIconaddress() {
        return iconaddress;
    }

    public String getHorizontal_pic() {
        return horizontal_pic;
    }

    public String getStory() {
        return story;
    }

    public String getTotal_episode() {
        return total_episode;
    }
}
