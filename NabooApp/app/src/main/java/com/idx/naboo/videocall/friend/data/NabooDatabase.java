package com.idx.naboo.videocall.friend.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.idx.naboo.takeout.data.room.TakeoutSeller;
import com.idx.naboo.takeout.data.room.TakeoutSellerDao;
import com.idx.naboo.videocall.call.data.MissedCall;
import com.idx.naboo.videocall.call.data.MissedCallDao;

/**
 * 数据库
 * Created by danny on 3/30/18.
 */
@Database(entities = {HxUser.class, Friend.class, TakeoutSeller.class, MissedCall.class},version = 1,exportSchema = false)
public abstract class NabooDatabase extends RoomDatabase {
    private static NabooDatabase sInstance;
    public abstract UserDao uerDao();
    public abstract TakeoutSellerDao takeoutSellerDao();
    public abstract MissedCallDao missedCallDao();

    private static final Object sLock =new Object();

    public static NabooDatabase getInstance(Context context){
        if (sInstance==null){
            synchronized (sLock){
                if (sInstance==null){
                    sInstance=Room.databaseBuilder(context.getApplicationContext()
                            ,NabooDatabase.class,"naboo.db").build();
                }
            }
        }
        return sInstance;
    }

    public void close(){
        if (sInstance!=null){
            sInstance.close();
        }
    }
}
