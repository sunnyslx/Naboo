package com.idx.naboo.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.music.MusicActivity;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.videocall.utils.SpUtils;
import com.idx.naboo.weather.data.ContentWeather;
import com.idx.naboo.weather.data.ImoranResponse;
import com.idx.naboo.weather.data.Summary;
import com.idx.naboo.weather.data.Weather;

import net.imoran.sdk.entity.info.VUIDataEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class WeatherActivity extends BaseActivity implements WeatherItemInterface,
        TTSManager.Callback {
    private static final String TAG = WeatherActivity.class.getName();
    private IService mIService;
    private ImoranResponse imoranResponse;
    private List<Weather> mWeather = new ArrayList<>();
    private TextView weather_current_time, weather_current_week;
    private ImageView weather_now_icon;
    private TextView weather_city;
    private TextView weather_now_cond_txt, weather_current_temp;
    private TextView weather_current_aqi, weather_current_rays;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private Timer mTimer;
    private String mJson;
    private boolean flag;
    private int[] mCurrentDate;
    private Summary mSummary;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private String mCity;
    private static final int MSG_ONE = 0x001;
    //tts全部内容
    private String mTtsContent;
    //分割后的tts
    private String mSplitArray[];
    private String mPageId;
    private String mQueryId;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(WeatherActivity.this);
            mJson = mIService.getJson();
            if (mJson != null && (!mJson.equals(""))) {
                imoranResponse = ImoranResponseToBeanUtils.handleWeatherData(mJson);
                if (imoranResponse != null) {
                    pauseJson(mJson);
                    pauseQueryId(mJson);
                    findCurrentDates(imoranResponse);
                    dialogDismiss();
                    initView(imoranResponse.getDataWeather().getContent());
                    updateView();
                    getTTs(mJson);
                    flag = true;
                    if (mWeather != null && mWeather.size() > 0) {
                        if (mCurrentDate.length > 1) {
                            TTSManager.getInstance(getBaseContext()).speak(mSplitArray[0], WeatherActivity.this, false);
                        }else {
                            TTSManager.getInstance(getBaseContext()).speak(mSplitArray[0], WeatherActivity.this, true);
                        }
                    } else {
                        TTSManager.getInstance(getBaseContext()).speak(imoranResponse.getDataWeather().
                                getContent().getTts(), true);
                    }
                    //通知service
                    sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
                }
            }else {

                VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onJsonReceived(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            String domain = jsonObject.getJSONObject("data").getString("domain");
            if (domain.equals("cmd")) {
                JsonData jsonData = JsonUtil.createJsonData(json);
                if (jsonData != null) {
                    String type1 = jsonData.getType();
                    if (type1.equals("back")) {
                        finish();
                        TTSManager.getInstance(getBaseContext()).speak(imoranResponse.getDataWeather().
                                getContent().getTts(), true);
                    }
                }
            } else if (type.equals("weather")) {
                imoranResponse = ImoranResponseToBeanUtils.handleWeatherData(json);
                if (imoranResponse != null) {
                    if (ImoranResponseToBeanUtils.isImoranWeatherNull(imoranResponse)) {
                        mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
                        pauseJson(json);
                        pauseQueryId(json);
                        if (mWeather != null && mWeather.size() > 0) {
                            findCurrentDates(imoranResponse);
                            dialogDismiss();
                            initView(imoranResponse.getDataWeather().getContent());
                            updateView();
                            getTTs(json);
                            flag = true;
                            if (mCurrentDate.length > 1) {
                                TTSManager.getInstance(getBaseContext()).speak(mSplitArray[0], WeatherActivity.this, false);
                            }else {
                                TTSManager.getInstance(getBaseContext()).speak(mSplitArray[0], WeatherActivity.this, true);
                            }
                            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
                        } else {
                            TTSManager.getInstance(getBaseContext()).speak(imoranResponse.getDataWeather().
                                    getContent().getTts(), true);
                        }
                    }
                }
            }
            //通知service
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_ONE) {
                Log.i(TAG, "handleMessage: 进来了  flag=" + flag);
                if (flag && mCurrentDate.length > 1) {
                    if (mTtsContent.length() > 0) {
                        Log.i(TAG, "handleMessage: ");
                        TTSManager.getInstance(getBaseContext()).speak(mSplitArray[1], WeatherActivity.this, true);
                        flag = false;
                    }
                    update(imoranResponse.getDataWeather().getContent());
                    updateView();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        permissionRequest();
        setListener();
        flag = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), conn, BIND_AUTO_CREATE);
        //定期更新天气，半小时请求一次
//        mTimer=new Timer();
//        mTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (flag && mIService!=null){
//                    Log.i(TAG, "run: ");
//                    mIService.requestData(mCityNameDefault + "今天天气怎么样", new NLIRequest.onRequest() {
//                        @Override
//                        public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
//                            imoranResponse=ImoranResponseToBeanUtils.handleWeatherData(s);
//                            if (ImoranResponseToBeanUtils.isImoranWeatherNull(imoranResponse)){
//                                findCurrentDates(imoranResponse);
//                                initView(imoranResponse.getDataWeather().getContent());
//                                updateView();
//                            }
//                        }
//                        @Override
//                        public void onError() {
//
//                        }
//                    });
//                }
//            }
//        },0,1800000);
    }

    //解析json
    private void pauseJson(String json) {
        imoranResponse = ImoranResponseToBeanUtils.handleWeatherData(json);
        if (ImoranResponseToBeanUtils.isImoranWeatherNull(imoranResponse)) {
            mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
        }
    }

    private void pauseQueryId(String json){
        imoranResponse = ImoranResponseToBeanUtils.handleWeatherData(json);
        if (ImoranResponseToBeanUtils.isImoranWeatherNull(imoranResponse)) {
            //添加场景
            mQueryId = imoranResponse.getDataWeather().getQueryid();
            String domain = imoranResponse.getDataWeather().getDomain();
            String intention = imoranResponse.getDataWeather().getIntention();
            String type = imoranResponse.getDataWeather().getContent().getType();
            mPageId = domain + "_" + intention + "_" + type + "_" + "WeatherActivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        TTSManager.getInstance(getBaseContext()).stop();
        unbindService(conn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        if (mTimer != null) {
            mTimer = null;
        }
        if (handler != null) {
            handler = null;
        }
    }

    private void setListener() {
        weather_current_time = findViewById(R.id.weather_current_time);
        weather_current_week = findViewById(R.id.weather_current_week);
        weather_now_icon = findViewById(R.id.weather_now_icon);
        weather_city = findViewById(R.id.weather_city);
        weather_now_cond_txt = findViewById(R.id.weather_now_cond_txt);
        weather_current_temp = findViewById(R.id.weather_current_temp);
        weather_current_aqi = findViewById(R.id.weather_current_aqi);
        weather_current_rays = findViewById(R.id.weather_current_rays);
        recyclerView = findViewById(R.id.weather_recyclerView);
        relativeLayout = findViewById(R.id.weather_background);
    }

    private void initView(ContentWeather contentWeather) {
        try {
            if (contentWeather.getReply().getWeather() != null && contentWeather.getReply().getWeather().size() > 0) {
                if (mWeather != null && mWeather.size() > 0 && mCurrentDate != null) {
                    weather_current_time.setText((contentWeather.getReply().getWeather().get(mCurrentDate[0]).getDate()));
                    weather_current_week.setText(HandlerWeatherUtil.parseDateWeek(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[0]).getDate()));
                    weather_now_icon.setImageResource(HandlerWeatherUtil.getWeatherImageResource(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[0]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_city.setText(contentWeather.getReply().getWeather().get(mCurrentDate[0]).getCity());
                    weather_now_cond_txt.setText(contentWeather.getReply().getWeather().get(mCurrentDate[0]).getWeatherDetail()
                            .getWeatherStateCode().getTxt_d());
                    relativeLayout.setBackgroundResource(HandlerWeatherUtil.getWeatherBackground(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[0]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_current_temp.setText(contentWeather.getReply().getWeather().get(mCurrentDate[0]).getWeatherDetail().getTemperature().getMin()
                            + "℃ ~ " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getWeatherDetail().getTemperature()
                            .getMax() + "℃");
                    weather_current_aqi.setText(getResources().getString(R.string.weather_aqi) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getAqiQuality().getAqi()
                            + " " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getAqiQuality().getQlty());
                    weather_current_rays.setText(getResources().getString(R.string.weather_rays) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getSuggestion().getUv().getBrf());
                }
            } else {
                noMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //对周末天气情况,进行第二次更新
    private void update(ContentWeather contentWeather) {
        try {
            if (contentWeather != null) {
                if (mWeather != null && mWeather.size() > 0) {
                    weather_current_time.setText((contentWeather.getReply().getWeather().get(mCurrentDate[1]).getDate()));
                    weather_current_week.setText(HandlerWeatherUtil.parseDateWeek(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[1]).getDate()));
                    weather_now_icon.setImageResource(HandlerWeatherUtil.getWeatherImageResource(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[1]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_city.setText(contentWeather.getReply().getWeather().get(mCurrentDate[1]).getCity());
                    weather_now_cond_txt.setText(contentWeather.getReply().getWeather().get(mCurrentDate[1]).getWeatherDetail()
                            .getWeatherStateCode().getTxt_d());
                    relativeLayout.setBackgroundResource(HandlerWeatherUtil.getWeatherBackground(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[1]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_current_temp.setText(contentWeather.getReply().getWeather().get(mCurrentDate[1]).getWeatherDetail().getTemperature().getMin()
                            + "℃ ~ " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getWeatherDetail().getTemperature()
                            .getMax() + "℃");
                    weather_current_aqi.setText(getResources().getString(R.string.weather_aqi) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getAqiQuality().getAqi()
                            + " " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getAqiQuality().getQlty());
                    weather_current_rays.setText(getResources().getString(R.string.weather_rays) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getSuggestion().getUv().getBrf());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        //设置横向滑动
        if (mWeather != null && mWeather.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(WeatherActivity.this, LinearLayoutManager.HORIZONTAL, false));
            if (imoranResponse.getDataWeather().getContent().getReply().getWeather() != null) {
                mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
                weatherAdapter = new WeatherAdapter(mWeather);
                recyclerView.setAdapter(weatherAdapter);
                weatherAdapter.setWeatherOnItemClick(this);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        weatherAdapter.setClick(true);
        weatherAdapter.setCurrentItem(position);
    }

    //拿到返回的日期信息
    private void findCurrentDates(ImoranResponse imoranResponse) {
        try {
            mSummary = imoranResponse.getDataWeather().getContent().getSummary();
            mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
            mCurrentDate = new int[mSummary.getDates().length];
            for (int i = 0; i < mWeather.size(); i++) {
                for (int j = 0; j < mSummary.getDates().length; j++) {
                    if (mSummary.getDates()[j].equals(mWeather.get(i).getDate())) {
                        mCurrentDate[j] = i;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //数据为空时的界面处理
    private void noMessage() {
        JsonData jsonData = JsonUtil.createJsonData(mJson);
        getLocations(jsonData);
        Log.i(TAG, "noMessage: " + mCity);
        if (mCity != null) {
            builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.weather_not_find1) + mCity +
                    getResources().getString(R.string.weather_not_find2));
            builder.setPositiveButton(getResources().getString(R.string.dish_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
            relativeLayout.setBackgroundResource(R.mipmap.weather_bg);
        }
    }

    //状态栏消失
    private void dialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //申请权限
    private void permissionRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
    }

    //得到城市名
    private void getLocations(JsonData jsonData) {
        try {
            JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
            JSONArray locations = semantic.getJSONArray("locations");
            mCity = locations.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //拿到tts
    private void getTTs(String json) {
        JsonData jsonData = JsonUtil.createJsonData(json);
        if (jsonData != null) {
            if (mWeather != null && mWeather.size() > 0) {
                String tts = jsonData.getTts();
                mTtsContent = tts;
                splitTts();
            }
        }
    }

    //用逗号分割tts
    private void splitTts() {
        String string = ";";
        mSplitArray = mTtsContent.split(string);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPlayBegin(String s) {

    }

    @Override
    public void onPlayEnd(String s) {
        Log.i(TAG, "onPlayEnd: 继续发消息");
        Message message = Message.obtain();
        message.what = MSG_ONE;
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    @Override
    public void onPlayStopped(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String var1, int var2) {

    }

    @Override
    public void onError(@Nullable String s, int i, String s1) {

    }
}
