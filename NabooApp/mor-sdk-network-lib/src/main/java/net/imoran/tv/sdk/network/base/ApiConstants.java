package net.imoran.tv.sdk.network.base;

import net.imoran.tv.sdk.network.BuildConfig;

/**
 * Created by bobge on 2018/4/2.
 */

public class ApiConstants {
    public static String KEY = "C7484C880C551D33";
    public static String BaseUrlDebug = "http://47.95.114.20:18107/";
    public static String BaseUrlRelease = "http://api.xiaomor.com/";
    public static String BaseUrl = BuildConfig.DEBUG ? BaseUrlDebug : BaseUrlDebug;

    /**
     * 帐号密码注册
     */
    public static String URL_REGISTER_BY_PASSWORD = BaseUrl + "public/?service=Account.registerAccountByPasswd&key=" + KEY;

    /**
     * 手机号 + 密码登录
     */
    public static String URL_LOGIN_ACCOUNT_BY_PASSWORD = BaseUrl + "public/?service=Account.loginAccountByPasswd&key=" + KEY;
    /**
     * 重置密码
     */
    public static String URL_RESET_ACCOUNT_PASSWORD = BaseUrl + "public/?service=Account.resetAccountPasswd&key=" + KEY;
}
