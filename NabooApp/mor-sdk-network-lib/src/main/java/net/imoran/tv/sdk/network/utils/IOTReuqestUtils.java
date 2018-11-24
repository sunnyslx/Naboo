package net.imoran.tv.sdk.network.utils;

import android.app.Activity;

import net.imoran.tv.sdk.network.base.IotConstants;
import net.imoran.tv.sdk.network.callback.IResponseCallBack;
import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.param.iot.IotAccessTokenParams;
import net.imoran.tv.sdk.network.param.iot.IotBindParams;
import net.imoran.tv.sdk.network.param.iot.IotDelDeviceHeaderParams;
import net.imoran.tv.sdk.network.param.iot.IotDelDeviceParams;
import net.imoran.tv.sdk.network.param.iot.IotDeviceContrParams;
import net.imoran.tv.sdk.network.param.iot.IotDeviceInfoParams;
import net.imoran.tv.sdk.network.param.iot.IotDeviceListParams;
import net.imoran.tv.sdk.network.param.iot.IotPushMsgHeaderParams;
import net.imoran.tv.sdk.network.param.iot.IotPushMsgParams;
import net.imoran.tv.sdk.network.param.iot.IotSaveOrUpdateParams;
import net.imoran.tv.sdk.network.param.iot.IotSecenDeleteParams;
import net.imoran.tv.sdk.network.param.iot.IotSecenListParams;
import net.imoran.tv.sdk.network.param.iot.IotTyoeListParams;
import net.imoran.tv.sdk.network.param.iot.IotUnBindParams;
import net.imoran.tv.sdk.network.param.iot.IotUpdateParams;
import net.imoran.tv.sdk.network.param.iot.IotUpdatePositionParams;
import net.imoran.tv.sdk.network.requestdata.FProtocol;
import net.imoran.tv.sdk.network.requestdata.RequestHelper;

/**
 * Created by bobge on 2018/4/13.
 * 用于家居iot设备相关接口请求工具类
 */

public class IOTReuqestUtils {
    public static String BASETAG = "IOTReuqestUtils";

    private static IResponseCallBack getIResponseCallBack(final Activity activity, final NetRequestCallback callBack) {
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
     * 获取设备类型列表
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotTypeList(final Activity activity, IotTyoeListParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_DEVICE_TYPELIST)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 绑定设备
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotBind(final Activity activity, IotBindParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_USER_BIND)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 解绑设备
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotUnBind(final Activity activity, IotUnBindParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_USER_UNBIND)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 获取场景列表
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotSecenList(final Activity activity, IotSecenListParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_SCENE_SCENELIST)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }


    /**
     * 删除个性化配置
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotSecenDelete(final Activity activity, IotSecenDeleteParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_SCENE_DELETE)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 获取用户下的设备列表
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotDeviceList(final Activity activity, IotDeviceListParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_DEVICE_DEVICELIST)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }


    /**
     * 更新设备信息
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotUpdate(final Activity activity, IotUpdateParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_DEVICE_UPDATE)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }


    /**
     * 更新设备信息位置
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotUpdatePosition(final Activity activity, IotUpdatePositionParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_DEVICE_UPDATEPOSITION)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 保存或更新个性化配置
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotSecenSaveOrUpdate(final Activity activity, IotSaveOrUpdateParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_SCENE_SAVEORUPDATE)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 获取Access token接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotAccessToken(final Activity activity, IotAccessTokenParams params, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setPath(IotConstants.URL_IOT_OAUTH_TOKEN)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * OTA升级接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotAOTUpdate(final Activity activity, IotPushMsgParams params, IotPushMsgHeaderParams headerParams, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setHeaders(headerParams.getParams())
                .setPath(IotConstants.URL_IOT_PUSH_MSG)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 设备删除接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotDelDevice(final Activity activity, IotDelDeviceParams params, IotDelDeviceHeaderParams headerParams, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setHeaders(headerParams.getParams())
                .setPath(IotConstants.URL_IOT_GATEWAY_DELDEVICE)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }


    /**
     * 设备控制接口
     *
     * @param activity
     * @param params
     * @param requestCode
     * @param callBack
     */
    public static void getIotDeviceContr(final Activity activity, IotDeviceContrParams params, IotDelDeviceHeaderParams headerParams, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setHeaders(headerParams.getParams())
                .setPath(IotConstants.URL_IOT_GATEWAY_DEVICECONTR)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }

    /**
     * 设备状态指标查询接口
     *
     * @param activity
     * @param params
     * @param headerParams header参数
     * @param requestCode
     * @param callBack
     */
    public static void getIotDeviceInfo(final Activity activity, IotDeviceInfoParams params, IotDelDeviceHeaderParams headerParams, int requestCode, final NetRequestCallback callBack) {
        if (params == null) {
            LogUtils.e(BASETAG, "params is empty");
            return;
        }
        new RequestHelper(activity, getIResponseCallBack(activity, callBack)).getNewTaskBuilder()
                .setHeaders(headerParams.getParams())
                .setPath(IotConstants.URL_IOT_GATEWAY_DEVICEINFO)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build()
                .execute();
    }
}
