package com.idx.naboo.user.personal_center.address;

import android.app.Activity;


import com.idx.naboo.user.personal_center.address.bean.AddAddress;
import com.idx.naboo.user.personal_center.address.bean.DeleteAddress;
import com.idx.naboo.user.personal_center.address.bean.GetAdderssList;
import com.idx.naboo.user.personal_center.address.bean.QueryAddress;
import com.idx.naboo.user.personal_center.address.bean.UpdateAddress;

import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.requestdata.FProtocol;
import net.imoran.tv.sdk.network.requestdata.RequestHelper;
import net.imoran.tv.sdk.network.utils.ApiRequestUtils;

/**
 * Created by ryan on 18-4-21.
 * Email: Ryan_chan01212@yeah.net
 */

public class ApiRequestUtils_1 extends ApiRequestUtils {

    //增加地址
    public static void addAddress(Activity activity, AddAddress params, int requestCode, NetRequestCallback callBack) {
        (new RequestHelper(activity, getIResponseCallBack(activity, callBack)))
                .getNewTaskBuilder()
                .setPath(ApiConstant.URL_REGISTER_BY_ADD_ADDRESS)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build().execute();
    }

    //更新地址
    public static void updateAddress(Activity activity, UpdateAddress params, int requestCode, NetRequestCallback callBack) {
        (new RequestHelper(activity, getIResponseCallBack(activity, callBack)))
                .getNewTaskBuilder()
                .setPath(ApiConstant.URL_REGISTER_BY_UPDATE_ADDRESS)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build().execute();
    }

    //删除地址
    public static void deleteAddress(Activity activity, DeleteAddress params, int requestCode, NetRequestCallback callBack) {
        (new RequestHelper(activity, getIResponseCallBack(activity, callBack)))
                .getNewTaskBuilder()
                .setPath(ApiConstant.URL_REGISTER_BY_DELETE_ADDRESS)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build().execute();
    }

    //查询地址
    public static void queryAddress(Activity activity, QueryAddress params, int requestCode, NetRequestCallback callBack) {
        (new RequestHelper(activity, getIResponseCallBack(activity, callBack)))
                .getNewTaskBuilder()
                .setPath(ApiConstant.URL_REGISTER_BY_QUERY_ADDRESS)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build().execute();
    }

    //查询地址
    public static void getAddressList(Activity activity, GetAdderssList params, int requestCode, NetRequestCallback callBack) {
        (new RequestHelper(activity, getIResponseCallBack(activity, callBack)))
                .getNewTaskBuilder()
                .setPath(ApiConstant.URL_REGISTER_BY_GATADDRESSlIST_ADDRESS)
                .setMethod(FProtocol.HttpMethod.POST)
                .setPostParameters(params.getParams())
                .setRequestCode(requestCode)
                .build().execute();
    }
}
