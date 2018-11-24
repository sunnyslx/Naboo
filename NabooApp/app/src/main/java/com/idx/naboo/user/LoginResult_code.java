package com.idx.naboo.user;

/**
 * Created by peter on 3/7/18.
 */

class LoginResult_code {
    private int result_code;

    private String message;

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
}
