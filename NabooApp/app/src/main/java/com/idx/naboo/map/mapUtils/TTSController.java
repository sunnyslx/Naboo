package com.idx.naboo.map.mapUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.idx.naboo.imoran.TTSManager;

import net.imoran.sdk.tts.core.TTSClient;

/**
 * 当前DEMO的播报方式是队列模式。其原理就是依次将需要播报的语音放入链表中，播报过程是从头开始依次往后播报。
 * <p>
 * 导航SDK原则上是不提供语音播报模块的，如果您觉得此种播报方式不能满足你的需求，请自行优化或改进。
 */
public class TTSController implements AMapNaviListener, ICallBack {

    @Override
    public void onCompleted(int code) {
        if (handler != null) {
            handler.obtainMessage(1).sendToTarget();
        }
    }

    public static TTSController ttsManager;
    private Context mContext;
    private SystemTTS systemTTS;
    private TTSClient mTTSClient = null;
    private final int CHECK_TTS_PLAY = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_TTS_PLAY:
                    if (!mTTSClient.isPlaying()) {
                        String contains = (String)msg.obj;
                        mTTSClient.play(contains);
                    }
                    break;
            }

        }
    };

    private TTSController(Context context) {
        mContext = context.getApplicationContext();
        systemTTS = SystemTTS.getInstance(mContext);
    }

    public static TTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new TTSController(context);
        }
        return ttsManager;
    }

    public void init() {
        if (systemTTS != null) {
            systemTTS.init();
        }
//        mTTSClient.setCallback(this);
        mTTSClient = TTSManager.getInstance(mContext).getTTSClient();
        Log.i("看下地址", "init: mTTSClient"+mTTSClient.toString());
    }

    public void stopSpeaking() {
        mTTSClient.stop();
    }

    public void destroy() {
        if (systemTTS != null) {
            systemTTS.destroy();
        }
        ttsManager = null;
    }

    /****************************************************************************
     * 以下都是导航相关接口
     ****************************************************************************/


    @Override
    public void onArriveDestination() {
    }

    @Override
    public void onCalculateRouteSuccess() {

    }

    @Override
    public void onArrivedWayPoint(int arg0) {
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
//        if (wordList != null)
//            wordList.addLast("路线规划失败");
    }

    @Override
    public void onEndEmulatorNavi() {
    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
//        if (wordList != null)
//            wordList.addLast(arg1);
        Message msg = Message.obtain();
        msg.what = CHECK_TTS_PLAY;
        msg.obj = arg1;
        handler.sendMessage(msg);
    }

    @Override
    public void onInitNaviFailure() {
    }

    @Override
    public void onInitNaviSuccess() {
    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
//        if (wordList != null)
//            wordList.addLast("前方路线拥堵，路线重新规划");
    }

    @Override
    public void onReCalculateRouteForYaw() {
//        if (wordList != null)
//            wordList.addLast("路线重新规划");
    }

    @Override
    public void onStartNavi(int arg0) {
    }

    @Override
    public void onTrafficStatusUpdate() {
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] infoArray) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] infoArray) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {

    }


    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int parallelRoadType) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] infos) {

    }


    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int type) {

    }

    @Override
    @Deprecated
    public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {

    }
}
