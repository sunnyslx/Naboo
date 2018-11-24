package com.idx.naboo.videocall.call;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.idx.naboo.NabooApplication;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.listener.DataListener;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.videocall.VideoIntent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打电话基类
 * Created by danny on 3/28/18.
 */

public class CallActivity extends AppCompatActivity implements DataListener {
    private static final String TAG = CallActivity.class.getSimpleName();
    private CallActivity mActivity;
    public int mCallOrAnswer=0;
    public boolean mClickReject=false;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            intent.setAction(VideoIntent.ACTION_CALL_END);
            getBaseContext().sendBroadcast(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        //初始化界面,全屏,解锁,关闭输入法,屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public void initView(){
        if (CallManager.getInstance().getCallState()==CallManager.CallState.DISCONNECTED){//默认状态
            // 收到呼叫或者呼叫对方时初始化通话状态监听
            CallManager.getInstance().setCallState(CallManager.CallState.CONNECTING);
            CallManager.getInstance().registerCallStateListener();
            CallManager.getInstance().playCallHintAudio();

            if (!CallManager.getInstance().isInComingCall()){//默认接听
                CallManager.getInstance().makeVideoCall();
            }
        }
    }

    /**
     * 拒接
     */
    public void reject(){
        Log.d(TAG, "answer: 点击了拒接电话");
        CallManager.getInstance().rejectCall();
        onFinish();
        mCallOrAnswer=1;
//        mClickReject=true;
        mHandler.sendEmptyMessageDelayed(0,2000);
    }

    /**
     * 接听
     */
    public void answer(){
        Log.d(TAG, "answer: 点击了接电话");
        CallManager.getInstance().answerCall();
        mCallOrAnswer=1;
    }

    /**
     * 结束
     */
    public void end(){
        Log.d(TAG, "answer: 点击了挂电话");
        CallManager.getInstance().endCall();
        onFinish();
        mCallOrAnswer=1;
        mHandler.sendEmptyMessageDelayed(0,2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);//注册观察者模式
    }

    @Override
    protected void onResume() {
        if (CallManager.getInstance().getCallState()==CallManager.CallState.DISCONNECTED){
            onFinish();
            return;
        }else {
            CallManager.getInstance().cancelCallNotification();
            CallManager.getInstance().removeFloatWindow();
        }
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class),mConn,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 通话结束,销毁界面
     */
    public void onFinish(){
        if (mActivity!=null) {
            mActivity.finish();
            mActivity=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mActivity!=null) {mActivity = null;}
    }

    public IService mIService;

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(CallActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {if (mIService!=null)mIService=null;}
    };

    /**
     * Imoran数据
     */
    private JsonData mJsonData;//Imoran返回基础数据

    @Override
    public void onJsonReceived(String json) {
        Log.d("CallActivity", "onJsonReceived: 从此处进入外卖");
        mJsonData= JsonUtil.createJsonData(json);
        try {
            String type=new JSONObject(json).getJSONObject("data").getJSONObject("content").getString("type");
            if (type.equals("answer")) {
                answer();
            }else if (type.equals("reject")){
                reject();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSessionStateChanged(SessionState state) {}
}
