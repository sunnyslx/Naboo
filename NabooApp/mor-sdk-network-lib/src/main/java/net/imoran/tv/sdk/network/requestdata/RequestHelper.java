package net.imoran.tv.sdk.network.requestdata;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import net.imoran.tv.sdk.network.callback.IResponseCallBack;
import net.imoran.tv.sdk.network.utils.LogUtils;

/**
 * Created by bobge on 2017/7/12.
 */

public class RequestHelper {
    private static final String TAG = RequestHelper.class.getSimpleName();
    private Context mContext;
    private IResponseCallBack mRealResponseCallBack = new IResponseCallBack() {
        @Override
        public void resultDataSuccess(int requestCode, String data) {
            if (mResponseCallBack != null) {
                LogUtils.i(TAG, "success task " + requestCode);
                mResponseCallBack.resultDataSuccess(requestCode, data);
            }
            taskSet.remove(requestCode);
            LogUtils.i(TAG, "remove task " + requestCode);
        }

        @Override
        public void resultDataMistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus responseStatus, String errorMessage) {
            if (mResponseCallBack != null) {
                mResponseCallBack.resultDataMistake(requestCode, responseStatus, errorMessage);
            }
            taskSet.remove(requestCode);
            LogUtils.i(TAG, "remove task " + requestCode);
        }
    };
    private IResponseCallBack mResponseCallBack;
    private ArrayMap<Integer, ExecutorTask> taskSet = new ArrayMap<>();

    public RequestHelper(Context context, IResponseCallBack callBack) {
        if (context == null) return;
        mContext = context.getApplicationContext();
        mResponseCallBack = callBack;
    }

    public void dispose() {
        for (ExecutorTask task : taskSet.values()) {
            LogUtils.i("RequestHelper", "dispose task " + task);
            task.cancel();
        }
        mResponseCallBack = null;
    }

    public ExecutorTaskBuilder getNewTaskBuilder() {
        return new ExecutorTaskBuilder(mContext, taskSet).setCallBack(mRealResponseCallBack);
    }

    public void cancelOneTaskByRequestCode(int requestcode) {
        ExecutorTask task = taskSet.get(requestcode);
        if (task != null) {
            LogUtils.i("RequestHelper", "cancelOneTaskByRequestCode task " + task + "   requestcode=" + requestcode);
            task.cancel();
        }
    }
}
