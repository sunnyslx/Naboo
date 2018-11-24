package com.idx.naboo.user.iom.login;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.home.HomeActivity;
import com.idx.naboo.imoran.ImoranManager;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.service.Execute;
import com.idx.naboo.user.hx.EaseLoginActivity;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.user.User;
import com.idx.naboo.user.iom.register.RegisterActivity;
import com.idx.naboo.user.personal_center.address.AllAddress;
import com.idx.naboo.user.personal_center.address.ApiRequestUtils_1;
import com.idx.naboo.user.personal_center.address.DataBean;
import com.idx.naboo.user.personal_center.address.bean.GetAdderssList;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.MapUtils;
import com.idx.naboo.utils.NetStatusUtils;
import com.idx.naboo.utils.RegexUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.friend.data.FriendInjection;
import com.idx.naboo.videocall.friend.data.FriendRepository;
import com.idx.naboo.videocall.friend.data.HxUser;
import com.idx.naboo.videocall.utils.SpUtils;
import com.xiaomor.mor.app.common.usercenter.UserInfoBean;

import net.imoran.sdk.entity.info.ScenesDataEntity;
import net.imoran.tv.sdk.network.base.ApiConstants;
import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.param.LoginParams;
import net.imoran.tv.sdk.network.requestdata.FProtocol;
import net.imoran.tv.sdk.network.utils.ApiRequestUtils;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.List;

import com.idx.naboo.takeout.utils.Constant;

/**
 * Created by peter on 1/18/18.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText login_username;
    private EditText login_userpwd;
    private Button login;
    private Button register;
    private Button forget;
    private Bitmap mBitmap;
    private CheckBox cbLaws;
    String uuid = null;
    private static final String TAG = "Franck";
    private SharedPreferencesUtil sharePrefUtils;
    private CheckBox login_password;
    private Bitmap mBitmap_toolbar;
    public static final int LOGIN_CODE = 0x02;
    private FriendRepository mRepository;
    private HxUser mHxUser;
    private String json;
    private IService mIService;
    private static List<DataBean> mDataBean = null;
    private SpeakService mService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(LoginActivity.this);
            mService= ((SpeakService.SpeakBinder) service).getService();

            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())) {
                Log.i("LoginActivity", "onServiceConnected: 进入到Json获取");
                json = mIService.getJson();
                JsonData jsonUtil = JsonUtil.createJsonData(json);
                String tts = jsonUtil.getType();
                Log.d(TAG, "onResume: " + tts);
                if (tts.equals("show_version")) {
                    TTSManager.getInstance(getBaseContext()).speak("请先登录账号", false);
                }
                if (tts.equals("usercenter")) {
                    TTSManager.getInstance(getBaseContext()).speak("请登录账号", false);
                }
                if (tts.equals("order_list")){
                    TTSManager.getInstance(getBaseContext()).speak("请先登录账号，才能查询订单哦", false);

                }
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private View view;

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(LoginActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("show_version")){
            TTSManager.getInstance(getBaseContext()).speak("请登录账号",false);
        }
        if (type.equals("back")){
            if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,""))
                    && (SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE)
                    || SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE))){
                finish();
                SpUtils.put(getApplicationContext(),Constant.TAKEOUT_SELLER,"");
            }else if (!TextUtils.isEmpty(sharePrefUtils.getUUID("Order"))){
                sharePrefUtils.saveUUID("Order","");
//                startActivity(new Intent(LoginActivity.this, OrderTimeActivity.class));
                finish();
            }

            else {
                Log.d(TAG, "onJsonReceived: back");
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        }
        if (type.equals("usercenter")) {
            TTSManager.getInstance(getBaseContext()).speak("请登录账号", false);
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
        TTSManager.getInstance(getBaseContext()).stop();

        hideKeyBoard(view);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharePrefUtils = new SharedPreferencesUtil(this);
        ApiConstants.KEY = "C7484C880C551D33";
        uuid = sharePrefUtils.getUUID("uuid");

        setContentView(R.layout.login_activity);
        view = findViewById(R.id.linear);
        if (TextUtils.isEmpty(uuid)){
            startActivity(new Intent(LoginActivity.this,EaseLoginActivity.class));
            finish();
            return;
        }
        initToolbar();

        initView();
        mRepository= FriendInjection.getInstance(this);
        mHxUser=new HxUser();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("登录");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this,R.drawable.path,
                70,70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
    }



    public  void initView() {
        login_username = findViewById(R.id.userName_text);
        login_userpwd = findViewById(R.id.userPaw_text);
        login = findViewById(R.id.login_bnt);
        register = findViewById(R.id.Register_bnt);
        cbLaws = findViewById(R.id.cbLaws);
        login_password = findViewById(R.id.login_password_show_status);
        mBitmap = BitmapUtils.decodeBitmapFromResources(this, R.drawable.back);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        login_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isCHecked = " + isChecked);
                if (isChecked) {
                    login_password.setBackgroundResource(R.mipmap.show_icon);
                    login_userpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    login_password.setBackgroundResource(R.mipmap.hidden_icon);
                    login_userpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        login_username.addTextChangedListener(new TextWatcher() {
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
    }

    public void hideKeyBoard(View view) {
        //
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * 登录时保存地址用于查询订单
     *
     * @param data
     */
    private void saveUserInfo(String data) {
        final UserInfoBean userInfoBean = UserInfoBean.getInstance(this);
        userInfoBean.setLogin(true);
        userInfoBean.setInformation(data);
        userInfoBean.setUserid(getDataBean(data).getUserInfo().getUid());
        userInfoBean.setPhone(getDataBean(data).getUserInfo().getSysUserMobile());
        userInfoBean.setNickName(getDataBean(data).getUserInfo().getSysUserLoginName());
        userInfoBean.setName(getDataBean(data).getUserInfo().getSysUserRealName());
        final GetAdderssList getAdderssList = new GetAdderssList();
        getAdderssList.setMo(getDataBean(data).getUserInfo().getSysUserMobile());
        Log.d(TAG, "searchAddress: 当前手机号码");
        Log.d(TAG, "saveUserInfo: uuid " + getDataBean(data).getUserInfo().getUid());
        ApiRequestUtils_1.getAddressList(this, getAdderssList, 0x09, new NetRequestCallback() {
            @Override
            public void success(int requestCode, String data) {

                Gson gson = new Gson();
                AllAddress allAddress = gson.fromJson(data, AllAddress.class);
                if (allAddress.getRet() != 200) {
                    //服务器无地址返回
                    Log.d(TAG, "success: 无地址返回");
                } else {
                    //有地址
                    mDataBean = allAddress.getData();
                    userInfoBean.setLatitude(String.valueOf(mDataBean.get(0).getLatitude()));
                    userInfoBean.setLongitude(String.valueOf(mDataBean.get(0).getLongitude()));
                    userInfoBean.setDefault_address("");
                    userInfoBean.setDefaultAddressRegion(mDataBean.get(0).getAddressRegion());
                    userInfoBean.setDefaultAddressCity(mDataBean.get(0).getAddressCity());
                    userInfoBean.setDefaultAddressDetail(mDataBean.get(0).getAddressDetail());
                    userInfoBean.setDefaultConsignee(mDataBean.get(0).getConsignee());
                    userInfoBean.setDefaultConsigneePhone(mDataBean.get(0).getConsigneeMobile());
                    userInfoBean.setDefaultAdddressid(String.valueOf(mDataBean.get(0).getAdddressid()));
                    userInfoBean.setAddress_district(mDataBean.get(0).getAddressDistrict());
                }




            }



            @Override
            public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
            }
        });




    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_bnt:
                loginAccount();

                break;
            case R.id.Register_bnt:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
                break;
        }
    }


    private void loginAccount() {

        String name = login_username.getText().toString().trim();
        String pass = login_userpwd.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(LoginActivity.this,"请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!RegexUtils.checkMobile(name)) {
            Log.d(TAG, "checkMobile: " + RegexUtils.checkMobile(name) );
            Toast.makeText(LoginActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() > 11) {
            Log.d(TAG, "getName().length(): ");

            Toast.makeText(LoginActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length()<6 ){
            Toast.makeText(LoginActivity.this, "密码长度必须超过6位", Toast.LENGTH_SHORT).show();
            return;
        }
        iMorLogin(name,pass);
    }

    //蓦然登录
    private void iMorLogin(final String name, final String pass){
        LoginParams params = new LoginParams();
        LoginParams.UserInfoBean userInfoBean = new LoginParams.UserInfoBean();
        userInfoBean.setSysUserMobile(name);
        userInfoBean.setSysUserLoginPassword(pass);
        params.setUserInfo(userInfoBean);
        params.setUserSrc("SharpSoundBox");
        ApiRequestUtils.loginAccount(this, params, LOGIN_CODE, new NetRequestCallback() {
            @Override
            public void success(int requestCode, String data) {
                LogUtils.i(TAG, "loginAccount success :" + data);
                sharePrefUtils.saveUUID("login_data",data);

                if (getDataBean(data).getStatus() == 0){
                    sharePrefUtils.saveUUID("uuid",getDataBean(data).getUserInfo().getUid());
                    sharePrefUtils.saveUUID("mobile",getDataBean(data).getUserInfo().getSysUserMobile());
                    sharePrefUtils.saveUUID("name",getDataBean(data).getUserInfo().getSysUserRealName());
                   // hXLogin(name,pass);
                    Log.d(TAG, "success: " + getDataBean(data).getUserInfo().getUid());
                    startActivity(new Intent(LoginActivity.this,EaseLoginActivity.class));
                    finish();
                    saveUserInfo(data);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();



                }else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
                LogUtils.i(TAG, "loginAccount error :" + requestCode);
                Toast.makeText(LoginActivity.this, "网路错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static User.DataBean getDataBean(String jsonData){
        Gson gson = new Gson();
        User user = gson.fromJson(jsonData,User.class);
        return user.getData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null){
            mBitmap.recycle();
            mBitmap = null;
        }

        if (mBitmap_toolbar != null){
            mBitmap_toolbar.recycle();
            mBitmap_toolbar = null;
        }
        if (sharePrefUtils != null){
            sharePrefUtils = null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        LoginActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);


                if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,""))
                        && (SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE)
                        || SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE))){
                    finish();
                    SpUtils.put(getApplicationContext(),Constant.TAKEOUT_SELLER,"");
                }else if (!TextUtils.isEmpty(sharePrefUtils.getUUID("Order"))){
                    sharePrefUtils.saveUUID("Order","");
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateLocationToMoran() {
        if (NetStatusUtils.isWifiConnected(getBaseContext())) {
            MapUtils.setCallBack(new MapUtils.CallBack() {
                @Override
                public void call(AMapLocation amap) {
                    //拿当前城市名称
                    String mCityNameDefault = amap.getCity();
                    android.util.Log.d(TAG, "call: city=" + mCityNameDefault);
                    //拿当前amap做拼接工作
                    mAMapLocation = amap;
                    //设置蓦然用到的定为坐标
                    ImoranManager.getInstance(getBaseContext()).setLocation(amap);
                }
            });
            MapUtils.getCity(getApplicationContext());
        }
    }

}
