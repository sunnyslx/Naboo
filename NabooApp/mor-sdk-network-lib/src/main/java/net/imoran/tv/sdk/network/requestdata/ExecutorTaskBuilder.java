package net.imoran.tv.sdk.network.requestdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import net.imoran.tv.sdk.network.base.ApiConstants;
import net.imoran.tv.sdk.network.callback.IResponseCallBack;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2017/7/12.
 */
public class ExecutorTaskBuilder {
    private IResponseCallBack mResponseDataCallBack;
    private String mPath;
    private int mRequestCode;
    private FProtocol.NetDataProtocol.DataMode mDataAccessMode = FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET_NO_CACHE;
    private IResponseJudger mJudger;
    /*private String mServiceKey;*/

    Context mContext;// 由于访问sp，设置dataAccessMode时会被要求传入
    private FProtocol.HttpMethod mMethod = FProtocol.HttpMethod.GET;
    private HashMap<String, String> mPostParameters = null;
    private ArrayMap<Integer, ExecutorTask> taskSet;
    private HashMap<String, String> headers = null;
    //鉴权参数
    private NliAuthenRequest nliAuthenRequest;

    public ExecutorTaskBuilder(Context context, ArrayMap<Integer, ExecutorTask> taskSet) {
        mContext = context;
        this.taskSet = taskSet;
    }

    public ExecutorTask build() {
        final ExecutorTask task = new ExecutorTask(mContext,
                mResponseDataCallBack, mPath, mRequestCode, mMethod, mPostParameters)
                .setDataAccessMode(mDataAccessMode)
                .setJudger(mJudger)
                .setNliAuthenRequest(nliAuthenRequest)
                .setHeaders(headers);
               /* .setExecutorSerice(mServiceKey)*/
        taskSet.put(mRequestCode, task);
        LogUtils.i("RequestHelper", "build task " + task + ":" + mRequestCode);

        return task;
    }

    public ExecutorTaskBuilder setCallBack(IResponseCallBack responseDataCallBack) {
        this.mResponseDataCallBack = responseDataCallBack;
        return this;
    }


    public ExecutorTaskBuilder setPath(@NonNull String path) {
        this.mPath = path;
        return this;
    }


    public ExecutorTaskBuilder setRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public ExecutorTaskBuilder setMethod(FProtocol.HttpMethod method) {
        this.mMethod = method;
        return this;
    }

    public ExecutorTaskBuilder setPostParameters(HashMap<String, String> postParameters) {
        this.mPostParameters = postParameters;
        return this;
    }

    public ExecutorTaskBuilder setDataAccessMode(FProtocol.NetDataProtocol.DataMode dataAccessMode) {
        this.mDataAccessMode = dataAccessMode;
        return this;
    }

    public ExecutorTaskBuilder setJudger(IResponseJudger judger) {
        this.mJudger = judger;
        return this;
    }

    public ExecutorTaskBuilder setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }
    /*public ExecutorTaskBuilder setExecutorService(String serviceKey){
        this.mServiceKey = serviceKey;
        return this;
    }*/

 /*   public ExecutorTaskBuilder setContext(Context context){
        this.mContext = context;
        return this;
    }*/
}
