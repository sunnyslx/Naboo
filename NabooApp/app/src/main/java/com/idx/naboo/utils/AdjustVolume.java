package com.idx.naboo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.idx.naboo.imoran.TTSManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 音量相关工具类
 * Created by danny on 3/16/18.
 */

public class AdjustVolume {
    private static final String TAG = "AdjustVolume";
    private static AudioManager mAudioManager;
    private static int mCurrentVolume = 10;

    // 调整音量
    public static int adjustVolume(Context context, String response, int sysVoice) {
        int current=0;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (!TextUtils.isEmpty(response)) {
            current=judgeVolumeType(context,response,sysVoice);
        } else {
            Log.d(TAG, "adjustVolume: 拿到数据为空");
        }
        return current;
    }

    //调整音量类型
    public static int judgeVolumeType(Context context, String response,int sysVoice) {
        int volumeMax = getMediaMaxVolume();
//        int volumeCurrent = getMediaVolume();
        int volumeAdjust = (int)(volumeMax * 0.2);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            switch (type) {
                case "up":
                    mCurrentVolume = getMediaVolume();
                    if (sysVoice + volumeAdjust < volumeMax) {
                        setMediaVolume(sysVoice + volumeAdjust);
                    } else {
                        setMediaVolume(volumeMax);
                    }
                    if (getMediaVolume()==getMediaMaxVolume()) {
                        TTSManager.getInstance(context).speak("好的，当前音量已为最大值" + getMediaVolume(), true);
                    }else {
                        TTSManager.getInstance(context).speak("好的，为你将音量调大至" + getMediaVolume(), true);
                    }
                    return getMediaVolume();
                case "down":
                    mCurrentVolume = getMediaVolume();
                    if (sysVoice - volumeAdjust > 0) {
                        setMediaVolume(sysVoice - volumeAdjust);
                    } else {
                        mCurrentVolume = getMediaVolume();
                        setMediaVolume(0);
                    }
                    TTSManager.getInstance(context).speak("好的，为你将音量调小至"+getMediaVolume(), true);
                    return getMediaVolume();
                case "to":
                    mCurrentVolume = getMediaVolume();
                    JSONArray array = jsonObject.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("value");
                    Log.d(TAG, "judgeVolumeType: " + array.getString(0));
                    setMediaVolume(Integer.parseInt(array.getString(0)));
                    if (getMediaVolume()==getMediaMaxVolume()) {
                        TTSManager.getInstance(context).speak("好的，为你将音量调至最大值" + getMediaVolume(), true);
                    }else {
                        TTSManager.getInstance(context).speak("好的，为你将音量调至" + getMediaVolume(), true);
                    }
                    return getMediaVolume();
                case "mute":
                    mCurrentVolume = getMediaVolume();
                    setMediaVolume(0);
                    TTSManager.getInstance(context).speak("好的，已设置静音", true);
                    return getMediaVolume();
                case "unmute":
                    setMediaVolume(mCurrentVolume);
                    TTSManager.getInstance(context).speak("好的，已为你恢复声音", true);
                    return getMediaVolume();
                case "max":
                    mCurrentVolume = getMediaVolume();
                    setMediaVolume(volumeMax);
                    TTSManager.getInstance(context).speak("好的，为你将音量调到最大", true);
                    return getMediaVolume();
                case "min":
                    mCurrentVolume = getMediaVolume();
                    setMediaVolume(0);
                    TTSManager.getInstance(context).speak("好的，已设置静音", true);
                    return getMediaVolume();
                default:
                    return 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //设置音量值
    private static void setMediaVolume(int volume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
    }

    //获取多媒体最大音量
    public static int getMediaMaxVolume() {return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);}

    //获取多媒体当前音量
    public static int getMediaVolume() {return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);}
}
