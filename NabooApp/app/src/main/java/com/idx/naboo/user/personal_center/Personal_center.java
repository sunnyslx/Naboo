package com.idx.naboo.user.personal_center;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.idx.naboo.user.personal_center.address.AddressActivity;
import com.idx.naboo.user.personal_center.order.OrderActivity;
import com.idx.naboo.user.personal_center.order.orderbean.Order_list;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;
import com.idx.naboo.user.personal_center.order.orderbean.Reply;
import com.idx.naboo.user.personal_center.order.orderbean.Root;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.friend.FriendAct;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import java.util.List;

public class Personal_center extends BaseActivity implements View.OnClickListener {

    private ImageView accountBtn;
    private ImageView orderBtn;
    private ImageView addressBtn;
    private Bitmap mBitmap_toolbar;
    private ImageView friendBtn;

    private RelativeLayout account;
    private RelativeLayout order;
    private RelativeLayout address;
    private RelativeLayout friend;

    private Bitmap bitmap_beijing;

    private Gson gson;

    private TextView versionId;
    private static final String TAG = "Personal_center";
    private String mJson;
    private String json;
    private IService mIService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(Personal_center.this);
            gson = new Gson();

            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())) {
                Log.i("Personal_center", "onServiceConnected: 进入到Json获取");
                json = mIService.getJson();
                JsonData jsonUtil = JsonUtil.createJsonData(json);
                String tts = jsonUtil.getType();
                Log.d(TAG, "onResume: " + tts);
                if (tts.equals("show_version")) {

                    TTSManager.getInstance(getBaseContext()).speak("版本信息為" + getAppVersionName(getApplicationContext()) + "，版本已是最新啦！", true);
                }
                if (tts.equals("usercenter")) {

                    TTSManager.getInstance(getBaseContext()).speak("已为你打开了个人中心", true);
                }
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
            }

            mIService.requestData("所有的订单", new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    mJson = s;
                    JsonData jsonUtil = JsonUtil.createJsonData(s);
                    String type = jsonUtil.getType();
                    Log.d(TAG, "onJsonReceived: " + type);
                    if (type.equals("order_list")) {
                        gson = new Gson();

                        Root orderRoot = gson.fromJson(s, Root.class);
                        Reply reply = orderRoot.getData().getContent().getReply();

                        //訂單的列表
                        List<Order_list> order_list = reply.getOrder_list();
                        for (Order_list order : order_list) {
                            //遍历所有的得到订单
                            List<Orders> orders = order.getOrders();
                            if (!orders.isEmpty()) {//判断订单List 是否为空
                                Log.d(TAG, "onJsonReceived: 订单信息");
                                //保存json数据
                                sharedPreferencesUtil.saveUUID("mJson", mJson);
                            } else {
                                Log.d(TAG, "onJsonReceived: 订单信息为空");
                                sharedPreferencesUtil.saveUUID("mJson", "");
                            }
                        }
                    }
                    Intent intent = new Intent(Execute.ACTION_SUCCESS);
                    sendBroadcast(intent);
                }

                @Override
                public void onError() {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.personal_center);
        initView();
        initToolbar();


    }



    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(Personal_center.this, SpeakService.class), conn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        TTSManager.getInstance(getBaseContext()).stop();
        unbindService(conn);
    }

    private void initView() {
        versionId = findViewById(R.id.versionId);
        accountBtn = findViewById(R.id.accountBtn);
        orderBtn = findViewById(R.id.orderBtn);
        addressBtn = findViewById(R.id.addressBtn);
        friendBtn = findViewById(R.id.friendBtn);
        gson = new Gson();
        account = findViewById(R.id.line_account_management);
        order = findViewById(R.id.line_order_information);
        address = findViewById(R.id.line_address_management);
        friend = findViewById(R.id.line_friend_management);
        bitmap_beijing = BitmapUtils.decodeBitmapFromResources(this,R.drawable.naboo_bg);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        relativeLayout.setBackground(new BitmapDrawable(bitmap_beijing));

        account.setOnClickListener(this);
        order.setOnClickListener(this);
        address.setOnClickListener(this);
        friend.setOnClickListener(this);

        versionId.setText("版本信息 " + getAppVersionName(this));
        Bitmap bitmap = BitmapUtils.scaleBitmapFromResources(this, R.mipmap.left_path_icon, 70, 70);
        accountBtn.setBackgroundDrawable(new BitmapDrawable(bitmap));
        orderBtn.setBackgroundDrawable(new BitmapDrawable(bitmap));
        addressBtn.setBackgroundDrawable(new BitmapDrawable(bitmap));
        friendBtn.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("个人中心");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.decodeBitmapFromResources(this, R.drawable.path);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_account_management:
                startActivity(new Intent(Personal_center.this, AccountManagementActivity.class));
                finish();
                break;
            case R.id.line_order_information:

                startActivity(new Intent(Personal_center.this, OrderActivity.class));
                break;
            case R.id.line_address_management:
                startActivity(new Intent(Personal_center.this, AddressActivity.class));
                
                break;
            case R.id.line_friend_management:
//                startActivity(new Intent(Personal_center.this, CalenderActivity.class));
                startActivity(new Intent(Personal_center.this,FriendAct.class));
//
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                startActivity(new Intent(Personal_center.this, HomeActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            //versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return "V"+versionName;
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("show_version")){


            TTSManager.getInstance(getBaseContext()).speak("版本信息為"+getAppVersionName(this)+"，版本已是最新啦！",true);
        }
        if (type.equals("back")){

//            startActivity(new Intent(Personal_center.this,HomeActivity.class));
            finish();
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }
}