package com.idx.naboo.user.hx;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.utils.NetStatusUtils;

import net.imoran.sdk.entity.info.VUIDataEntity;


public class EaseRegisterActivity extends BaseActivity {

    private static final String TAG = "EaseRegisterActivity";
    private EditText name_editText;
    private EditText pass_editText;
    private EditText confirm_password_editText;
    private ProgressDialog dialog;
    private Button ease_register;
    private EditText phone_editText;
    private IService mIService;
    private CheckBox iv_password;
    private CheckBox iv_password2;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(EaseRegisterActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private View view;

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(EaseRegisterActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);

    }



    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
        hideKeyBoard(view);
    }

    public void hideKeyBoard(View view) {
        //
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("back")){
            startActivity(new Intent(EaseRegisterActivity.this,EaseLoginActivity.class));

            finish();
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_register);

        view = findViewById(R.id.linear);
        phone_editText = findViewById(R.id.user_phone);
        pass_editText = findViewById(R.id.password);
        confirm_password_editText = findViewById(R.id.confirm_password);
        ease_register = findViewById(R.id.ease_register);
        iv_password = findViewById(R.id.iv_password);
        iv_password2 = findViewById(R.id.iv_password2);
        ease_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userphone = phone_editText.getText().toString().trim();
                final String password = pass_editText.getText().toString().trim();
                String confirm_password = confirm_password_editText.getText().toString().trim();
                if (!NetStatusUtils.isWifiConnected(EaseRegisterActivity.this)){
                    Toast.makeText(EaseRegisterActivity.this,"请连接网络",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(userphone)){
                    Toast.makeText(EaseRegisterActivity.this,"帐号不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(EaseRegisterActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_password)){
                    Toast.makeText(EaseRegisterActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(confirm_password)){
                    Toast.makeText(EaseRegisterActivity.this,"两次密码不同",Toast.LENGTH_LONG).show();
                    return;
                }
                if (userphone.length() < 11) {
                    Log.d(TAG, "getName().length(): ");

                    Toast.makeText(EaseRegisterActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6 || confirm_password.length()<6) {
                    Toast.makeText(EaseRegisterActivity.this, "密码长度必须超过6位", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog = new ProgressDialog(EaseRegisterActivity.this);
                dialog.setMessage("注册中，请稍等...");
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            EMClient.getInstance().createAccount(userphone,password);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!EaseRegisterActivity.this.isFinishing()){
                                        dialog.dismiss();
                                    }

                                    Toast.makeText(EaseRegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(EaseRegisterActivity.this,EaseLoginActivity.class));
                                    finish();
                                }
                            });
                        } catch (final HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!EaseRegisterActivity.this.isFinishing()){
                                        dialog.dismiss();
                                    }
                                    int errorCode = e.getErrorCode();
                                    String message = e.getMessage();
                                    Log.d("lzan13", String.format("sign up - errorCode:%d, errorMsg:%s", errorCode, e.getMessage()));
                                    switch (errorCode) {
                                        // 网络错误
                                        case EMError.NETWORK_ERROR:
                                            Toast.makeText(EaseRegisterActivity.this, "网络错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                            break;
                                        // 用户已存在
                                        case EMError.USER_ALREADY_EXIST:
                                            Toast.makeText(EaseRegisterActivity.this, "用户已存在 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                            break;
                                        // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                        case EMError.USER_ILLEGAL_ARGUMENT:
                                            Toast.makeText(EaseRegisterActivity.this, "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                            break;
                                        // 服务器未知错误
                                        case EMError.SERVER_UNKNOWN_ERROR:
                                            Toast.makeText(EaseRegisterActivity.this, "服务器未知错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                            break;
                                        case EMError.USER_REG_FAILED:
                                            Toast.makeText(EaseRegisterActivity.this, "账户注册失败 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                            break;
                                        default:
                                            Toast.makeText(EaseRegisterActivity.this, "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            });
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        });



        phone_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    String tem = temp.substring(temp.length()-1, temp.length());
                    char[] temC = tem.toCharArray();
                    int mid = temC[0];
                    if(mid>=48&&mid<=57){//数字
                        return;
                    }
                    s.delete(temp.length()-1, temp.length());
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        iv_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isCHecked = " + isChecked);
                if (isChecked) {
                    pass_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    pass_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        iv_password2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isCHecked = " + isChecked);
                if (isChecked) {
                    confirm_password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    confirm_password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EaseRegisterActivity.this,EaseLoginActivity.class));

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null){
            dialog = null;
        }
    }
}