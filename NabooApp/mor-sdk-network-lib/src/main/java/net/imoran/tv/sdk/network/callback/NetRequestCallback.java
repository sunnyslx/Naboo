package net.imoran.tv.sdk.network.callback;

import net.imoran.tv.sdk.network.requestdata.FProtocol;

/**
 * Created by bobge on 2018/4/13.
 * 网络请求回调对外接口
 */

public interface NetRequestCallback {
    public void success(int requestCode, String data);

    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage);
}
