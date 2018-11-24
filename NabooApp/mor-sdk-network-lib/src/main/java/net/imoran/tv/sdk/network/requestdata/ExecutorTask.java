package net.imoran.tv.sdk.network.requestdata;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;

import net.imoran.tv.sdk.network.callback.IResponseCallBack;
import net.imoran.tv.sdk.network.utils.CharToUrlTools;
import net.imoran.tv.sdk.network.utils.LogUtils;
import net.imoran.tv.sdk.network.utils.NetWorkUtil;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bobge on 2017/7/12.
 */

public class ExecutorTask implements Runnable {
    private static InternalHandler sHandler;
    private IResponseCallBack mIResponseCallBack;
    private String path;
    private int requestCode;

    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private static final String LOG_TAG = ExecutorTask.class.getSimpleName();

    /**
     * 直接从网络取数据不需要本地存储 DATA_FROM_NET_NO_CACHE
     * <p/>
     * 直接从网络取数据需要本地存储 DATA_FROM_NET
     * <p/>
     * 直接从本地存储拿数据 DATA_FROM_CACHE
     * <p/>
     * 先从本地存储取数据显示出来 再去网络取数据更新界面并本地存储 DATA_UPDATE_CACHE
     */
    private FProtocol.NetDataProtocol.DataMode dataAccessMode = FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET_NO_CACHE;
    private IResponseJudger judger;
    Context mContext;// 由于访问sp，设置dataAccessMode时会被要求传入
    private FProtocol.HttpMethod method = FProtocol.HttpMethod.GET;
    private HashMap<String, String> postParameters = null;
    private HashMap<String, String> headers = null;

    private boolean isCache = false;
    //鉴权的参数
    private NliAuthenRequest nliAuthenRequest;

    private ExecutorService mExecutorService = CoreExecutorService.getDefaultExecutorService();

    /**
     * @param context              context
     * @param responseDataCallBack 回调
     * @param path                 url
     * @param requestCode          请求码
     * @param method               get post delete put
     * @param postParameters       postParameters
     */

    public ExecutorTask(Context context, final IResponseCallBack responseDataCallBack, final String path, final int requestCode, final FProtocol.HttpMethod method, final HashMap<String, String> postParameters) {
        this.mIResponseCallBack = responseDataCallBack;
        this.path = CharToUrlTools.toUtf8String(path);
        this.requestCode = requestCode;
        this.mContext = context;
        this.method = method;
        this.postParameters = postParameters;
    }


    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        String json;
        try {
            if (isCancelled()) {
                return;
            }
            if (!NetWorkUtil.isConnect(mContext) && (dataAccessMode == FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET_NO_CACHE || dataAccessMode == FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET)) {
                //断网处理
                resultDataMistake(requestCode, FProtocol.NetDataProtocol.ResponseStatus.LOAD_NET_DISCONNENT, "");
                return;
            }
            //如果需要鉴权则拼接鉴权参数
            if (nliAuthenRequest != null) {
                path = path + "?" + nliAuthenRequest.getParams();
            }
            switch (dataAccessMode) {
                // 访问网络，不做本地存储
                case DATA_FROM_NET_NO_CACHE:
                    json = DataUtil.getJsonFromServer(path, mContext, method, postParameters, headers);
                    isCache = false;
                    break;
                // 仅访问本地存储
                case DATA_FROM_CACHE:
                    // cache的数据永远正确
                    json = DataUtil.getJsonFromCache(path, mContext);
                    break;
                // 先访问本地存储返回数据展示,再访问网络更新数据并刷新UI,
                case DATA_UPDATE_CACHE:
                    json = DataUtil.getJsonFromServer(path, mContext, method, postParameters, headers);
                    if (!TextUtils.isEmpty(json)) {
                        isCache = true;
                    } else {
                        json = DataUtil.getJsonFromCache(path, mContext);
                    }
                    break;
                case DATA_FROM_NET:
                    json = DataUtil.getJsonFromServer(path, mContext, method, postParameters, headers);
                    isCache = true;
                    break;
                // 默认访问网络，正常情况不会执行default
                default:
                    json = DataUtil.getJsonFromServer(path, mContext, method, postParameters, headers);
                    isCache = false;
                    break;
            }
            if (!TextUtils.isEmpty(json)) {
                // 返回0再缓存
                //JSONObject responseHeader = new JSONObject(gson);
                //if ("0".equals(responseHeader.getString("code"))) {
                if (judger != null && !judger.judge(json)) {
                    resultDataMistake(requestCode, FProtocol.NetDataProtocol.ResponseStatus.LOAD_MISTAKE, json);
                    return;
                }
                if (isCache) {
                    DataUtil.cacheJson(path, json, mContext);
                }
                resultDataSuccess(requestCode, json);
                //}
                // 如果没返回0也要返回 为了提取错误消息
            } else {
                resultDataMistake(requestCode, FProtocol.NetDataProtocol.ResponseStatus.LOAD_MISTAKE, json);
            }


        } catch (Exception e) {
            LogUtils.e(LOG_TAG, "error:::" + e.toString());
            e.printStackTrace();
            resultDataMistake(requestCode, FProtocol.NetDataProtocol.ResponseStatus.LOAD_EXCEPTION, "");
        }
    }

    public ExecutorTask setDataAccessMode(FProtocol.NetDataProtocol.DataMode dataAccessMode) {
        this.dataAccessMode = dataAccessMode;
        return this;
    }

    public ExecutorTask setJudger(IResponseJudger judger) {
        this.judger = judger;
        return this;
    }

    public ExecutorTask setNliAuthenRequest(NliAuthenRequest nliAuthenRequest) {
        this.nliAuthenRequest = nliAuthenRequest;
        return this;
    }

    public ExecutorTask setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }
    /*public ExecutorTask setExecutorSerice(String serviceKey) {
        mExecutorService = CoreExecutorService.getExecutorService(serviceKey);
        return this;
    }*/

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void cancel() {
        mCancelled.set(true);
        CoreExecutorService.cancel(this);
    }

    public void execute() {
        mExecutorService.submit(this);
    }


    private void resultDataSuccess(final int requestCode, final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mIResponseCallBack != null) {
                    mIResponseCallBack.resultDataSuccess(requestCode, data);
                }
            }
        });
    }


    private void resultDataMistake(final int requestCode, final FProtocol.NetDataProtocol.ResponseStatus responseStatus, final String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mIResponseCallBack != null) {
                    mIResponseCallBack.resultDataMistake(requestCode, responseStatus, errorMessage);
                }
            }
        });
    }


    private void runOnUiThread(Runnable task) {
        getHandler().post(task);
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }
    }

    private static Handler getHandler() {
        synchronized (ExecutorTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }
}
