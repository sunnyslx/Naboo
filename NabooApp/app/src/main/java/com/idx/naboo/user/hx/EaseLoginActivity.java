package com.idx.naboo.user.hx;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
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

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.home.HomeActivity;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.ui.TakeoutCarActivity;
import com.idx.naboo.takeout.ui.TakeoutSellerActivity;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.user.personal_center.Personal_center;
import com.idx.naboo.utils.RegexUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.friend.data.FriendDataSource;
import com.idx.naboo.videocall.friend.data.FriendInjection;
import com.idx.naboo.videocall.friend.data.FriendRepository;
import com.idx.naboo.videocall.friend.data.HxUser;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.sdk.entity.info.VUIDataEntity;

import java.util.UUID;


public class EaseLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EaseLoginActivity";
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;

    private Button mSignUpBtn;
    private Button mSignInBtn;
    private ProgressDialog dialog;

    private HxUser mHxUser;
    private SharedPreferencesUtil sharePrefUtils;

    private FriendRepository mRepository;

    private String json;

    private IService mIService;
    private View view;
    private CheckBox es_login_password;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(EaseLoginActivity.this);
            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())) {
                Log.i("CalendarActivity", "onServiceConnected: 进入到Json获取");
                json = mIService.getJson();
                JsonData jsonUtil = JsonUtil.createJsonData(json);
                String tts = jsonUtil.getType();
                if (tts.equals("show_version")) {

                    TTSManager.getInstance(getBaseContext()).speak("请登录环信账号", false);
                }
                if (tts.equals("usercenter")) {

                    TTSManager.getInstance(getBaseContext()).speak("请登录环信账号", false);
                }
                if (tts.equals("order_list")){

                    TTSManager.getInstance(getBaseContext()).speak("请登录环信账号", false);
                }
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(EaseLoginActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
        TTSManager.getInstance(getBaseContext()).stop();

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
        if (type.equals("show_version")){

            TTSManager.getInstance(getBaseContext()).speak("请登录环信账号",false);
        }
        if (type.equals("back")){

            if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,""))
                    && (SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE)
                    || SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE))){
                finish();
                SpUtils.put(getApplicationContext(),Constant.TAKEOUT_SELLER,"");
            }else if (sharePrefUtils.getUUID("Order").equals("Order")){
                sharePrefUtils.saveUUID("Order","");
//                startActivity(new Intent(EaseLoginActivity.this, OrderTimeActivity.class));
                finish();
            }
            else {

                startActivity(new Intent(EaseLoginActivity.this, HomeActivity.class));
                finish();
            }
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (EMClient.getInstance().isLoggedInBefore()){
            startActivity(new Intent(EaseLoginActivity.this,Personal_center.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_ease_login);

        view = findViewById(R.id.login_linear);
        initView();
    }


    private void initView() {
        mUsernameEdit = (EditText) findViewById(R.id.username);
        mPasswordEdit = (EditText) findViewById(R.id.password_es);
        mSignInBtn = findViewById(R.id.ease_login);
        mSignUpBtn = findViewById(R.id.ease_register_1);
        sharePrefUtils = new SharedPreferencesUtil(this);
        mRepository= FriendInjection.getInstance(this);
        mHxUser=new HxUser();
        es_login_password = findViewById(R.id.iv_password_login);
        mSignInBtn.setOnClickListener(this);
        mSignUpBtn.setOnClickListener(this);



        es_login_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isCHecked = " + isChecked);
                if (isChecked) {
                    mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }




    //登录进 用户信息界面
    public void login(){

        final String username = mUsernameEdit.getText().toString().trim();
        final String password = mPasswordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            Toast.makeText(EaseLoginActivity.this,"请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(EaseLoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!RegexUtils.checkMobile(username)) {
            Log.d(TAG, "checkMobile: " + RegexUtils.checkMobile(username) );
            Toast.makeText(EaseLoginActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.length() > 11) {
            Log.d(TAG, "getName().length(): ");

            Toast.makeText(EaseLoginActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.length()<6 ){
            Toast.makeText(EaseLoginActivity.this, "密码长度必须超过6位", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new ProgressDialog(this);
        dialog.setMessage("登录中，请稍等...");
        dialog.show();
        EMClient.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                //sp保存数据信息
                String uuid=UUID.randomUUID().toString().replaceAll("-","");
                mHxUser.id= uuid;
                mHxUser.account=username;
                mHxUser.alias=sharePrefUtils.getUUID("name");
                mRepository.queryUser(username, new FriendDataSource.LoadUserCallback() {
                    @Override
                    public void onSuccess(HxUser user) {
                        Log.d(TAG, "onSuccess: 用户已存在");
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "run: 用户不存在"+mHxUser.id);
                        mRepository.insertUser(mHxUser);
                    }
                });
                SpUtils.put(NabooApplication.getInstance().getBaseContext(),"hyphenate_current_user",uuid);

                SpUtils.put(NabooApplication.getInstance().getBaseContext(), "username", username);
                SpUtils.put(NabooApplication.getInstance().getBaseContext(), "password", password);
                dialog.dismiss();

                if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,""))){
                    if (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE)) {
                        Intent intent = new Intent(EaseLoginActivity.this, TakeoutCarActivity.class);
//                        intent.putExtra("order_id", SpUtils.get(getApplicationContext(), "order_id", ""));
                        startActivity(intent);
//                        SpUtils.put(getApplicationContext(), "order_id", "");
//                        finish();
                    }else if (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE)){
                        Intent intent = new Intent(EaseLoginActivity.this, TakeoutSellerActivity.class);
//                        intent.putExtra("order_id", SpUtils.get(getApplicationContext(), "order_id", ""));
                        startActivity(intent);
                    }
                    SpUtils.put(getApplicationContext(), Constant.TAKEOUT_SELLER, "");
                    finish();
                }else if (!TextUtils.isEmpty(sharePrefUtils.getUUID("Order"))){
                    Log.d(TAG, "环信登录成功"+ sharePrefUtils.getUUID("Order"));
                    //startActivity(new Intent(EaseLoginActivity.this, HomeActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(EaseLoginActivity.this, Personal_center.class));
                    finish();

                }
            }
            /**
             * 登陆错误的回调
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(EaseLoginActivity.this, "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(EaseLoginActivity.this, "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(EaseLoginActivity.this, "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(EaseLoginActivity.this, "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(EaseLoginActivity.this, "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(EaseLoginActivity.this, "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(EaseLoginActivity.this, "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(EaseLoginActivity.this, "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(EaseLoginActivity.this, "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(EaseLoginActivity.this, "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ease_login:
                login();
                break;

            case R.id.ease_register_1:
                Intent intent = new Intent(EaseLoginActivity.this,EaseRegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null){
            dialog.dismiss();
        }
    }
}
