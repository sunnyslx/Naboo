package net.imoran.tv.sdk.network;

import java.util.Map;

/**
 * http客户端的抽象,使本地网络请求与不同的网络库解耦合,不依赖具体的实现库
 */
public interface NetClient {
    public final static int TYPE_GET = 1;

    public final static int TYPE_POST = 2;

    /**
     * 异步请求接口
     * @param type
     * @param url
     * @param params
     * @param requestCallBack
     */
//    public void request(int type, String url, Map<String, String> params, RequestCallBack requestCallBack);

    /**
     * 异步请求接口
     * @param type 请求类型 get、post等
     * @param url 请求url
     * @param params 请求的参数 key-value 对
     * @param requestCallBack 回调接口
     */
    public void requestString(int type, String url, Map<String, String> params, RequestCallBack requestCallBack);

    /**
     * 同步请求接口
     * @param type 请求类型 get、post等
     * @param url 请求url
     * @param params 请求的参数 key-value 对
     * @return
     */
    public String request(int type, String url, Map<String, String> params);
}
