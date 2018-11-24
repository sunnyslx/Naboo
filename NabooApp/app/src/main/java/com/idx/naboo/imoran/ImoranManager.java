package com.idx.naboo.imoran;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.baidu.android.common.logging.Log;
import com.xiaomor.mor.app.common.log.ExceptionReporter;
import com.xiaomor.mor.app.common.log.ReporterFactory;

import net.imoran.sdk.Imoran;
import net.imoran.sdk.entity.info.ClearContextEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.impl.RequestCallback;
import net.imoran.sdk.service.nli.NLIRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Imoran NLI
 * Created by danny on 3/6/18.
 */

public class ImoranManager {
    private static final String TAG = ImoranManager.class.getSimpleName();
    private volatile static ImoranManager INSTANCE = null;
    private ExceptionReporter mReporter;
    private Imoran mImoran;
    private static final String mKey = "C7484C880C551D33";
    private static final String mAccountId = "222";

    public static ImoranManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ImoranManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImoranManager(context, mKey, mAccountId);
                }
            }
        }
        return INSTANCE;
    }

    private ImoranManager(Context context, String key, String accountId) {
        try {
            mReporter = ReporterFactory.createBuglyReporter(context);
            mImoran = Imoran.createImoran(context, key, accountId);
        } catch (Throwable throwable) {
            Log.e(TAG, "NLIWorker: 创建Imoran示例失败");
            mReporter.postCatchedException(throwable);
        }
    }

    /**
     * 发送数据至蓦然伺服器
     *
     * @param query 发送文本
     */
    public void tell(String query) {
        if (mImoran != null) {
            mImoran.tell(query);
        }
    }

//    /**
//     * 发送数据至蓦然伺服器，并为此次事件，设置数据回调监听器
//     *
//     * @param query    发送文本
//     * @param callback 数据监听器
//     */
//    public void tell(String query, NLIRequest.onRequest callback) {
//        if (mImoran != null) {
//            mImoran.tell(query, callback);
//        }
//    }

    public void tell(String query, VUIDataEntity vuiDataEntity,  NLIRequest.onRequest callback) {
        if (mImoran != null) {
            mImoran.tell(query, vuiDataEntity, callback);
        }
    }

    /**
     * @param aMapLocation
     */
    public void setLocation(AMapLocation aMapLocation) {
        if (mImoran != null) {
            mImoran.updateLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        }
    }

    public void clearContext(String pageId, String queryId, RequestCallback callback) {
        if (mImoran != null) {
            mImoran.createImoranMessage().clearWechatContext();
            ClearContextEntity clearContextEntity = new ClearContextEntity();
            ClearContextEntity.ClearContextBean clearContextBean = new ClearContextEntity.ClearContextBean();
            List<ClearContextEntity.ClearContextBean.ScenesBean> list = new ArrayList<>();
            ClearContextEntity.ClearContextBean.ScenesBean bean = new ClearContextEntity.ClearContextBean.ScenesBean();
            bean.setPage_id(pageId);
            bean.setQuery_id(queryId);
            clearContextBean.setScenes(list);
            clearContextEntity.setClear_context(clearContextBean);
            mImoran.createImoranMessage().cancelContext(clearContextEntity, callback);
        }
    }

    /**
     * 销毁单实例
     */
    public synchronized void destroy() {
        if (mImoran != null) {
            mImoran.destroy();
            mImoran = null;
        }
        INSTANCE = null;
    }

}
