package net.imoran.tv.sdk.network.requestdata;

/**
 * Created by bobge on 2017/7/12.
 * 用于判断返回的json数据是否正确
 */
public interface IResponseJudger {
    public boolean judge(String data);
}
