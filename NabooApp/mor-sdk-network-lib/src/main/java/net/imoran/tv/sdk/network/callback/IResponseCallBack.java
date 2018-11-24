package net.imoran.tv.sdk.network.callback;


import net.imoran.tv.sdk.network.requestdata.FProtocol;

/**
 * Created by bobge on 2017/7/12.
 */
public interface IResponseCallBack {

    void resultDataSuccess(int requestCode, String data);

    void resultDataMistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus responseStatus, String errorMessage);
}
