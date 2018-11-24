package net.imoran.tv.sdk.network;

import android.content.Context;

/**
 * Created by jia on 16/12/15.
 * NetClient 的工厂类，实现网络访问工具的实现完全与使用方隔离
 */

public class NetClientFactory {

    public static NetClient createNetClient(Context context){
        return new OkHttpNetClient(context);
    }
}
