package com.idx.naboo.videocall.call;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.videocall.VideoIntent;

import org.greenrobot.eventbus.EventBus;

/**
 * 通话状态监听
 * Created by danny on 3/28/18.
 */

public class CallStateListener implements EMCallStateChangeListener {
    private static final String TAG=CallStateListener.class.getSimpleName();
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "onCallStateChanged: ");
            Intent intentend = new Intent();
            intentend.setAction(VideoIntent.ACTION_CALL_END);
            NabooApplication.getInstance().getBaseContext().sendBroadcast(intentend);
        }
    };

    @Override
    public void onCallStateChanged(CallState callState, CallError callError) {
        CallEvent callEvent=new CallEvent();
        callEvent.setState(true);
        callEvent.setCallError(callError);
        callEvent.setCallState(callState);
        EventBus.getDefault().post(callEvent);
        switch (callState){
            case CONNECTING:
                Log.d(TAG, "正在呼叫对方");
                CallManager.getInstance().setCallState(CallManager.CallState.CONNECTING);
                break;
            case RINGING:Log.d(TAG, "响铃中...");break;
            case CONNECTED:
                Log.d(TAG, "正在连接...");
                Intent intent = new Intent();
                intent.setAction(VideoIntent.ACTION_CALL_START);
                NabooApplication.getInstance().getBaseContext().sendBroadcast(intent);
                CallManager.getInstance().setCallState(CallManager.CallState.CONNECTED);
                break;
            case ACCEPTED:
                Log.e(TAG, "通话已接通");
                CallManager.getInstance().stopCallSound();
                CallManager.getInstance().startRecordCallTime();
                CallManager.getInstance().setEndType(CallManager.EndType.NORMAL);
                CallManager.getInstance().setCallState(CallManager.CallState.ACCEPTED);
                break;
            case DISCONNECTED:
                Log.e(TAG, "通话已结束,重置通话状态");
                if (callError == CallError.ERROR_UNAVAILABLE) {
                    Log.d("对方不在线" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.OFFLINE);
                } else if (callError == CallError.ERROR_BUSY) {
                    Log.d("对方正忙" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.BUSY);
                } else if (callError == CallError.REJECTED) {
                    Log.d("对方已拒绝" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.REJECTED);
                } else if (callError == CallError.ERROR_NORESPONSE) {
                    Log.d("对方未响应，可能手机不在身边" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.NORESPONSE);
                } else if (callError == CallError.ERROR_TRANSPORT) {
                    Log.d("连接建立失败" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.TRANSPORT);
                } else if (callError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED) {
                    Log.d("双方通讯协议不同" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.DIFFERENT);
                } else if (callError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                    Log.d("双方通讯协议不同" , callError.toString());
                    CallManager.getInstance().setEndType(CallManager.EndType.DIFFERENT);
                } else if (callError == CallError.ERROR_NO_DATA) {
                    Log.d("没有通话数据" , callError.toString());
                } else {
                    Log.d("通话已结束", callError.toString());
                    if (CallManager.getInstance().getEndType() == CallManager.EndType.CANCEL) {
                        CallManager.getInstance().setEndType(CallManager.EndType.CANCELLED);
                    }
                }
                CallManager.getInstance().reset();
                mHandler.sendEmptyMessageDelayed(0,2000);
                CallManager.getInstance().cancelCallNotification();
                break;
            case NETWORK_DISCONNECTED:Log.d(TAG, "对方网络不可用");break;
            case NETWORK_NORMAL:Log.d(TAG, "网络正常");break;
            case NETWORK_UNSTABLE:
                if (callError== CallError.ERROR_NO_DATA){
                    Log.d(TAG, "没有通话数据");
                }else {
                    Log.d(TAG, "网络不稳定");
                }
                break;
            case VIDEO_RESUME:Log.d(TAG, "视频传输已恢复");break;
            case VIDEO_PAUSE:Log.d(TAG, "视频传输暂停");break;
            case VOICE_RESUME:Log.d(TAG, "语音传输已恢复");break;
            case VOICE_PAUSE:Log.d(TAG, "语音传输暂停");break;
            default:break;
        }
    }
}
