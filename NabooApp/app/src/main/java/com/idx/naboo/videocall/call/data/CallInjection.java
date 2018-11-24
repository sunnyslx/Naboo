package com.idx.naboo.videocall.call.data;

import android.content.Context;

import com.idx.naboo.videocall.friend.data.FriendRepository;
import com.idx.naboo.videocall.friend.data.LocalFriendDataSource;
import com.idx.naboo.videocall.friend.data.NabooDatabase;
import com.idx.naboo.videocall.utils.NabooExecutors;

/**
 * 通过该类拿到未接电话表操作对象-CallRepository
 * Created by danny on 3/31/18.
 */

public class CallInjection {
    public static CallRepository getInstance(Context context){
        NabooDatabase database= NabooDatabase.getInstance(context);
        CallRepository repository= CallRepository.getInstance(LocalCallDataSource.getInstance(new NabooExecutors(),database.missedCallDao()));
        return repository;
    }
}
