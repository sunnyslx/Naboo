package com.idx.naboo.user;

/**
 * Created by peter on 3/8/18.
 */

public class User {

    /**
     * ret : 200
     * data : {"userInfo":{"uid":"430508073472229376","sysUserLoginName":"S","sysUserMobile":"S","sysUserRealName":"S"},"status":0,"msg":"Successful login !"}
     * msg :
     */

    private int ret;
    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * userInfo : {"uid":"430508073472229376","sysUserLoginName":"S","sysUserMobile":"S","sysUserRealName":"S"}
         * status : 0
         * msg : Successful login !
         */

        private UserInfoBean userInfo;
        private int status;
        private String msg;

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public static class UserInfoBean {
            /**
             * uid : 430508073472229376
             * sysUserLoginName : S
             * sysUserMobile : S
             * sysUserRealName : S
             */

            private String uid;
            private String sysUserLoginName;
            private String sysUserMobile;
            private String sysUserRealName;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getSysUserLoginName() {
                return sysUserLoginName;
            }

            public void setSysUserLoginName(String sysUserLoginName) {
                this.sysUserLoginName = sysUserLoginName;
            }

            public String getSysUserMobile() {
                return sysUserMobile;
            }

            public void setSysUserMobile(String sysUserMobile) {
                this.sysUserMobile = sysUserMobile;
            }

            public String getSysUserRealName() {
                return sysUserRealName;
            }

            public void setSysUserRealName(String sysUserRealName) {
                this.sysUserRealName = sysUserRealName;
            }
        }
    }
}


