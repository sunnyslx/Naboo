package net.imoran.tv.sdk.network;


/**
 * http 请求的回调
 */
public interface RequestCallBack<T> {

    public void onResult(T result);

    public void onError(String message, int errorCode);
}
