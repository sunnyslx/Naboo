package com.idx.naboo.videocall.friend.data;

import android.content.Context;

import com.idx.naboo.videocall.utils.NabooExecutors;

/**
 * 通过该类拿到好友表操作对象-FriendRepository
 * Created by danny on 3/31/18.
 */

public class FriendInjection {
    public static FriendRepository getInstance(Context context){
        NabooDatabase database= NabooDatabase.getInstance(context);
        FriendRepository repository= FriendRepository.getInstance(LocalFriendDataSource.getInstance(new NabooExecutors(),database.uerDao()));
        return repository;
    }
}
