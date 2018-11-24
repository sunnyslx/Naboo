package com.idx.naboo.user;

/**
 * Created by peter on 3/6/18.
 */

public class Token {
        private int status_code;

        private int result_code;

        private String message;

        private String token_type;

        private int expires_in;

        private String access_token;

        public void setStatus_code(int status_code){
            this.status_code = status_code;
        }
        public int getStatus_code(){
            return this.status_code;
        }
        public void setResult_code(int result_code){
            this.result_code = result_code;
        }
        public int getResult_code(){
            return this.result_code;
        }
        public void setMessage(String message){
            this.message = message;
        }
        public String getMessage(){
            return this.message;
        }
        public void setToken_type(String token_type){
            this.token_type = token_type;
        }
        public String getToken_type(){
            return this.token_type;
        }
        public void setExpires_in(int expires_in){
            this.expires_in = expires_in;
        }
        public int getExpires_in(){
            return this.expires_in;
        }
        public void setAccess_token(String access_token){
            this.access_token = access_token;
        }
        public String getAccess_token(){
            return this.access_token;
        }
}
