package com.idx.naboo.user.iom.register;




/**
 * Created by peter on 1/19/18.
 */

public interface IRegisterView {

    String getName();
    String getPass();
    String getName_user();
    String getRegister_Code();
    String getGender();
//    void onSuccess(String msg);
//    void onFail(String msg);
    void onSuccessRegister(String msg);
    void onFailRegister(String msg);
}
