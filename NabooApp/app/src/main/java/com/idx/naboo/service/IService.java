package com.idx.naboo.service;

import com.idx.naboo.service.listener.DataListener;

import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

/**
 * Created by derik on 18-4-14.
 * Email: weilai0314@163.com
 */

public interface IService {
    /**
     * 设置指令数据监听器
     * @param listener
     */
    void setDataListener(DataListener listener);

    /**
     * 数据请求接口，不经过语音交互
     * @param text
     * @param callback
     */
    void requestData(String text, NLIRequest.onRequest callback);

    /**
     * getJson for news
     * @return
     */
    String getJson();
}
