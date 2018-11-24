package com.idx.naboo.video.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Json第四层解析
 * Created by sunny on 18-3-14.
 */

public class VideoReply {

    @SerializedName("movie")
    private List<Movie> moviesList;

    public List<Movie> getMoviesList() {
        return moviesList;
    }
}
