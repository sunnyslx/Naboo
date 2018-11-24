package com.idx.naboo.videocall.call;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.idx.naboo.R;


import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频电话管理类
 * Created by danny on 3/28/18.
 */

public class CallManager {
    private static final String TAG = CallManager.class.getSimpleName();
    private static CallManager mInstance = null;
    private Context mContext;
    private boolean mIsInComingCall = true;//呼入
    private Timer mTimer;
    private int mCallTime = 0;
    private boolean mIsMicOpen = true;
    private boolean mIsSpeakerOpen = true;
    private CallStateListener mCallStateListener;
    private AudioManager mAudioManager;
    private SoundPool mSoundPool;

    private int mStreamId;
    private int mLoadId;
    private boolean mIsLoaded = false;

    private NotificationManager mNotificationManager;
    private int mCallNotificationId = 7059;

    private String mToChatId;
    private CallState mCallState = CallState.DISCONNECTED;
    private EndType mEndType = EndType.CANCEL;

    private CallManager() {
    }

    public static CallManager getInstance() {
        if (mInstance == null) {
            mInstance = new CallManager();
        }
        return mInstance;
    }

    /**
     * 通话相关设置
     */
    public void init(Context context) {
        mContext = context;
        initSoundPool();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(true);
        EMClient.getInstance().callManager().getCallOptions().setEnableExternalVideoData(false);
        EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(true);
        EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(800);
        EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(150);
        EMClient.getInstance().callManager().getCallOptions().setVideoResolution(640, 480);
        EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(30);
        EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(32000);
    }

    /**
     * 初始化音频池
     */
    private void initSoundPool() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {//>21
            AudioAttributes attributes = new AudioAttributes.Builder()//
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)//用于电话铃声
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
        } else {//老版本
            mSoundPool = new SoundPool(1, AudioManager.MODE_RINGTONE, 0);
        }
    }

    /**
     * 接听电话
     */
    public boolean answerCall() {
        stopCallSound();
        try {
            EMClient.getInstance().callManager().answerCall();
            return true;
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通知铃声关闭
     */
    public void stopCallSound() {
        if (mSoundPool!=null){
            mSoundPool.stop(mStreamId);
        }
    }

    /**
     * 拒绝
     */
    public void rejectCall() {
        try {
            EMClient.getInstance().callManager().rejectCall();
            setEndType(EndType.REJECT);
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        }
        reset();
    }

    /**
     * 拨打
     */
    public void makeVideoCall() {
        try {
            EMClient.getInstance().callManager().makeVideoCall(mToChatId, "{'ext':{'type':'video','key':'value'}}");
            setEndType(EndType.CANCEL);
        } catch (EMServiceNotReadyException e) {
            e.printStackTrace();
        }
//        String ext = EMClient.getInstance().callManager().getCurrentCallSession().getExt();
//        Log.d(TAG, "makeVideoCall: " + ext);
    }

    /**
     * 结束通话
     */
    public void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        }
        reset();
    }

    /**
     * 记录通话时间
     */
    public void startRecordCallTime(){
        final CallEvent callEvent=new CallEvent();
        EventBus.getDefault().post(callEvent);
        callEvent.setCheckTime(true);
        if (mTimer==null){
            mTimer=new Timer();
        }
        mTimer.purge();//从计时器的任务队列中删除所有取消的任务
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                mCallTime++;
                EventBus.getDefault().post(callEvent);
            }
        };
        mTimer.scheduleAtFixedRate(timerTask,1000,1000);//固定费率执行适用于对绝对时间敏感的周期性活动,适用于调度必须保持相互同步的多个重复计时器任务
    }

    /**
     * 停止计时
     */
    private void stopRecordCallTime(){
        if (mTimer!=null){
            mTimer.purge();
            mTimer.cancel();
            mTimer=null;
        }
        mCallTime=0;
    }

    /**
     * 设置通话图像回调处理器
      */
    public void setCallCameraDataProcessor() {
        Log.d(TAG, "setCallCameraDataProcessor: 设置通话图像回调处理器");
//        CameraDataProcessor cameraDataProcessor = new CameraDataProcessor();// 初始化视频数据处理器
        EMCallManager.EMCameraDataProcessor cameraDataProcessor= new EMCallManager.EMCameraDataProcessor() {
            @Override
            public void onProcessData(byte[] data, Camera camera, int width, int height, int rotation) {
                int wh = width * height;
                for (int i = 0; i < wh; i++) {
                    int d = (data[i] & 0xFF);
                    d = d < 16 ? 16 : d;
                    d = d > 235 ? 235 : d;
                    data[i] = (byte) d;
                }
            }
        };
        EMClient.getInstance().callManager().setCameraDataProcessor(cameraDataProcessor);// 设置视频通话数据处理类
    }

    /**
     * 释放资源
     */
    public void reset() {
        mIsMicOpen=true;
        mIsSpeakerOpen=true;
        setCallState(CallState.DISCONNECTED);
        stopRecordCallTime();
        unregisterCallStateListener();
        if (mSoundPool!=null){
            mSoundPool.stop(mStreamId);
        }
        if (mAudioManager!=null){
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(true);
        }
    }

    /**
     * 注册通话状态监听
     */
    public void registerCallStateListener(){
        if (mCallStateListener==null){
            mCallStateListener=new CallStateListener();
        }
        EMClient.getInstance().callManager().addCallStateChangeListener(mCallStateListener);
    }

    /**
     * 解除通话状态监听
     */
    public void unregisterCallStateListener() {
        if (mCallStateListener!=null) {
            EMClient.getInstance().callManager().removeCallStateChangeListener(mCallStateListener);
            mCallStateListener=null;
        }
    }

//    public void openSpeaker(){
//        if (!mAudioManager.isSpeakerphoneOn()){
//            mAudioManager.setSpeakerphoneOn(true);
//        }
//        if (mCallState== CallState.ACCEPTED){
//            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        }else {
//            mAudioManager.setMode(AudioManager.MODE_NORMAL);
//        }
//        setSpeakerOpen(true);
//    }

//    public void closeSpeaker(){
//        if (mAudioManager.isSpeakerphoneOn()){
//            mAudioManager.setSpeakerphoneOn(false);
//        }
//        if (mCallState== CallState.ACCEPTED){
//            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        }else {
//            mAudioManager.setMode(AudioManager.MODE_NORMAL);
//        }
//        setSpeakerOpen(false);
//    }

    private int currVolume = 0;
    /**
     * 打开扬声器
     */
    public void openSpeaker() {
        try{
            mAudioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!mAudioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                if (mCallState== CallState.ACCEPTED){
                    mAudioManager.setMode(AudioManager.MODE_IN_CALL);
//                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                }else {
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                }
                setSpeakerOpen(true);
                mAudioManager.setSpeakerphoneOn(true);
                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            if(mAudioManager != null) {
                if(mAudioManager.isSpeakerphoneOn()) {
                    if (mCallState== CallState.ACCEPTED){
                        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    }else {
                        mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    }
                    setSpeakerOpen(false);
                    mAudioManager.setSpeakerphoneOn(false);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,10,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 播放电话铃声
     */
    private void playCallAudio(){
        openSpeaker();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        if (mSoundPool!=null){
            mStreamId=mSoundPool.play(mLoadId,0.5f,0.5f,1,-1,1);
        }
    }

    /**
     * 播放呼叫提示音
     */
    public void playCallHintAudio(){
        if (mIsLoaded){
            playCallAudio();
        }else {
            loadAudio();
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    Log.d(TAG, "铃声加载完毕: "+sampleId+"-"+status);
                    mIsLoaded=true;
                    playCallAudio();
                }
            });
        }
    }

    /**
     * 加载铃声
     */
    private void loadAudio() {
        if (mIsInComingCall){//呼入
            mLoadId=mSoundPool.load(mContext, R.raw.sound_call_incoming,1);
        }else {
            mLoadId=mSoundPool.load(mContext,R.raw.sound_calling,1);
        }
    }

    /**
     * 打电话通知
     */
    public void addCallNotification(){
        mNotificationManager= (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.mipmap.video_call_notification_icon);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);//使用默认通知灯，震动
        builder.setContentText("点击恢复通话");
        builder.setContentTitle("与 "+mToChatId+" 进行通话中...");
        Intent intent=new Intent(mContext, VideoCallActivity.class);
        PendingIntent pi=PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);//相当于添加点击事件，打开活动
        builder.setOngoing(true);//是否为正在进行通知
        builder.setWhen(System.currentTimeMillis());//通知时间
        mNotificationManager.notify(mCallNotificationId,builder.build());
    }

    /**
     * 取消打电话通知
     */
    public void cancelCallNotification(){
        if (mNotificationManager!=null) {
            mNotificationManager.cancel(mCallNotificationId);
            mNotificationManager=null;
        }
    }

    /**
     * 移除悬浮窗
     */
    public void removeFloatWindow() {FloatWindow.getInstance(mContext).removeFloatWindow();}

    /**
     * 开启悬浮窗
     */
    public void addFloatWindow() {FloatWindow.getInstance(mContext).addFloatWindow();}

    /**
     * 通话时间，计时器产生
     */
    public int getCallTime() {return mCallTime;}

    /**
     * 麦克风-get\set
     */
    public boolean isMicOpen() {return mIsMicOpen;}

    public void setMicOpen(boolean micOpen) {mIsMicOpen = micOpen;}

    /**
     * 扩音器-get\set
     */
    public boolean isSpeakerOpen() {return mIsSpeakerOpen;}

    public void setSpeakerOpen(boolean speakerOpen) {mIsSpeakerOpen = speakerOpen;}

    /**
     * 被呼叫方
     */
    public String getToChatId() {return mToChatId;}

    public void setToChatId(String toChatId) {mToChatId = toChatId;}

    /**
     * 电话状态-get\set
     */
    public CallState getCallState() {return mCallState;}

    public void setCallState(CallState callState) {mCallState = callState;}

    /**
     * 结束类型-get\set
     */
    public EndType getEndType() {return mEndType;}

    public void setEndType(EndType endType) {mEndType = endType;}

    /**
     * 呼入、呼出-get\set
     */
    public boolean isInComingCall() {return mIsInComingCall;}

    public void setInComingCall(boolean inComingCall) {mIsInComingCall = inComingCall;}

    /**
     * call state
     */
    public enum CallState {
        CONNECTING,     // 连接中
        CONNECTED,      // 连接成功，等待接受
        ACCEPTED,       // 通话中
        DISCONNECTED    // 通话中断
    }

    /**
     * end type
     */
    public enum EndType {
        NORMAL,     // 正常结束通话
        CANCEL,     // 取消
        CANCELLED,  // 被取消
        BUSY,       // 对方忙碌
        OFFLINE,    // 对方不在线
        REJECT,     // 拒绝的
        REJECTED,   // 被拒绝的
        NORESPONSE, // 未响应
        TRANSPORT,  // 建立连接失败
        DIFFERENT   // 通讯协议不同
    }
}
