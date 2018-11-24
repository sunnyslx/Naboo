package net.imoran.tv.sdk.network.param;

import android.text.TextUtils;
import android.util.Log;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by bobge on 2018/4/2.
 */

public class RegisterParams extends BaseParams {
    /**
     * userInfo : {"sysUserMobile":"13720054281","accountUUID":"3hfcf8e90828448988b83f3295b38567","sysUserLoginName":"BUPTFB","sysUserLoginPassword":"QWE123.","sysUserRealName":"樊波"}
     * userType : 1
     * userSrc : SharpTV
     */

    private UserInfoBean userInfo;
    private int userType;
    private String userSrc;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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
         * accountUUID : 3hfcf8e90828448988b83f3295b38567
         * sysUserLoginName : BUPTFB
         * sysUserLoginPassword : QWE123.
         * sysUserRealName : 樊波
         */

        private String sysUserMobile;
        private String accountUUID;
        private String sysUserLoginName;
        private String sysUserLoginPassword;
        private String sysUserRealName;

        public String getSysUserMobile() {
            return sysUserMobile;
        }

        public void setSysUserMobile(String sysUserMobile) {
            this.sysUserMobile = sysUserMobile;
        }

        public String getAccountUUID() {
            return accountUUID;
        }

        public void setAccountUUID(String accountUUID) {
            this.accountUUID = accountUUID;
        }

        public String getSysUserLoginName() {
            return sysUserLoginName;
        }

        public void setSysUserLoginName(String sysUserLoginName) {
            this.sysUserLoginName = sysUserLoginName;
        }

        public String getSysUserLoginPassword() {
            return sysUserLoginPassword;
        }

        public void setSysUserLoginPassword(String sysUserLoginPassword) {
            this.sysUserLoginPassword = sysUserLoginPassword;
        }

        public String getSysUserRealName() {
            return sysUserRealName;
        }

        public void setSysUserRealName(String sysUserRealName) {
            this.sysUserRealName = sysUserRealName;
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
        params.put("userType", getUserType() + "");
        params.put("userSrc", getUserSrc());
        return params;
    }
}
