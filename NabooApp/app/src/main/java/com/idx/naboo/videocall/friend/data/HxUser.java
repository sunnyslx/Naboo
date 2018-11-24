package com.idx.naboo.videocall.friend.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * 用户表
 * Created by danny on 3/30/18.
 */
@Entity(tableName = "user")
public class HxUser {
    @PrimaryKey
    @NonNull
    public String id;
    public String account;
    public String alias;

    @Override
    public String toString() {
        return "HxUser{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
