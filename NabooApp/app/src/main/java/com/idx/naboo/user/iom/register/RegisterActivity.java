package com.idx.naboo.user.iom.register;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.user.Root;
import com.idx.naboo.user.iom.login.LoginActivity;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.RegexUtils;

import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.param.RegisterParams;
import net.imoran.tv.sdk.network.requestdata.FProtocol;
import net.imoran.tv.sdk.network.utils.ApiRequestUtils;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.UUID;


public class RegisterActivity extends BaseActivity implements  View.OnClickListener {
    private static final String TAG = "Franck_RegisterActivity";
    private Button register_bnt;
    private Button Register_clear_bnt;
    private EditText Register_name;
    private EditText Register_pwd;
    private EditText Register_pwd_2;
    private Bitmap mBitmap;
    private CheckBox cbLaws;
    private CheckBox cbLaws_2;
    private EditText name_text1;
    private Bitmap mBitmap_toolbar;
    public static final int REGISTER_CODE = 0x01;
    private String name;
    private String pass;
    private String mobile;
    private String pass_2;
    private AlertDialog dialog;
    private IService mIService;
    private View view;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(RegisterActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        view = findViewById(R.id.linear);

        initView();
        initToolbar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(RegisterActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);

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
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

            finish();
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("注册");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this,R.drawable.path,
                    70,70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Register_login_bnt:
                registerAccount();
                break;
            case R.id.Register_clear_bnt:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
                break;
        }
    }

    private void initView() {
        Register_name = findViewById(R.id.register_userName_text);
        Register_pwd = findViewById(R.id.register_userPaw_text);
        name_text1 = findViewById(R.id.name_text);
        Register_clear_bnt = findViewById(R.id.Register_clear_bnt);
        register_bnt = findViewById(R.id.Register_login_bnt);
        cbLaws = findViewById(R.id.cbLaws);
        cbLaws_2 = findViewById(R.id.cbLaws_2);
        Register_pwd_2 = findViewById(R.id.register_userPaw_text_2);
        mBitmap = BitmapUtils.decodeBitmapFromResources(this, R.drawable.back);
        Register_name.setInputType(InputType.TYPE_CLASS_PHONE);
        name_text1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        cbLaws.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isCHecked = "+isChecked);
                if (isChecked){
                    cbLaws.setBackgroundResource(R.mipmap.show_icon);
                    Register_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    cbLaws.setBackgroundResource(R.mipmap.hidden_icon);
                    Register_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        cbLaws_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isCHecked = "+isChecked);
                if (isChecked){
                    cbLaws_2.setBackgroundResource(R.mipmap.show_icon);
                    Register_pwd_2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    cbLaws_2.setBackgroundResource(R.mipmap.hidden_icon);
                    Register_pwd_2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        register_bnt.setOnClickListener(this);
        Register_clear_bnt.setOnClickListener(this);


        Register_name.addTextChangedListener(new TextWatcher() {
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

    private void registerAccount() {
        name = name_text1.getText().toString().trim();
        pass = Register_pwd.getText().toString().trim();
        mobile = Register_name.getText().toString().trim();
        pass_2 = Register_pwd_2.getText().toString().trim();
        //注册逻辑
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(RegisterActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!RegexUtils.checkMobile(mobile)) {
            Log.d(TAG, "checkMobile: " + RegexUtils.checkMobile(mobile));
            Toast.makeText(RegisterActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobile.length() < 11) {
            Log.d(TAG, "getName().length(): ");

            Toast.makeText(RegisterActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6 || pass_2.length()<6) {
            Toast.makeText(RegisterActivity.this, "密码长度必须超过6位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(pass_2) || !pass_2.equals(pass)){
            Toast.makeText(RegisterActivity.this, "两次输入密码不同", Toast.LENGTH_SHORT).show();
            return;
        }



        if (!RegexUtils.checkChinese(name)) {
            Log.d(TAG, "checkMobile: " + RegexUtils.checkMobile(mobile));
            Toast.makeText(RegisterActivity.this, "请输入中文", Toast.LENGTH_SHORT).show();
            return;
        }
        iMoRegister(mobile,pass_2,name);
    }

    private void iMoRegister(final String mobile, final String pass, final String name){
        RegisterParams params = new RegisterParams();
        RegisterParams.UserInfoBean userInfoBean = new RegisterParams.UserInfoBean();
        userInfoBean.setAccountUUID("");
        userInfoBean.setSysUserLoginName("ss");
        userInfoBean.setSysUserLoginPassword(pass);
        userInfoBean.setSysUserMobile(mobile);
        userInfoBean.setSysUserRealName(name);
        params.setUserInfo(userInfoBean);
        params.setUserSrc("SharpSoundBox");
        params.setUserType(1);
//        Log.d(TAG, "registerAccount: " + " " + pass + " " + mobile + " " + name + " " + userName);
        ApiRequestUtils.registerAccount(RegisterActivity.this, params, REGISTER_CODE, new NetRequestCallback() {
            @Override
            public void success(int requestCode, String data) {
                Log.d(TAG, "success: 蓦然已经注册成功");
                LogUtils.i(TAG, "registerAccount success :" + data);
                LogUtils.i(TAG, "registerAccount success :" + requestCode);
                if (getDataBean(data).getStatus() == 0){
                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();

                }else if (getDataBean(data).getStatus() == 1){
                    //弹出已注册提示框
                    showDialog(mobile);

                }else if (getDataBean(data).getStatus() == 2){
                    Toast.makeText(RegisterActivity.this, "手机号码错误", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
                LogUtils.i(TAG, "registerAccount error :" + requestCode);
                LogUtils.i(TAG, "registerAccount error :" + errorMessage);
                Toast.makeText(RegisterActivity.this, "网路错误", Toast.LENGTH_SHORT).show();
            }
        });
    }



    
    private static Root.DataBean getDataBean(String jsonData){
        Gson gson = new Gson();
        Root root = gson.fromJson(jsonData,Root.class);
        return root.getData();
    }

    private String getMyUUID(){
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;
    }


    //弹出已注册提示框
    private void showDialog(final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_reigster, null);
        builder.setView(layout);
        Button goto_login = layout.findViewById(R.id.goto_login);
        Button cancel = layout.findViewById(R.id.cancel);
        TextView textView = layout.findViewById(R.id.tv_account);
        textView.setText(name + "已是会员！");
        Log.d(TAG, "showDialog: ");
        dialog = builder.create();
        dialog.show();
        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap_toolbar != null){
            mBitmap_toolbar.recycle();
            mBitmap_toolbar = null;
        }
        if (dialog != null){
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
}

