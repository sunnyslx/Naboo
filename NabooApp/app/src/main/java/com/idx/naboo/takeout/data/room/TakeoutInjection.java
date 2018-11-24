package com.idx.naboo.takeout.data.room;

import android.content.Context;

import com.idx.naboo.videocall.friend.data.NabooDatabase;
import com.idx.naboo.videocall.utils.NabooExecutors;

/**
 * 通过该类拿到外卖表操作对象-TakeoutRepository
 * Created by danny on 4/21/18.
 */

public class TakeoutInjection {
    public static TakeoutRepository getInstance(Context context){
        NabooDatabase nabooDatabase=NabooDatabase.getInstance(context);
        TakeoutRepository repository=TakeoutRepository.getInstance(LocalTakeoutSellerDataSource.getInstance(new NabooExecutors(),nabooDatabase.takeoutSellerDao()));
        return repository;
    }
}
