package com.idx.naboo.home;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.baidu.android.common.logging.Log;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.imoran.ImoranManager;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.user.hx.EaseLoginActivity;
import com.idx.naboo.user.iom.login.LoginActivity;
import com.idx.naboo.user.personal_center.Personal_center;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.utils.MapUtils;
import com.idx.naboo.utils.NetStatusUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.call.data.CallDataSource;
import com.idx.naboo.videocall.call.data.CallInjection;
import com.idx.naboo.videocall.call.data.CallRepository;
import com.idx.naboo.videocall.call.data.MissedCall;
import com.idx.naboo.videocall.friend.FriendAct;
import com.idx.naboo.weather.HandlerWeatherUtil;
import com.idx.naboo.weather.data.ContentWeather;
import com.idx.naboo.weather.data.ImoranResponse;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import q.rorbin.badgeview.Badge;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();
    private IService mIService;
    private TextView weather_tmp;
    private ImageView weatherIcon;
    private TextView holiday;
    private FrameLayout right;
    private NavigationView left;
    private DrawerLayout drawerLayout;
    private String festival;
    private ImageView iphone;
    private TextView mIphoneNum;
    List<Badge> badge = new ArrayList<>();
    private CallRepository mCallRepository;
    private ImageView wifi_image;           //信号图片显示
    private LinearLayout set;
    private LinearLayout individua;
    private LinearLayout contacts;
    private LinearLayout optimization;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Timer mTimer;
    private long current_time;
    private int mMissedCallCount=0;
    private View headerView;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(HomeActivity.this);

            if (mCityNameDefault == null && sharedPreferencesUtil.loadDrawable("weatherIcon")== 0 &&
                    sharedPreferencesUtil.getUUID("weather_tmp") == null) {
                weatherIcon.setImageResource(R.mipmap.weather_sunshine);
                weather_tmp.setText("17° ~ 24°");
            }else {
                getWeatherService();
            }

            if (festival == null) {

                getHolity_service();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private TimerTask mTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d(TAG, "onCreate: 开始时间" + System.currentTimeMillis());
        Log.d(TAG, "onCreate:");
        setContentView(R.layout.activity_home);
        if (!isServiceRunning(SpeakService.class.getName())) {
            android.util.Log.d(TAG, "onCreate: 开启服务");
            startService(new Intent(HomeActivity.this, SpeakService.class));
        }
        initView();
        //侧滑栏配置



        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mIService!= null) {
                    getWeather_service();
                }
            }
        };
        mTimer.schedule(mTimerTask,0,1800000);
        initDrawer();
        initListener();
    }




    private void initView() {
        mCallRepository= CallInjection.getInstance(getApplicationContext());
        drawerLayout = findViewById(R.id.drawer_layout);
        //菜单栏
        right = findViewById(R.id.right);

        left = findViewById(R.id.nav_view);
        //节日
        holiday = findViewById(R.id.holiday);
        //未接记录
        iphone = findViewById(R.id.iphone);
        mIphoneNum = findViewById(R.id.iphone_num);
        //WiFi信号强度变化
        wifi_image = (ImageView) findViewById(R.id.wifi_image);
        //天气
        weatherIcon = findViewById(R.id.weatherIcon);
        weather_tmp = findViewById(R.id.home_weather_tmp);
        //显示背景图
        right.setBackgroundResource(R.mipmap.bg_70);
        holiday.setVisibility(View.GONE);
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        //获取当前系统的时间戳
        sharedPreferencesUtil.saveUUID("Order","");

        current_time = System.currentTimeMillis();
        wifi_image.setImageResource(R.mipmap.wifi_5);

    }





    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        set = headerView.findViewById(R.id.set_menu);
        individua = headerView.findViewById(R.id.individua_menu);
        contacts = headerView.findViewById(R.id.contacts_menu);
        optimization = headerView.findViewById(R.id.ear_menu);

    }
    private void initListener() {
        set.setOnClickListener(this);
        individua.setOnClickListener(this);
        contacts.setOnClickListener(this);
        wifi_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);

            }
        });
        iphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))){
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }else {
                    startActivity(new Intent(HomeActivity.this, FriendAct.class));
                }
//                startActivity(new Intent(HomeActivity.this,FriendAct.class));
//                mCallRepository.deleteCall();
            }
        });
    }

    // Wifi的连接速度及信号强度：
    private int obtainWifiInfo(Context context) {
        int strength = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度，5为获取的信号强度值在5以内
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
        }
        return strength;
    }



    @Override
    protected void onResume() {
        super.onResume();
        // 注册wifi消息处理器
        registerReceiver(wifiIntentReceiver,new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        bindService(new Intent(getBaseContext(), SpeakService.class), conn, BIND_AUTO_CREATE);
        //如果上次时间戳不为null

        mCallRepository.queryAllCall(new CallDataSource.LoadCallCallback() {
            @Override
            public void onSuccess(List<MissedCall> calls) {
                mMissedCallCount=0;
                for (int i=0;i<calls.size();i++){
                    mMissedCallCount+=calls.get(i).count;
                }
                mIphoneNum.setVisibility(View.VISIBLE);
                if (0<mMissedCallCount && mMissedCallCount<99){
                    mIphoneNum.setText(""+mMissedCallCount);
                }else {
                    mIphoneNum.setText("99+");
                }
            }

            @Override
            public void onError() {
                mMissedCallCount=0;
                mIphoneNum.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause:");
        unbindService(conn);
        unregisterReceiver(wifiIntentReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy:");
        if (mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;

        }
        if (mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        if (sharedPreferencesUtil != null){
            sharedPreferencesUtil = null;
        }
        if (drawerLayout != null){
            drawerLayout = null;
        }
        if (headerView != null){
            headerView = null;
        }if (mCallRepository != null){
            mCallRepository = null;
        }
    }


    public void getHolity(String json) {
        Time_home holiday_class = ImoranResponseToBeanUtils.handleTimeHome(json);
        if (holiday_class != null && holiday_class.getRet() == 200) {
                festival = holiday_class.getData().getContent().getReply().getDefaultX().get(0).getStr();
                if (festival != null){
                holiday.setText(festival);
                holiday.setVisibility(View.VISIBLE);
            }else {
                holiday.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "请连接网络", Toast.LENGTH_LONG).show();
        }
    }


    //获取当前节日
    private void getHolity_service(){

        mIService.requestData("今天是什么节日", new NLIRequest.onRequest() {
            @Override
            public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                android.util.Log.d(TAG, "onResponse: " + s);
                getHolity(s);
            }

            @Override
            public void onError() {

            }
        });
    }

    //获取当前城市天气
    private void getWeather_service(){
        VUIDataEntity.wrapData(((NabooApplication)getApplication()).getDataEntity(), "","");
        mIService.requestData(mCityNameDefault + "今天的天气怎么样",  new NLIRequest.onRequest() {
            @Override
            public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                getWeather(s);
            }

            @Override
            public void onError() {
            }
        });
    }

    public void getWeather(String json) {

        try {

            android.util.Log.d(TAG, "getWeather: " + json);
            Gson gson = new Gson();
            ImoranResponse weather = gson.fromJson(json, ImoranResponse.class);
            android.util.Log.d(TAG, "getWeather: " + weather);
            if (weather != null && weather.getDataWeather() != null &&
                    weather.getDataWeather().getContent() != null &&
                    weather.getDataWeather().getContent().getType().equals("weather")) {
                ContentWeather content = weather.getDataWeather().getContent();
                if (content.getErrorCode() == 0) {
                    if (content.getReply().getWeather() != null && content.getReply().getWeather().size() > 0) {
                        String tmp = content.getReply().getWeather().get(0).getWeatherDetail().getTemperature().getMin() + "° ~ " +
                                content.getReply().getWeather().get(0).getWeatherDetail().getTemperature().getMax() + "°";
                        int weather_int = HandlerWeatherUtil.getWeatherImageResource(Integer.parseInt(content.getReply().getWeather().get(0)
                                .getWeatherDetail().getWeatherStateCode().getCodeDay()));
                        weather_tmp.setText(tmp);
                        weatherIcon.setImageResource(weather_int);
                        sharedPreferencesUtil.saveDrawableResId("weatherIcon", weather_int);  //保存图片
                        sharedPreferencesUtil.saveUUID("weather_tmp", tmp);                  //保存温度
                        sharedPreferencesUtil.saveUUID("System_time", String.valueOf(System.currentTimeMillis())); //当前系统时间戳
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_menu:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;
    		case R.id.ear_menu:
                android.util.Log.d(TAG, "onNavigationItemSelected: EarEQ");
                Intent intent1= new Intent(Intent.ACTION_MAIN);
                intent1.setComponent(new ComponentName("com.unlimiter.hear.app.eareq.ultimate","com.unlimiter.hear.app.eareq.activity.MainActivity"));
                startActivity(intent1);
                break;
            case R.id.individua_menu:
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))){
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    drawerLayout.closeDrawers();
                    return;
                }
                if (!EMClient.getInstance().isLoggedInBefore()){
                    startActivity(new Intent(HomeActivity.this,EaseLoginActivity.class));
                    drawerLayout.closeDrawers();
                    return;
                    }
                startActivity(new Intent(HomeActivity.this, Personal_center.class));

                break;
            case R.id.contacts_menu:
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))){
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }else {
                    startActivity(new Intent(HomeActivity.this, FriendAct.class));
                }
                break;
        }
        drawerLayout.closeDrawers();
    }

    private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            NetStatusUtils.isNetWorkAvailableOfGet("https://www.baidu.com/", new Comparable<Boolean>() {
                @Override
                public int compareTo(Boolean available) {
                    if (available) {
                        if ( mCityNameDefault == null) {
                                MapUtils.setCallBack(new MapUtils.CallBack() {
                                    @Override
                                    public void call(AMapLocation amap) {
                                        //拿当前城市名称
                                        mCityNameDefault = amap.getCity();
                                        android.util.Log.d(TAG, "call: city=" + mCityNameDefault);
                                        //拿当前amap做拼接工作
                                        mAMapLocation = amap;
                                        //设置蓦然用到的定为坐标
                                        ImoranManager.getInstance(getBaseContext()).setLocation(amap);
                                        if (mIService != null) {
                                            //之前没有网　未执行　则在这步　获取天气
                                            android.util.Log.d(TAG, "有网后执行: ");
                                            getWeatherService();
                                        }
                                    }
                                });
                                MapUtils.getCity(getApplicationContext());
                            }
                        int s = obtainWifiInfo(context);
                        android.util.Log.d(TAG, "onReceive:有网");
                        if(s == 0){
                            wifi_image.setImageResource(R.mipmap.wifi_1);
                        } else if(s == 1){
                            wifi_image.setImageResource(R.mipmap.wifi_2);
                        } else if(s == 2){
                            wifi_image.setImageResource(R.mipmap.wifi_3);
                        }else if(s == 3){
                            wifi_image.setImageResource(R.mipmap.wifi_4);
                        }else if(s == 4){
                            wifi_image.setImageResource(R.mipmap.wifi_5);
                        }

                    } else {
                        android.util.Log.d(TAG, "onReceive:无网");
                        wifi_image.setImageResource(R.mipmap.wifi_error);
                        weatherIcon.setImageResource(R.mipmap.weather_sunshine);
                        weather_tmp.setText("17° ~ 24°");
                    }



                    return 0;
                }

            });


        }
    };


    //获取天气状况
    private void getWeatherService(){
        if (sharedPreferencesUtil.getUUID("System_time") != null){
            //上次更新的时间的时间戳
            long system_time = Long.parseLong(sharedPreferencesUtil.getUUID("System_time"));
            //判断当前时间 减去 上次的时间  是否大于30分钟
            if ((current_time - system_time) > 60 * 30 * 1000){
                mIService.requestData(mCityNameDefault + "今天的天气怎么样", new NLIRequest.onRequest() {
                    @Override
                    public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                        getWeather(s);
                    }

                    @Override
                    public void onError() {
                    }
                });
            }else {
                android.util.Log.d(TAG, "小于30分钟 直接取出图片");
//                    bitmap_share = sharedPreferencesUtil.loadDrawable("weatherIcon");
                weatherIcon.setImageResource(sharedPreferencesUtil.loadDrawable("weatherIcon"));
                weather_tmp.setText(sharedPreferencesUtil.getUUID("weather_tmp"));
            }
        }
        else {
            mIService.requestData(mCityNameDefault + "今天的天气怎么样", new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    android.util.Log.d(TAG, "第一次安装 走这里 ");
                    getWeather(s);
                }

                @Override
                public void onError() {
                }
            });
        }

    }
}
