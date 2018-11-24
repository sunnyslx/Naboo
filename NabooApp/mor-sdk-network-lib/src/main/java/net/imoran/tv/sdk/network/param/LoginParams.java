package net.imoran.tv.sdk.network.param;

import android.text.TextUtils;
import android.util.Log;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/2.
 * 登录和重置密码使用同一个参数
 */

public class LoginParams extends BaseParams {

    /**
     * userInfo : {"sysUserMobile":"13720054281","sysUserLoginPassword":"QWE123."}
     * userSrc : SharpTV
     */

    private UserInfoBean userInfo;
    private String userSrc;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserSrc() {
        return userSrc;
    }

    public void setUserSrc(String userSrc) {
        this.userSrc = userSrc;
    }

    public static class UserInfoBean {
        /**
         * sysUserMobile : 13720054281
         * sysUserLoginPassword : QWE123.
         */

        private String sysUserMobile;
        private String sysUserLoginPassword;

        public String getSysUserMobile() {
            return sysUserMobile;
        }

        public void setSysUserMobile(String sysUserMobile) {
            this.sysUserMobile = sysUserMobile;
        }

        public String getSysUserLoginPassword() {
            return sysUserLoginPassword;
        }

        public void setSysUserLoginPassword(String sysUserLoginPassword) {
            this.sysUserLoginPassword = sysUserLoginPassword;
        }
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(GsonObjectDeserializer.produceGson().toJson(getUserInfo()))) {
            Log.e("params", "RegisterParams getParams empty");
            return null;
        }
        params.put("userInfo", GsonObjectDeserializer.produceGson().toJson(getUserInfo()));
        params.put("userSrc", getUserSrc());
        return params;
    }
}
