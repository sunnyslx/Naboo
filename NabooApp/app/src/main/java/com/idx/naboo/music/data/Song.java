package com.idx.naboo.music.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by danny on 3/8/18.
 */

public class Song {
    private String album;
    private String[] singer;
    private String name;
    @SerializedName("pic_url")
    private String picUrl;
    @SerializedName("song_url")
    private String songUrl;

    public String getAlbum() {return album;}

    public String[] getSinger() {return singer;}

    public String getName() {return name;}

    public String getPicUrl() {return picUrl;}

    public String getSongUrl() {return songUrl;}
}
