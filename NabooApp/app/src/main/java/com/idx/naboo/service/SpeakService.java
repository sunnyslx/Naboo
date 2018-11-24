package com.idx.naboo.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.baidu.android.common.logging.Log;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.idx.naboo.NabooActions;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.calendar.CalenderActivity;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.dish.DishActivity;
import com.idx.naboo.figure.FigureActivity;
import com.idx.naboo.home.HomeActivity;
import com.idx.naboo.imoran.ImoranManager;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.map.MapActivity;
import com.idx.naboo.music.MusicActivity;
import com.idx.naboo.music.utils.ToastUtil;
import com.idx.naboo.news.NewsActivity;
import com.idx.naboo.service.listener.DataListener;
import com.idx.naboo.service.listener.SpeakWzRecordListener;
import com.idx.naboo.takeout.CartReportBean;
import com.idx.naboo.takeout.ui.TakeoutActivity;
import com.idx.naboo.takeout.ui.TakeoutCarActivity;
import com.idx.naboo.takeout.ui.TakeoutSellerActivity;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.user.hx.EaseLoginActivity;
import com.idx.naboo.user.iom.login.LoginActivity;
import com.idx.naboo.user.personal_center.Personal_center;
import com.idx.naboo.user.personal_center.order.OrderTimeActivity;
import com.idx.naboo.utils.AdjustVolume;
import com.idx.naboo.utils.MapUtils;
import com.idx.naboo.utils.MathTool;
import com.idx.naboo.utils.NetStatusUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.video.ui.VideoActivity;
import com.idx.naboo.video.ui.VideoDetailActivity;
import com.idx.naboo.videocall.VideoIntent;
import com.idx.naboo.videocall.call.VideoCallActivity;
import com.idx.naboo.videocall.call.data.CallDataSource;
import com.idx.naboo.videocall.call.data.CallInjection;
import com.idx.naboo.videocall.call.data.CallRepository;
import com.idx.naboo.videocall.call.data.MissedCall;
import com.idx.naboo.videocall.friend.AgreeFriendListener;
import com.idx.naboo.videocall.friend.DeleteFriendListener;
import com.idx.naboo.videocall.friend.FriendAct;
import com.idx.naboo.videocall.friend.RejectFriendListener;
import com.idx.naboo.videocall.friend.data.Friend;
import com.idx.naboo.videocall.friend.data.FriendDataSource;
import com.idx.naboo.videocall.friend.data.FriendInjection;
import com.idx.naboo.videocall.friend.data.FriendRepository;
import com.idx.naboo.videocall.utils.CallUtil;
import com.idx.naboo.videocall.utils.SpUtils;
import com.idx.naboo.weather.WeatherActivity;
import com.idx.naboo.wenzhi.ASRManager;
import com.idx.naboo.wenzhi.SpeakDialog;

import com.wenzhi.asr.WzActivationStatus;
import com.wenzhi.asr.WzRecognizeListener;
import com.wenzhi.asr.WzSpeechStatus;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.bean.TakeoutmenuBean;
import net.imoran.sdk.entity.info.ScenesDataEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.impl.RequestCallback;
import net.imoran.sdk.service.nli.NLIRequest;
import net.imoran.sdk.tts.core.TTSListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeakService extends Service {
    private static final String TAG = SpeakService.class.getName();
    /**
     * 唤醒词，唤醒后开启会话
     */
    private static final int CONSTANT_SESSION_START = 0x001;
    /**
     * 1.用户说再见，结束会话
     * 2.唤醒后，用户一定时间内未交互，自动结束会话
     * 3.完成指令后，开始播放影音内容，自动结束会话
     * 4.识别异常，自动结束会话
     */
    private static final int CONSTANT_SESSION_END = 0x002;
    /**
     * 继唤醒，播放问候音频之后，开始进入识别状态
     */
    private static final int CONSTANT_RECOGNIZE_START = 0x101;
    /**
     * 开始识别后，一定时间内未检测到用户说话
     */
    private static final int CONSTANT_RECOGNIZE_TIMEOUT = 0x102;
    /**
     * 成功获取识别结果
     */
    private static final int CONSTANT_RECOGNIZE_SUCCESS = 0x103;
    /**
     * 识别异常
     */
    private static final int CONSTANT_RECOGNIZE_ERROR = 0x104;
    /**
     * 开始播放语音
     */
    private static final int CONSTANT_TTS_SPEAK_START = 0x200;
    /**
     * 语音播放结束
     */
    private static final int CONSTANT_TTS_SPEAK_END = 0x201;
    /**
     * 停止语音播放
     */
    private static final int CONSTANT_TTS_SPEAK_STOP = 0x202;
    /**
     * 语音播放异常
     */
    private static final int CONSTANT_TTS_SPEAK_ERROR = 0x203;
    /**
     * 播放完问候语音，到开始识别的时间间隔
     */
    private static final int TIME_DELAY = 350;
    /**
     * 成功接收Server返回的交互结果
     */
    private static final int CONSTANT_JSON_RECEIVED = 0x400;
    /**
     * 返回交互结果异常
     */
    private static final int CONSTANT_JSON_RECEIVE_ERROR = 0x401;
    /**
     * 最大识别超时次数
     */
    private static final int mRegTimeoutMax = 2;
    private ASRManager mASRManager;
    private TTSManager mTTSManager;
    /**
     * 会话状态标识
     */
    private boolean isSessionEnd = true;
    private boolean isRecognizeValid = false;
    private boolean isRecognizing = false;
    /**
     * 会话状态框
     */
    private SpeakDialog mSpeakDialog;
    /**
     * 各功能语音交互结果监听器
     */
    private DataListener mDataListener;
    /**
     * 识别超时计数
     */
    private int mRegTimeoutCount = 0;
    /**
     * 暂存交互结果
     */
    private String mJson = null;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private SpeakBroadcastReceiver mReceiver = new SpeakBroadcastReceiver();

    private String[] mVoiceWelcome;
    private String[] mVoiceNothing;
    private String[] mVoiceTimeout;
    private String[] mVoiceBye;
    private String[] mVoiceRepeat;
    private String[] mVoiceSorry;
    private AMapLocation mAMapLocation;

    /**
     * 将各数据收集至Handler处理
     */
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case CONSTANT_SESSION_START:
                    startSession();
                    break;
                case CONSTANT_RECOGNIZE_ERROR:
                    endSession();
                    break;
                case CONSTANT_RECOGNIZE_START:
                    mTTSManager.stop();
                    mASRManager.startRecognize();
                    showDialogRecognizing();
                    isRecognizing = true;
                    isRecognizeValid = true;
                    break;
                case CONSTANT_RECOGNIZE_TIMEOUT:
                    if (!isSessionEnd) {
                        String voice;
                        if (++mRegTimeoutCount < mRegTimeoutMax) {
                            isRecognizeValid = false;
                            mASRManager.stopRecognize();
                            voice = mVoiceNothing[MathTool.randomValue(mVoiceNothing.length)];
                        } else {
                            isSessionEnd = true;
                            voice = mVoiceTimeout[MathTool.randomValue(mVoiceTimeout.length)];
                        }
                        mTTSManager.speak(voice, true);
                        dismissDialog();
                    }
                    break;
                case CONSTANT_RECOGNIZE_SUCCESS:
                    if (!isSessionEnd && isRecognizing && isRecognizeValid) {
                        mASRManager.stopRecognize();
                        isRecognizing = false;
                        final String result = (String) msg.obj;
                        updateDialogMessage(result);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRegTimeoutCount = 0;
                                dismissDialog();
                                if (result.equals("选择方案一。")) {
                                    mTTSManager.speak("好的，已为您选择1号方案", false);
                                    startTarget(MapActivity.class, "1");
                                } else if (result.equals("选择方案二。")) {
                                    mTTSManager.speak("好的，已为您选择2号方案", false);
                                    startTarget(MapActivity.class, "2");
                                } else if (result.equals("选择方案三。")) {
                                    mTTSManager.speak("好的，已为您选择3号方案", false);
                                    startTarget(MapActivity.class, "3");
                                } else {
                                    VUIDataEntity vuiDataEntity = ((NabooApplication) getApplication()).getDataEntity();
                                    List<ScenesDataEntity> scenesDataEntityList = vuiDataEntity.getScenes();
                                    if (scenesDataEntityList != null && scenesDataEntityList.size() > 0) {
                                        Log.e(TAG, "page_id=" + scenesDataEntityList.get(0).getPage_id());
                                        Log.e(TAG, "query_id=" + scenesDataEntityList.get(0).getQuery_id());
                                        Log.e(TAG, "tell=" + result + " data=" + vuiDataEntity.getScenes().size());
                                    }
                                    CartReportBean<TakeoutmenuBean.TakeoutmenuEntity> entityCartReportBean = (CartReportBean<TakeoutmenuBean.TakeoutmenuEntity>) vuiDataEntity.getPresent_data();
                                    if (entityCartReportBean != null) {
                                        CartReportBean.CartInfoBean<TakeoutmenuBean.TakeoutmenuEntity> takeoutCartInfoBean = entityCartReportBean.getCart_info();
                                        if (takeoutCartInfoBean != null) {
                                            Log.e(TAG, "type: " + takeoutCartInfoBean.getType());
                                            List<TakeoutmenuBean.TakeoutmenuEntity> lists = takeoutCartInfoBean.getData();
                                            if (lists != null) {
                                                for (TakeoutmenuBean.TakeoutmenuEntity entity : lists) {
                                                    Log.e(TAG, "food_name: " + entity.getName());
                                                }
                                            }
                                        }
                                    }

                                    Log.e(TAG, "scenes: " + vuiDataEntity.getScenes());
                                    Log.e(TAG, "custom_data: " + vuiDataEntity.getCustom_data());
                                    Log.e(TAG, "present_data: " + vuiDataEntity.getPresent_data());
                                    Log.e(TAG, "voice_print: " + vuiDataEntity.getVoicePrint());
                                    ImoranManager.getInstance(getBaseContext()).tell(result, ((NabooApplication) getApplication()).getDataEntity(), mTellCallback);
                                }
                            }
                        }, 1500);
                    }
                    break;
                case CONSTANT_JSON_RECEIVED:
                    if (!isSessionEnd && !isRecognizing) {
                        String json = (String) msg.obj;
                        try {
                            handleMoranResponse(json);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case CONSTANT_JSON_RECEIVE_ERROR:
                    if (!isSessionEnd && !isRecognizing) {
                        String voice = mVoiceSorry[MathTool.randomValue(mVoiceSorry.length)];
                        mTTSManager.speak(voice, true);
                    }
                    break;
                case CONSTANT_TTS_SPEAK_START:
                    if (!isSessionEnd && isRecognizing) {
                        mASRManager.stopRecognize();
                    }
                    break;
                case CONSTANT_TTS_SPEAK_END:
                    if (!isSessionEnd) {
                        SpeakService.this.sendMessageDelayed(CONSTANT_RECOGNIZE_START, null, TIME_DELAY);
                    } else {
                        SpeakService.this.sendMessageDelayed(CONSTANT_SESSION_END, null, TIME_DELAY);
                    }
                    break;
                case CONSTANT_TTS_SPEAK_STOP:
                    break;
                case CONSTANT_TTS_SPEAK_ERROR:
                case CONSTANT_SESSION_END:
                    endSession();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
        init();
        mRepository = FriendInjection.getInstance(this);
        mCallRepository = CallInjection.getInstance(this);
        addFriendListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return new SpeakBinder();
    }

    private void init() {
        //初始化问之
        mASRManager = ASRManager.getInstance();
        mASRManager.init(getApplicationContext(), mWzRecognizeListener, new SpeakWzRecordListener());
        mASRManager.start();

        //初始化TTS
        mTTSManager = TTSManager.getInstance(getBaseContext());
        mTTSManager.setTTSListener(mTTSListener);

        updateLocationToMoran();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Media.ACTION_PLAY);
        intentFilter.addAction(Media.ACTION_PAUSE);
        intentFilter.addAction(Execute.ACTION_START);
        intentFilter.addAction(Execute.ACTION_SUCCESS);
        intentFilter.addAction(Execute.ACTION_ERROR);
        intentFilter.addAction(VideoIntent.ACTION_CALL_START);
        intentFilter.addAction(VideoIntent.ACTION_CALL_END);
        registerReceiver(mReceiver, intentFilter);
        initData();
    }

    private void initData() {
        mVoiceWelcome = getResources().getStringArray(R.array.voice_welcome);
        mVoiceNothing = getResources().getStringArray(R.array.voice_nothing);
        mVoiceTimeout = getResources().getStringArray(R.array.voice_timeout);
        mVoiceBye = getResources().getStringArray(R.array.voice_bye);
        mVoiceRepeat = getResources().getStringArray(R.array.voice_repeat);
        mVoiceSorry = getResources().getStringArray(R.array.voice_sorry);
    }

    public void updateLocationToMoran() {
        if (NetStatusUtils.isWifiConnected(getBaseContext())) {
            MapUtils.setCallBack(new MapUtils.CallBack() {
                @Override
                public void call(AMapLocation amap) {
                    //拿当前城市名称
                    String mCityNameDefault = amap.getCity();
                    android.util.Log.d(TAG, "call: city=" + mCityNameDefault);
                    //拿当前amap做拼接工作
                    mAMapLocation = amap;
                    //设置蓦然用到的定为坐标
                    ImoranManager.getInstance(getBaseContext()).setLocation(amap);
                }
            });
            MapUtils.getCity(getApplicationContext());
        }
    }

    /**
     * 开启一次会话
     */
    private void startSession() {
        if (NetStatusUtils.isOnline(getBaseContext())) {
            mASRManager.stopRecognize();
            mTTSManager.stop();
            mRegTimeoutCount = 0;
            isSessionEnd = false;
            isRecognizing = false;
            showDialogBegin();
            String voice = mVoiceWelcome[MathTool.randomValue(mVoiceWelcome.length)];
            mTTSManager.speak(voice, true);
            if (mDataListener != null) {
                mDataListener.onSessionStateChanged(SessionState.START);
            }
        } else {
            ToastUtil.showToast(getBaseContext(), getString(R.string.net_error));
            mTTSManager.speak(getString(R.string.net_error), false);
        }
    }

    /**
     * 结束会话
     */
    private void endSession() {
        mASRManager.stopRecognize();
        isSessionEnd = true;
        isRecognizing = false;
        dismissDialog();
        if (mDataListener != null) {
            mDataListener.onSessionStateChanged(SessionState.END);
        }
    }

    private void handleMoranResponse(String json) {
        if (json == null || json.equals("")) {
            return;
        }
        JsonData jsonData = JsonUtil.createJsonData(json);
        String domain = jsonData.getDomain();
        String tts = jsonData.getTts();
        String type = jsonData.getType();
        Log.d(TAG, "TTS=" + tts);
        switch (domain) {
            case NabooActions.Weather.TARGET_WEATHER:
                startTarget(WeatherActivity.class, json);
                break;
            case NabooActions.Music.TARGET_MUSIC:
                android.util.Log.d(TAG, "handleMoranResponse: music");
                startTarget(MusicActivity.class, json);
                break;
            case NabooActions.Video.TARGET_VIDEO:
                String intention = jsonData.getIntention();
                if (intention.equals("watching") || intention.equals("detailing")) {
                    startTarget(VideoDetailActivity.class, json);
                } else if (intention.equals("searching")) {
                    startTarget(VideoActivity.class, json);
                }
                break;
            case NabooActions.Dish.TARGET_DISH:
                startTarget(DishActivity.class, json);
                break;
            case NabooActions.Map.TARGET_MAP:
            case NabooActions.Map.TARGET_RESTAURANT:
            case NabooActions.Map.TARGET_VIEWSPOT:
                startTarget(MapActivity.class, json);
                break;
            case NabooActions.TakeOut.TARGET_TAKEOUT:
                if (type.equals(NabooActions.TakeOut.TARGET_TYPE_SHOP)) {
                    startTarget(TakeoutActivity.class, json);
                } else if (type.equals(NabooActions.TakeOut.TARGET_TYPE_MENU)) {
                    startTarget(TakeoutSellerActivity.class, json);
                }
                break;
            case NabooActions.TakeOut.TARGET_TAKEOUT_CAR:
//                !(topActivity().contains("LinQuConfirmOrderActivity") || topActivity().contains("PayOrderActivity"))
                if (topActivity().contains("TakeoutCarActivity")) {
                    startTarget(TakeoutCarActivity.class, json);
                }
                break;
            case NabooActions.Figure.TARGET_FIGURE:
                startTarget(FigureActivity.class, json);
                break;
            case NabooActions.News.TARGET_NEWS:
                startTarget(NewsActivity.class, json);
                break;
            case NabooActions.Phone.TARGET_PHONE:
                CallUtil.openVideoCall(getApplicationContext(), json);
                break;
            case NabooActions.Cmd.TARGET_CMD:
                cmdType(type, json);
                break;
            case NabooActions.Calendar.TARGET_CALENDAR:
                startTarget(CalenderActivity.class, json);
                break;
            case NabooActions.Order.TARGET_ORDER_LIST:
                sharedPreferencesUtil = new SharedPreferencesUtil(this);
                sharedPreferencesUtil.saveUUID("Order", "Order");
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
                    startTarget(LoginActivity.class, json);
                    android.util.Log.d(TAG, "LoginActivity: " + json);
                } else if (!EMClient.getInstance().isLoggedInBefore()) {
                    startTarget(EaseLoginActivity.class, json);
                    android.util.Log.d(TAG, "EaseLoginActivity: " + json);

                } else {
                    startTarget(OrderTimeActivity.class, json);
                }
                break;
            default:
                // 无界面部分
                if (tts.contains("再见")) {
                    tts = mVoiceBye[MathTool.randomValue(mVoiceBye.length)];
                    isSessionEnd = true;
                } else if (tts.equals("")) {
                    tts = mVoiceRepeat[MathTool.randomValue(mVoiceRepeat.length)];
                }
                mTTSManager.speak(tts, true);
        }

    }

    /**
     * 蓦然数据回调接口
     */
    private NLIRequest.onRequest mTellCallback = new NLIRequest.onRequest() {
        @Override
        public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
            Log.d(TAG, "接收Json数据: " + s);
            sendMessage(CONSTANT_JSON_RECEIVED, s);
        }

        @Override
        public void onError() {
            Log.e(TAG, "接受Json Error: ");
            sendMessage(CONSTANT_JSON_RECEIVE_ERROR, null);
        }
    };

    /**
     * 问之语音识别回调
     */
    private WzRecognizeListener mWzRecognizeListener = new WzRecognizeListener() {
        @Override
        public void onVoiceStart() {
        }

        @Override
        public void onVoiceEnd() {
        }

        @Override
        public void onRecognizingPartialResult(String s) {
        }

        @Override
        public void onRecognizingResult(WzSpeechStatus wzSpeechStatus, RecognizedResult recognizedResult) {
            switch (wzSpeechStatus) {
                case WZ_SPEECH_STATUS_SUCCESS:
                    Log.d(TAG, ">>>>>识别结果: " + recognizedResult.recognizedString + "<<<<<");
                    sendMessage(CONSTANT_RECOGNIZE_SUCCESS, recognizedResult.recognizedString);
                    break;
                default:
                    sendMessage(CONSTANT_RECOGNIZE_ERROR, wzSpeechStatus);
            }
        }

        @Override
        public void onActivatedResult(String s, String s1, WzActivationStatus wzActivationStatus) {
            Log.d(TAG, "引擎状态: " + wzActivationStatus.toString());
            switch (wzActivationStatus) {
                case WZ_ACTIVATION_STATUS_WAKEUP:
                    Log.d(TAG, "WAKE UP.");
                    sendMessage(CONSTANT_SESSION_START, null);
                    break;
                case WZ_ACTIVATION_STATUS_SLEEP_TIMEOUT:
                    sendMessage(CONSTANT_RECOGNIZE_TIMEOUT, null);
            }
        }
    };

    /**
     * 语音播放回调
     */
    private TTSListener mTTSListener = new TTSListener() {
        @Override
        public void onPlayBegin(@Nullable String s) {
            Log.d(TAG, "TTS onPlayBegin: 语音开始");
            sendMessage(CONSTANT_TTS_SPEAK_START, null);
        }

        @Override
        public void onPlayEnd(@Nullable String s) {
            Log.d(TAG, "TTS onPlayEnd: 语音结束");
            sendMessage(CONSTANT_TTS_SPEAK_END, null);
        }

        @Override
        public void onPlayStopped(@Nullable String s) {
            Log.d(TAG, "TTS onPlayBegin: 语音暂停");
            sendMessage(CONSTANT_TTS_SPEAK_STOP, null);
        }

        @Override
        public void onError(@Nullable String s, int i, String s1) {
            Log.d(TAG, "TTS onError: 语音播放错误");
            sendMessage(CONSTANT_TTS_SPEAK_ERROR, null);
        }

        @Override
        public void onPlayPieceStart(String s, int i, int i1) {

        }

        @Override
        public void onPlayPieceEnd(String s, int i, int i1) {

        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }
    };

    public RequestCallback mClearRequestCallback = new RequestCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess: clear context");
        }

        @Override
        public void onFail() {

        }

        @Override
        public void onError() {

        }

        @Override
        public void onResult(String s) {

        }
    };

    public class SpeakBinder extends Binder implements IService {
        @Override
        public void setDataListener(DataListener listener) {
            mDataListener = listener;
        }

        @Override
        public void requestData(String text, NLIRequest.onRequest callback) {
            ImoranManager.getInstance(getBaseContext()).tell(text, ((NabooApplication) getApplication()).getDataEntity(), callback);
        }

        @Override
        synchronized public String getJson() {
            return mJson;
        }

        public SpeakService getService() {
            return SpeakService.this;
        }
    }

    private void sendMessage(int what, Object object) {
        sendMessageDelayed(what, object, 0);
    }

    private void sendMessageDelayed(int what, Object object, int delay) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            if (object != null) {
                msg.obj = object;
            }
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    /**
     * 启动目的领域，或执行领域中的指令
     *
     * @param clz  启动目标的类名
     * @param json 领域所需要的json
     */
    private void startTarget(Class<?> clz, String json) {
        if (!topActivity().contains("VideoCallActivity")) {
            if (isActivityRunning(clz.getName())) {
                if (mDataListener != null) {
                    android.util.Log.d(TAG, "startTarget: 111");
                    mDataListener.onJsonReceived(json);
                }
            } else {
                //部分json数据过大，不能使用intent携带，需getJson接口主动获取111
                android.util.Log.d(TAG, "startTarget: 2222");

                mJson = json;
                mTTSManager.stop();
                Intent intent = new Intent(getBaseContext(), clz);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    /**
     * 显示状态栏，识别中
     */
    private void showDialogRecognizing() {
        if (mSpeakDialog == null) {
            mSpeakDialog = new SpeakDialog(getBaseContext());
        }
        mSpeakDialog.showSpeaking();
    }

    /**
     * 显示状态栏，开始识别
     */
    private void showDialogBegin() {
        if (mSpeakDialog == null) {
            mSpeakDialog = new SpeakDialog(getBaseContext());
        }
        mSpeakDialog.showReady();
    }

    /**
     * 显示状态栏，识别结果
     */
    private void updateDialogMessage(String text) {
        if (mSpeakDialog != null) {
            mSpeakDialog.setText(text);
        }
    }

    /**
     * 移除状态栏
     */
    private void dismissDialog() {
        if (mSpeakDialog != null) {
            mSpeakDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mHandler != null) {
            mHandler = null;
        }
        unregisterReceiver(mReceiver);
        mASRManager.destroy();
        ScenesDataEntity scenesDataEntity = ((NabooApplication) getApplication()).getDataEntity().getScenes().get(0);
        String pageId = scenesDataEntity.getPage_id();
        String queryId = scenesDataEntity.getQuery_id();
        ImoranManager.getInstance(getBaseContext()).clearContext(pageId, queryId, mClearRequestCallback);
        ImoranManager.getInstance(getBaseContext()).destroy();
        mTTSManager.destroy();
        if (mSpeakDialog != null) {
            mSpeakDialog.dismiss();
            mSpeakDialog = null;
        }
    }

    /**
     * 通用指令
     *
     * @param type 指令类型
     * @param json 通用指令所需json
     */
    private void cmdType(String type, String json) {
        String name = topActivity();
        switch (type) {
            case "up":
            case "down":
            case "to":
            case "unmute":
            case "mute":
            case "max":
            case "min":
                AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                int sysVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.d(TAG, "cmdType: 调前" + sysVoice);
                sysVoice = AdjustVolume.adjustVolume(this, json, sysVoice);
                Log.d(TAG, "cmdType: 调后" + sysVoice);
                break;
            case "main_page"://回到主页
                startTarget(HomeActivity.class, "");
                break;
            case "show_version":
                sharedPreferencesUtil = new SharedPreferencesUtil(this);
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
                    startTarget(LoginActivity.class, json);
                } else if (!EMClient.getInstance().isLoggedInBefore()) {
                    startTarget(EaseLoginActivity.class, json);
                } else {
                    startTarget(Personal_center.class, json);
                }

                break;
            case "usercenter": //打开个人中心
                sharedPreferencesUtil = new SharedPreferencesUtil(this);
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
                    startTarget(LoginActivity.class, json);
                } else if (!EMClient.getInstance().isLoggedInBefore()) {
                    startTarget(EaseLoginActivity.class, json);
                } else {
                    startTarget(Personal_center.class, json);
                }

                break;
            default:
                if (!name.equals("")) {
                    if (mDataListener != null) {
                        mDataListener.onJsonReceived(json);
                    }
                }
                break;
        }
    }

    /**
     * 判断对应的Activity是否正在前台运行
     *
     * @param name Activity 名字
     * @return
     */
    public boolean isActivityRunning(String name) {
        boolean result;
        result = name.equals(topActivity());
        Log.d(TAG, "isActivityRunning: " + name + ", " + result);
        return result;
    }

    /**
     * 获取当前栈顶Activity的类名
     *
     * @return
     */
    private String topActivity() {
        String result = "";
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningTaskInfo> lists = (ArrayList<ActivityManager.RunningTaskInfo>) manager.getRunningTasks(1);
        if (lists != null && lists.size() > 0) {
            ComponentName cpn = lists.get(0).topActivity;
            result = cpn.getClassName();
        }
        Log.d(TAG, "topActivity: " + result);
        return result;
    }

    /**
     * 添加好友监听
     */
    private String regex = "[A-Za-z0-9]+";
    private FriendRepository mRepository;
    private CallRepository mCallRepository;
    private NotificationManager mNotificationManager;
    private AgreeFriendListener mAgreeListener;
    private RejectFriendListener mRejectListener;
    private DeleteFriendListener mDeleteListener;

    public void setAgreeFriendListener(AgreeFriendListener listener) {
        mAgreeListener = listener;
    }

    public void setRejectFriendListener(RejectFriendListener listener) {
        mRejectListener = listener;
    }

    public void setDeleteFriendListener(DeleteFriendListener listener) {
        mDeleteListener = listener;
    }

    private void addFriendListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            //增加
            @Override
            public void onContactAdded(String s) {
                Log.d(TAG, "onContactAdded: 添加好友" + s);
            }

            //删除
            @Override
            public void onContactDeleted(final String s) {
                Log.d(TAG, "onContactDeleted: 删除好友");
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    Log.d(TAG, "onContactDeleted: 账号删除好友" + s);
                    mRepository.deleteFriend(s, new FriendDataSource.DeleteFriendSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            if (topActivity().contains("FriendAct")) {if (mDeleteListener != null) {mDeleteListener.delete(s);}}
                        }
                    });

                    mCallRepository.queryPointCall(s, new CallDataSource.LoadPointCallCallback() {//删除未接电话
                        @Override
                        public void onSuccess(MissedCall call) {
                            Log.e(TAG, "delete: "+call.callAccount);
                            mCallRepository.deleteCall(s);
                        }

                        @Override
                        public void onError() {}
                    });
                } else {
                    Log.d(TAG, "onContactDeleted: 昵称删除好友" + s);
                    mRepository.deleteAliasFriend(s, new FriendDataSource.DeleteFriendAliasSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            if (topActivity().contains("FriendAct")) {if (mDeleteListener != null) {mDeleteListener.delete(s);}}
                        }
                    });
                }
            }

            //收到邀请
            @Override
            public void onContactInvited(String s, String s1) {
                mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setSmallIcon(R.mipmap.video_call_notification_icon);
                builder.setPriority(Notification.PRIORITY_HIGH);
                builder.setAutoCancel(true);
                builder.setContentText("点击同意添加好友");
                builder.setContentTitle(s1 + " 要添加你为好友...");
                Intent intent = new Intent(getApplicationContext(), FriendAct.class);
                SpUtils.put(getApplicationContext(), "notification_account", s);
                SpUtils.put(getApplicationContext(), "notification_name", s1);
//                SpUtils.put(getApplicationContext(), "notification_userid", mUserId);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pi);//相当于添加点击事件，打开活动
                builder.setOngoing(true);//是否为正在进行通知
                builder.setWhen(System.currentTimeMillis());//通知时间
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                Notification notification = builder.build();
                notification.sound = uri;
                mNotificationManager.notify(0, notification);
                Log.d(TAG, "onContactInvited: 收到添加邀请" + s1 + "-" + s);
            }

            //同意
            @Override
            public void onFriendRequestAccepted(final String s) {
                Log.d(TAG, "onFriendRequestAccepted: 同意:" + s);
                String toUserName = SpUtils.get(getApplicationContext(), Constant.VIDEO_CALL_TO_USER_NAME, "");
                String toUserAccount = SpUtils.get(getApplicationContext(), Constant.VIDEO_CALL_TO_USERACCOUNT, "");
                Log.d(TAG, "onFriendRequestAccepted: " + toUserAccount + "-" + toUserName);
                if (!TextUtils.isEmpty(toUserName) && !TextUtils.isEmpty(toUserAccount)) {
                    Friend friend = new Friend();
                    friend.alias = toUserName;
                    String userId = SpUtils.get(getBaseContext(), "hyphenate_current_user", "");
                    Log.d(TAG, "onFriendRequestAccepted: ");
                    friend.userId = userId;
                    friend.friendAccount = toUserAccount;
                    mRepository.insertFriend(friend, new FriendDataSource.AddFriendSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            if (topActivity().contains("FriendAct")) {if (mAgreeListener != null) {mAgreeListener.agree(s);}}
                        }
                    });
                    SpUtils.put(getApplicationContext(), Constant.VIDEO_CALL_TO_USER_NAME, "");
                    SpUtils.put(getApplicationContext(), Constant.VIDEO_CALL_TO_USERACCOUNT, "");
                }
            }

            //拒绝
            @Override
            public void onFriendRequestDeclined(String s) {
                Log.d(TAG, "onFriendRequestDeclined: 拒绝" + s);
                if (topActivity().contains("FriendAct")) {if (mRejectListener != null) {mRejectListener.reject(s);}}
            }
        });
    }

    public class SpeakBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: action=" + action);
            switch (action) {
                case VideoIntent.ACTION_CALL_START:
                case Intent.ACTION_SCREEN_OFF:
                    isSessionEnd = true;
                    isRecognizing = false;
                    dismissDialog();
                    mASRManager.stop();
                    mTTSManager.stop();
                    mSpeakDialog = null;
                    break;
                case VideoIntent.ACTION_CALL_END:
                case Intent.ACTION_USER_PRESENT:
                    mASRManager.start();
                    break;
                case Media.ACTION_PLAY:
                    if (!isSessionEnd) {
                        sendMessage(CONSTANT_SESSION_END, null);
                    }
                    break;
                case Execute.ACTION_START:
                    break;
                case Execute.ACTION_SUCCESS:
                    android.util.Log.d(TAG, "onReceive: json　滞空");
                    mJson = "";
                    break;
                case Execute.ACTION_ERROR:
                    break;
                default:
            }
        }
    }
}
