package com.idx.naboo.music.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 第四层json数据：Content下
 * Created by danny on 3/2/18.
 */

public class ReplyMusic {
    @SerializedName("song")
    private List<Song> mSongs;

    public List<Song> getSongs() {return mSongs;}
}
