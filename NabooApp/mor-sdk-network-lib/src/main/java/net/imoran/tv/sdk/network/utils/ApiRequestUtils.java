package net.imoran.tv.sdk.network.utils;

import android.app.Activity;

import net.imoran.tv.sdk.network.base.ApiConstants;
import net.imoran.tv.sdk.network.callback.IResponseCallBack;
import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.param.LoginParams;
import net.imoran.tv.sdk.network.param.RegisterParams;
import net.imoran.tv.sdk.network.requestdata.FProtocol;
import net.imoran.tv.sdk.network.requestdata.RequestHelper;

/**
 * Created by bobge on 2018/4/2.
 * 用户登录的相关接口请求工具类
 */

public class ApiRequestUtils {
    public static String BASETAG = "ApiRequestUtils";

    public static IResponseCallBack getIResponseCallBack(final Activity activity, final NetRequestCallback callBack) {
        IResponseCallBack iResponseCallBack = new IResponseCallBack() {
            @Override
            public void resultDataSuccess(final int requestCode, final String data) {
                if (activity == null) {
                    callBack.success(requestCode, data);
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callBack.success(requestCode, data);
                        } catch (Exception e) {
                            LogUtils.e(BASETAG, "error method resultDataSuccess = " + requestCode + " : error " + e.toString());
                            e.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void resultDataMistake(final int requestCode, final FProtocol.NetDataProtocol.ResponseStatus responseStatus, final String errorMessage) {
                if (activity == null) {
                    callBack.mistake(requestCode, responseStatus, errorMessage);
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callBack.mistake(requestCode, responseStatus, errorMessage);
                        } catch (Exception e) {
                            LogUtils.e(BASETAG, "error method resultDataMistake = " + requestCode + " : error " + e.toString());
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        return iResponseCallBack;
    }

    /**
     * 注册接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void registerAccount(final Activity activity, RegisterParams params, int requestCode, final NetRequestCallback callBack) {
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(ApiConstants.URL_REGISTER_BY_PASSWORD)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 登录接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void loginAccount(final Activity activity, LoginParams params, int requestCode, final NetRequestCallback callBack) {
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(ApiConstants.URL_LOGIN_ACCOUNT_BY_PASSWORD)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 重置密码接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void resetAccount(final Activity activity, LoginParams params, int requestCode, NetRequestCallback callBack) {
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(ApiConstants.URL_RESET_ACCOUNT_PASSWORD)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }
}
