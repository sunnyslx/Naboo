package com.idx.naboo.wenzhi;

/**
 * Created by hayden on 18-4-11.
 */

import android.content.Context;
import com.baidu.android.common.logging.Log;
import com.idx.naboo.music.utils.ToastUtil;
import com.wenzhi.asr.WzRecognizeListener;
import com.wenzhi.asr.WzRecognizer;
import com.wenzhi.asr.WzRecordListener;
import com.idx.naboo.utils.FileCopyUtils;

/**
 * 问之的工具类
 */

public class ASRManager {

    private static final String TAG = ASRManager.class.getName();
    private static final String mAppKey = "78306f26ecff29198964";
    private static final String mAppSecret = "ae790cab26325e3ca13d5fa13660e983";
    private WzRecognizer mRecognizer;
    private static ASRManager INSTANCE;
    private String mDirName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/wz";

    public static ASRManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ASRManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ASRManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context, WzRecognizeListener wzRecognizeListener, WzRecordListener wzRecordListener) {
        try {
            if (mRecognizer == null) {
                //得到对象
                mRecognizer = new WzRecognizer();
                //自动置入文件
                FileCopyUtils.copyFilesToSD(context);
            } else {
                return;
            }

        /*
         * context:用于识别引擎内部访问和android上下文相关资源
         * recognizeListener:识别相关的回调接口，用于通知客户端回调结果
         * appKey:开发用key
         * appSecret:开发用secret
         * configPath:模型数据存放的文件夹
         * recordListener:录音相关接口回调
         */
            mRecognizer.init(context, wzRecognizeListener, mAppKey, mAppSecret, mDirName, wzRecordListener);
            //设置流式效果
            mRecognizer.setContinuos(true);
            mRecognizer.setWakenTime(8000);
        } catch (Exception e){
            e.printStackTrace();
            ToastUtil.showToast(context, "ASR initialization failed");
        }
    }

    public void startRecognize() {
        if (mRecognizer != null) {
            mRecognizer.startWakeup();
        }
    }

    public void stopRecognize() {
        if (mRecognizer != null) {
            mRecognizer.stopWakeup();
        }
    }

    public void start() {
        if (mRecognizer != null) {
            Log.i("CallStateListener", "start被执行: ");
            mRecognizer.start();
        }
    }

    public void stop() {
        if (mRecognizer != null) {
            Log.i("CallStateListener", "stop被执行: ");
            mRecognizer.stop();
            mRecognizer.stopWakeup();
        }
    }

    public void close() {
        if (mRecognizer != null) {
            mRecognizer.close();
        }
    }

    synchronized public void destroy() {
        Log.d(TAG, "destroy");
        if (mRecognizer != null) {
            mRecognizer.stopWakeup();
            mRecognizer.stop();
            mRecognizer.close();
            mRecognizer = null;
        }
        INSTANCE = null;
    }
}
