package com.idx.naboo.user.personal_center.address;

/**
 * Created by ryan on 18-4-21.
 * Email: Ryan_chan01212@yeah.net
 */

public class ApiConstant {

    public static String KEY = "C7484C880C551D33";
    public static String BaseUrlDebug = "http://47.95.114.20:18107/";
    public static String BaseUrlRelease = "http://api.xiaomor.com/";
    public static String BaseUrl = net.imoran.tv.sdk.network.BuildConfig.DEBUG ? BaseUrlDebug : BaseUrlDebug;



    /**
     * 增加地址
     */
    public static String URL_REGISTER_BY_ADD_ADDRESS = BaseUrlRelease + "public/?service=Account.addAddress&key=" + KEY;


    /**
     * 更新地址
     */
    public static String URL_REGISTER_BY_UPDATE_ADDRESS = BaseUrlRelease + "public/?service=Account.updateAddress&key=" + KEY;


    /**
     * 删除地址
     */
    public static String URL_REGISTER_BY_DELETE_ADDRESS = BaseUrlRelease + "public/?service=Account.deleteAddress&key=" + KEY;


    /**
     * 查询地址
     */
    public static String URL_REGISTER_BY_QUERY_ADDRESS = BaseUrlRelease + "public/?service=Account.rollPollingAddress&key=" + KEY;

    /**
     * 查询地址
     */
    public static String URL_REGISTER_BY_GATADDRESSlIST_ADDRESS = BaseUrlRelease + "public/?service=Account.getAddressList&key=" + KEY;



}