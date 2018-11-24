package com.idx.naboo.takeout.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Glide.with(this).load(mSong.get(mSongIndex).getPicUrl()).error(R.mipmap.takeout_order_item_food).into(mBackground);
 * 支付界面
 * Created by danny on 4/18/18.
 */

public class TakeoutPayActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG=TakeoutPayActivity.class.getSimpleName();
    private Context mContext;
    private IService mIService;

    //view
    private Button mBack;
    private TextView mPayInfo;
    private ImageButton mWeChat;
    private ImageButton mAliPay;
    private ImageView mPayQrCode;

    //计时器
    private TimerTask mTimerTask;
    private long curTime;
    private Timer mTimer;
    private boolean mFlag;

    private DecimalFormat df;
    private long mCountDown;
    private long mTime;
    private double mTotalFee;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mPayInfo.setText("需要支付" + df.format(mTotalFee) + "元,支付剩余时间" + toClock(curTime));
                    break;
                case 1:
                    menu();
                    break;
            }
        }
    };

    private void menu() {
        View popupView = TakeoutPayActivity.this.getLayoutInflater().inflate(R.layout.takeout_pay_out_time, null);
        final PopupWindow window = new PopupWindow(popupView, 800, 400);
//        window.setAnimationStyle(R.style.popup_window_anim);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.option));
        popupView.setFocusable(true);
        popupView.setFocusableInTouchMode(true);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAtLocation(mPayInfo, Gravity.CENTER,0,0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeout_pay);
        mContext=this;
        mTime=getIntent().getLongExtra("time",0);
        mTotalFee=getIntent().getDoubleExtra("total",0);
        initView();
    }

    private void initView() {
        mBack=findViewById(R.id.takeout_pay_back);
        mPayInfo=findViewById(R.id.takeout_pay_info);
        mWeChat=findViewById(R.id.takeout_pay_we_chat);
        mAliPay=findViewById(R.id.takeout_pay_ali_pay);
        mPayQrCode=findViewById(R.id.takeout_pay_qr_code);
        mBack.setOnClickListener(this);
        mWeChat.setOnClickListener(this);
        mAliPay.setOnClickListener(this);
        df = new DecimalFormat("######0.00");
    }

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(TakeoutPayActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {if (mIService!=null)mIService=null;}
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class),mConn,BIND_AUTO_CREATE);
        mFlag=true;
        mCountDown=14 * 60 * 1000;

        if ( System.currentTimeMillis() - mTime> mCountDown){
            mHandler.sendEmptyMessage(1);
        }else {
            curTime=(14*60*1000)-(System.currentTimeMillis() - mTime);
            mPayInfo.setText("需要支付" + df.format(mTotalFee) + "元,支付剩余时间20:00");
            if (curTime != 0) {
                Log.d(TAG, "onResume: ");
                destroyTimer();
                initTimer();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConn);
        destroyTimer();
    }

    @Override
    public void onJsonReceived(String json) {
        Log.d(TAG, "onJsonReceived: 从此处进入支付");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String domain=jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            Log.d(TAG, "onJsonReceived: "+type);
            if (type.equals("back")){
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takeout_pay_back:
                back();
                break;
            case R.id.takeout_pay_we_chat:
                weChatPay();
                break;
            case R.id.takeout_pay_ali_pay:
                aliPay();
                break;
            default:break;
        }
    }

    //返回
    private void back() {
        destroyTimer();
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
    }

    //微信支付
    private void weChatPay() {
    }

    //阿里支付
    private void aliPay() {
    }

    //将倒计时转为00:00格式
    public String toClock(long millisUntilFinished) {
        Log.d(TAG, "toClock: "+millisUntilFinished);
        long minute = (millisUntilFinished) / (60 * 1000);
        long second = (millisUntilFinished - minute * 60 * 1000) / 1000;
        String sm = "";
        String ss = "";
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (second < 10) {
            ss = "0" + String.valueOf(second);
        } else {
            ss = String.valueOf(second);
        }
        Log.d(TAG, "toClock: "+sm + ":" + ss);
        return sm + ":" + ss;
    }

    //初始化timer,每隔1秒发一次消息，更新计时消息
    public void initTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mFlag) {
                    if (curTime <= 0 || System.currentTimeMillis() - mTime >= mCountDown) {
                        curTime = 0;
                        mFlag=false;
                        Message message = Message.obtain();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    } else {
                        curTime -= 1000;//计时器，每次减一秒。
                        Message message = Message.obtain();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    //销毁计时器
    public void destroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mFlag=false;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
