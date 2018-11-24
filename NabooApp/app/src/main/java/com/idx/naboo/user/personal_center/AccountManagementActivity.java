package com.idx.naboo.user.personal_center;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.listener.DataListener;
import com.idx.naboo.user.iom.login.LoginActivity;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.xiaomor.mor.app.common.usercenter.UserInfoBean;

public class AccountManagementActivity extends BaseActivity {
    private Bitmap mBitmap_toolbar;
    private Button log_off;
    private Button change_passWord;
    private AlertDialog.Builder nomralDiglog;
    private SharedPreferencesUtil sharePrefUtils;

    private static final String TAG = "AccountManagementActivi";
    private TextView account_phone;
    private IService mIService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener((DataListener) AccountManagementActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_account_management);
        initToolbar();
        initView();
    }

    private void initView() {
//        change_passWord = findViewById(R.id.change_passWord);
        log_off = findViewById(R.id.log_off);
        sharePrefUtils = new SharedPreferencesUtil(this);
        account_phone = findViewById(R.id.account_phone);
        String phone = sharePrefUtils.getUUID("mobile");
        account_phone.setText(phone);
//        change_passWord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        log_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }


    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_account_info,null);
        builder.setView(layout);
        Button ok = layout.findViewById(R.id.ok);
        Button cancel = layout.findViewById(R.id.cancel);
        dialog = builder.create();
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sharePrefUtils.saveUUID("mJson","");
                sharePrefUtils.saveUUID("uuid","");
                sharePrefUtils.saveUUID("mobile","");
                sharePrefUtils.saveUUID("name","");
                sharePrefUtils.saveUUID("Order","");
                final UserInfoBean userInfoBean = UserInfoBean.getInstance(getBaseContext());

                userInfoBean.logout(getBaseContext());


                //                    userInfoBean.setLogin(false);
//                    userInfoBean.setUserid("");
//                    userInfoBean.setPhone("");
//                    userInfoBean.setNickName("");
//                    userInfoBean.setName("");
//                    userInfoBean.setLatitude("");
//                    userInfoBean.setLongitude("");
//                    userInfoBean.setDefault_address("");
//                    userInfoBean.setDefaultAddressRegion("");
//                    userInfoBean.setDefaultAddressCity("");
//                    userInfoBean.setDefaultAddressDetail("");
//                    userInfoBean.setDefaultConsignee("");
//                    userInfoBean.setDefaultConsigneePhone("");
//                    userInfoBean.setDefaultAdddressid("");
//                    userInfoBean.setAddress_district("");


                startActivity(new Intent(AccountManagementActivity.this, LoginActivity.class));

                //退出环信账号
                EMClient.getInstance().logout(true, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: *********************");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "onFail: *********************");
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                finish();
//                Toast.makeText(AccountManagementActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                Toast.makeText(AccountManagementActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("账号管理");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this,R.drawable.path,
                70,70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(AccountManagementActivity.this,Personal_center.class));
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(AccountManagementActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AccountManagementActivity.this,Personal_center.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null){
            dialog = null;
        }
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        if (type.equals("back")){
            startActivity(new Intent(AccountManagementActivity.this,Personal_center.class));
            finish();
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

}
