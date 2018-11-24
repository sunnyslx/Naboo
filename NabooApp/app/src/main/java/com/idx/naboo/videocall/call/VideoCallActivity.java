package com.idx.naboo.videocall.call;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.idx.naboo.R;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.videocall.call.data.CallDataSource;
import com.idx.naboo.videocall.call.data.CallInjection;
import com.idx.naboo.videocall.call.data.CallRepository;
import com.idx.naboo.videocall.call.data.MissedCall;
import com.idx.naboo.videocall.friend.data.NotificationMissedCallListener;
import com.idx.naboo.videocall.utils.SpUtils;
import com.superrtc.sdk.VideoView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频通话界面处理
 * Created by danny on 2018/03/18.
 */
public class VideoCallActivity extends CallActivity implements View.OnClickListener{
    private static final String TAG = VideoCallActivity.class.getSimpleName();
    // SurfaceView 控件状态:  -1 表示通话没有接通  0 表示本小远大  1 表示远小本大
    private int mSurfaceState = -1;
    private Context mContext;

    //小窗口位置及大小
    private int mLittleWidth;
    private int mLittleHeight;
    private int mRightMargin;
    private int mTopMargin;

    //通话组件初始化
    private EMCallSurfaceView mLocalSurface = null;
    private EMCallSurfaceView mOppositeSurface = null;
    private RelativeLayout.LayoutParams mLocalParams = null;
    private RelativeLayout.LayoutParams mOppositeParams = null;

    //基本组件
    private View mControlLayout;
    private RelativeLayout surfaceLayout;
    private TextView mCallStateView;
    private TextView mCallTimeView;
    private TextView mFriends;
    private ImageButton mMicSwitch;
    private ImageButton mSpeakerSwitch;
    private FloatingActionButton mRejectCall;
    private FloatingActionButton mEndCall;
    private FloatingActionButton mAnswerCall;

    //计时器
    private TimerTask mTimerTask;
    private int mCurrentTime;
    private Timer mTimer;
    
    private static boolean mIsConnect=false;
    private CallRepository mCallRepository;
    private MissedCall mMissedCall;
    private String mName;
    private static VideoCallActivity sInstance=null;

    public static VideoCallActivity getInstance(){
        if (sInstance==null){
            sInstance=new VideoCallActivity();
        }
        return sInstance;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        mContext=this;
        mCurrentTime = 30 * 1000;
        mControlLayout=findViewById(R.id.video_call_layout_call_control);
        surfaceLayout=findViewById(R.id.video_call_layout_surface_container);
        mCallStateView=findViewById(R.id.video_call_state);
        mCallTimeView=findViewById(R.id.video_call_time);
        mFriends=findViewById(R.id.video_call_friends);
        mMicSwitch=findViewById(R.id.video_call_mic_switch);
        mSpeakerSwitch=findViewById(R.id.video_call_speaker_switch);
        mRejectCall=findViewById(R.id.video_call_reject_call);
        mEndCall=findViewById(R.id.video_call_end_call);
        mAnswerCall=findViewById(R.id.video_call_answer_call);
        mLocalSurface=findViewById(R.id.video_call_local_surface);
        mOppositeSurface=findViewById(R.id.video_call_opposite_surface);
        mMicSwitch.setOnClickListener(this);
        mSpeakerSwitch.setOnClickListener(this);
        mAnswerCall.setOnClickListener(this);
        mEndCall.setOnClickListener(this);
        mRejectCall.setOnClickListener(this);
        mControlLayout.setOnClickListener(this);
        mCallRepository= CallInjection.getInstance(mContext);
        mName=SpUtils.get(mContext,Constant.VIDEO_CALL_TO_USERNAME,"");
        Log.d(TAG, "refreshCallView: "+mName);
        initView();
    }

    /**
     * 初始化timer
     */
    public void initTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mCurrentTime == 0) {
                    mCallOrAnswer=1;
                    if (CallManager.getInstance().getCallState()!= CallManager.CallState.ACCEPTED) {
                        end();
                        return;
                    }
                } else {
                    mCurrentTime -= 1000;//计时器，每次减1秒。
                }
            }
        };
        if (mTimer==null) {
            mTimer = new Timer();
        }
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 销毁计时器
     */
    private void destroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimer==null) {
            initTimer();
        }
        mIsConnect=SpUtils.getBoolean(mContext,"float_into_video_call",false);
        SpUtils.putBoolean(mContext,"float_into_video_call",false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
        onFinish();
        CallManager.getInstance().cancelCallNotification();
        if (mIService!=null){mIService=null;}
        if (mContext!=null){mContext=null;}
    }

    // 重载父类方法,实现一些当前通话的操作，
    @Override public void initView() {
        mLittleWidth=(int)getResources().getDimension(R.dimen.little_surface_width);
        mLittleHeight=(int)getResources().getDimension(R.dimen.little_surface_height);
        mRightMargin=(int)getResources().getDimension(R.dimen.little_surface_margin_right);
        mTopMargin=(int)getResources().getDimension(R.dimen.little_surface_margin_top);

        super.initView();
        if (CallManager.getInstance().isInComingCall()) {//判断是呼入还是呼出
            mEndCall.setVisibility(View.GONE);
            mAnswerCall.setVisibility(View.VISIBLE);
            mRejectCall.setVisibility(View.VISIBLE);
            mCallStateView.setText(R.string.call_connected_is_incoming);
        } else {
            mEndCall.setVisibility(View.VISIBLE);
            mAnswerCall.setVisibility(View.GONE);
            mRejectCall.setVisibility(View.GONE);
            mCallStateView.setText(R.string.call_connecting);
        }

        mMicSwitch.setActivated(!CallManager.getInstance().isMicOpen());//false
        mSpeakerSwitch.setActivated(CallManager.getInstance().isSpeakerOpen());//true

        initCallSurface();
        if (CallManager.getInstance().getCallState() == CallManager.CallState.ACCEPTED) {// 判断当前通话是刚开始，还是从后台恢复已经存在的通话
            mEndCall.setVisibility(View.VISIBLE);
            mAnswerCall.setVisibility(View.GONE);
            mRejectCall.setVisibility(View.GONE);
            mCallStateView.setText(R.string.call_accepted);
            refreshCallTime();
            onCallSurface();
        }
        try {
            EMClient.getInstance().callManager().setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        CallManager.getInstance().setCallCameraDataProcessor();
    }

    /**
     * 拨打电话时控制界面按钮的显示与隐藏
      */
    private void onControlLayout() {
        if (mControlLayout.isShown()) {
            mControlLayout.setVisibility(View.GONE);
        } else {
            mControlLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 退出全屏通话界面
      */
    private void exitFullScreen() {
        CallManager.getInstance().addCallNotification();
        CallManager.getInstance().addFloatWindow();
        onFinish();
    }

    /**
     * 麦克风开关(默认声音正常传输)
     */
    private void onMicrophone() {
        try {
            if (mMicSwitch.isActivated()) {
                mMicSwitch.setActivated(false);
                EMClient.getInstance().callManager().resumeVoiceTransfer();
                CallManager.getInstance().setMicOpen(true);
            } else {
                mMicSwitch.setActivated(true);
                EMClient.getInstance().callManager().pauseVoiceTransfer();
                CallManager.getInstance().setMicOpen(false);
            }
        } catch (HyphenateException e) {
            Log.e(TAG, e.getErrorCode()+"错误码:错误信息"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 扬声器开关(默认关闭)
     */
    private void onSpeaker() {
        if (mSpeakerSwitch.isActivated()) {
            mSpeakerSwitch.setActivated(false);
            CallManager.getInstance().closeSpeaker();
            CallManager.getInstance().setSpeakerOpen(false);
        } else {
            mSpeakerSwitch.setActivated(true);
            CallManager.getInstance().openSpeaker();
            CallManager.getInstance().setSpeakerOpen(true);
        }
    }

    /**
     * 接听通话
     */
    @Override public void answer() {
        super.answer();
        mEndCall.setVisibility(View.VISIBLE);
        mRejectCall.setVisibility(View.GONE);
        mAnswerCall.setVisibility(View.GONE);
    }

    /**
     * 初始化通话界面控件
     */
    private void initCallSurface() {
        // 初始化显示远端画面控件
        mOppositeParams = new RelativeLayout.LayoutParams(0, 0);
        mOppositeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mOppositeParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mOppositeSurface.setLayoutParams(mOppositeParams);

        // 初始化显示本地画面控件
        mLocalParams = new RelativeLayout.LayoutParams(0, 0);
        mLocalParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mLocalParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mLocalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mLocalSurface.setLayoutParams(mLocalParams);

        mLocalSurface.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onControlLayout();
            }
        });

        mLocalSurface.setZOrderOnTop(false);
        mLocalSurface.setZOrderMediaOverlay(true);
        mLocalSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        mOppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        // 设置通话画面显示控件
        EMClient.getInstance().callManager().setSurfaceView(mLocalSurface, mOppositeSurface);
    }

    /**
     * 接通通话，改变本地画面 view 大小
      */
    private void onCallSurface() {
        mSurfaceState = 0;
        mLocalParams = new RelativeLayout.LayoutParams(mLittleWidth, mLittleHeight);
        mLocalParams.width = mLittleWidth;
        mLocalParams.height = mLittleHeight;
        mLocalParams.rightMargin = mRightMargin;
        mLocalParams.topMargin = mTopMargin;
        mLocalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mLocalSurface.setLayoutParams(mLocalParams);

        mLocalSurface.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                changeCallSurface();
            }
        });

        mOppositeSurface.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onControlLayout();
            }
        });
    }

    /**
     * 切换通话界面
      */
    private void changeCallSurface() {
        if (mSurfaceState == 0) {
            mSurfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(mOppositeSurface, mLocalSurface);
        } else {
            mSurfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(mLocalSurface, mOppositeSurface);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(CallEvent event) {
        if (event.isState()) {refreshCallView(event);}
        if (event.isCheckTime()) {refreshCallTime();}
    }

    /**
     * 刷新通话界面
     */
    private void refreshCallView(CallEvent event) {
        EMCallStateChangeListener.CallError callError = event.getCallError();
        EMCallStateChangeListener.CallState callState = event.getCallState();
        switch (callState) {
            case CONNECTING:Log.d(TAG+"正在呼叫对方" , callError.toString());break;
            case CONNECTED:
                Log.d(TAG+"正在连接" , callError.toString());
                if (CallManager.getInstance().isInComingCall()) {
                    mCallStateView.setText(R.string.call_connected_is_incoming);
                    Log.d(TAG, "initView: "+SpUtils.get(mContext, Constant.VIDEO_CALL_TO_USERNAME,""));
                    if (!mFriends.isShown()) {
                        mFriends.setVisibility(View.VISIBLE);
                    }
                    mFriends.setText(SpUtils.get(mContext,Constant.VIDEO_CALL_TO_USERNAME,""));
                } else {
                    mCallStateView.setText(R.string.call_connected);
                }
                break;
            case ACCEPTED:
                mIsConnect=true;
                Log.d(TAG,"通话已接通");
                mCallStateView.setText(R.string.call_accepted);
                if (!mFriends.isShown()){
                    mFriends.setVisibility(View.VISIBLE);
                }
//                mFriends.setText(CallManager.getInstance().getToChatId());
                mFriends.setText(SpUtils.get(mContext,Constant.VIDEO_CALL_TO_USERNAME,""));
                onCallSurface();// 通话接通，更新界面
                break;
            case DISCONNECTED:
                Log.d(TAG+"通话已结束" , callError.toString());
                if (callError.toString().equals("error_none")){
                    if (!mIsConnect && mCallOrAnswer==0){
                        Log.d(TAG, "refreshCallView: 未接电话");
                        final String callAccount=CallManager.getInstance().getToChatId();
                        mCallRepository.queryPointCall(callAccount, new CallDataSource.LoadPointCallCallback() {
                            @Override
                            public void onSuccess(MissedCall call) {
                                Log.d(TAG, "onSuccess: 未接电话已存在累加");
                                mCallRepository.updateCallCount(1, callAccount, new CallDataSource.SuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (mMissedCallListener!=null){mMissedCallListener.notification();}
                                    }
                                });
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "onError: 未接电话存储");
                                mMissedCall=new MissedCall();
                                mMissedCall.callAccount=callAccount;
                                mMissedCall.callName=SpUtils.get(mContext,Constant.VIDEO_CALL_TO_USERNAME,"");
                                mMissedCall.count=1;
                                mCallRepository.insertCall(mMissedCall, new CallDataSource.SuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (mMissedCallListener!=null){mMissedCallListener.notification();}
                                    }
                                });
                            }
                        });
                    }
                }
                CallManager.getInstance().cancelCallNotification();
                CallManager.getInstance().removeFloatWindow();
                onFinish();
                break;
            case NETWORK_DISCONNECTED:
                Toast.makeText(mContext, getResources().getString(R.string.huan_xin_call_net_down), Toast.LENGTH_SHORT).show();
                CallManager.getInstance().cancelCallNotification();
                CallManager.getInstance().removeFloatWindow();
                end();
                break;
            case NETWORK_NORMAL:Log.d(TAG,"网络正常");break;
            case NETWORK_UNSTABLE:
                if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                    Log.d(TAG+"没有通话数据" , callError.toString());
                } else {
                    Log.d(TAG+"网络不稳定" , callError.toString());
                }
                CallManager.getInstance().cancelCallNotification();
//                CallManager.getInstance().removeFloatWindow();
                end();
                break;
            case VIDEO_PAUSE:Toast.makeText(mContext, getResources().getString(R.string.huan_xin_call_video_pause), Toast.LENGTH_SHORT).show();break;
            case VIDEO_RESUME:Toast.makeText(mContext, getResources().getString(R.string.huan_xin_call_video_resume), Toast.LENGTH_SHORT).show();break;
            case VOICE_PAUSE:Toast.makeText(mContext, getResources().getString(R.string.huan_xin_call_voice_pause), Toast.LENGTH_SHORT).show();break;
            case VOICE_RESUME:Toast.makeText(mContext, getResources().getString(R.string.huan_xin_call_voice_resume), Toast.LENGTH_SHORT).show();break;
            default:break;
        }
    }

    private NotificationMissedCallListener mMissedCallListener;

    public void setNotificationMissedCallListener(NotificationMissedCallListener listener){mMissedCallListener=listener;}

    /**
     * 通话时间显示
     */
    private void refreshCallTime() {
        int t = CallManager.getInstance().getCallTime();
        int h = t / 60 / 60;
        int m = t / 60 % 60;
        int s = t % 60 % 60;
        String time = "";
        if (h > 9) {
            time = "" + h;
        } else {
            time = "0" + h;
        }
        if (m > 9) {
            time += ":" + m;
        } else {
            time += ":0" + m;
        }
        if (s > 9) {
            time += ":" + s;
        } else {
            time += ":0" + s;
        }
        if (!mCallTimeView.isShown()) {
            mCallTimeView.setVisibility(View.VISIBLE);
        }
        mCallTimeView.setText(time);
    }

    /**
     * 横竖屏切换，重写该方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {super.onConfigurationChanged(newConfig);}

    /**
     * home键等操作，退出全屏，通知栏通知，点击恢复通话
     */
    @Override
    protected void onUserLeaveHint() {exitFullScreen();}

    /**
     * 后退键，退出全屏，通知栏通知，点击恢复通话
     */
    @Override public void onBackPressed() {exitFullScreen();}

    /**
     * 通话结束释放界面资源
     */
    @Override public void onFinish() {
        if (mLocalSurface != null) {
            if (mLocalSurface.getRenderer() != null) {
                mLocalSurface.getRenderer().dispose();
            }
            mLocalSurface.release();
            mLocalSurface = null;
        }
        if (mOppositeSurface != null) {
            if (mOppositeSurface.getRenderer() != null) {
                mOppositeSurface.getRenderer().dispose();
            }
            mOppositeSurface.release();
            mOppositeSurface = null;
        }
        mIsConnect=false;
        super.onFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_call_layout_call_control:
                onControlLayout();
                break;
            case R.id.video_call_mic_switch:
                onMicrophone();
                break;
            case R.id.video_call_speaker_switch:
                onSpeaker();
                break;
            case R.id.video_call_end_call:
                end();
                break;
            case R.id.video_call_reject_call:
                reject();
                break;
            case R.id.video_call_answer_call:
                answer();
                break;
        }
    }
}
