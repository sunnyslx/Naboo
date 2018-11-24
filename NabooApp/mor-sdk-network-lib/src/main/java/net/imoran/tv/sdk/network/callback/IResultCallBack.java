package net.imoran.tv.sdk.network.callback;


import okhttp3.Request;

/**
 * Created by bobge on 2017/7/12.
 */
public interface IResultCallBack<T> {
    //下载进度
    public abstract void onProgress(float total, float current);

    //下载错误
    public abstract void onError(Request request, String errorMsg);

    //下载成功
    public abstract void onResponse(T response);
}
