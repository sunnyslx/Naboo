package com.idx.naboo.imoran;

import android.content.Context;
import android.support.annotation.Nullable;

import net.imoran.sdk.tts.core.TTSClient;
import net.imoran.sdk.tts.core.TTSClientFactory;
import net.imoran.sdk.tts.core.TTSListener;

public class TTSManager {
    private static final String TAG = TTSManager.class.getName();
    private static TTSManager INSTANCE = null;
    private static final String mAppId = "10874541";
    private static final String mAppKey = "i4Lx2jdz5DNOt4QVB8O3peSX";
    private static final String mAppSecret = "19I0ZaAvU4EpOG6p07nzpdwjuh1jigHZ";
    private static final int TTS_SPEAK_SPEED = 100;
    private TTSClient mTTSClient;
    private Callback mCallback;
    private TTSListener listener;
    private boolean mEnable = true;

    public interface Callback {
        void onSpeechProgressChanged(String var1, int var2);

        void onPlayBegin(String s);

        void onPlayEnd(String s);

        void onPlayStopped(String s);

        void onError(@Nullable String s, int i, String s1);
    }

    private TTSListener mListener = new TTSListener() {
        @Override
        public void onPlayBegin(@Nullable String s) {
            if (listener != null && mEnable) {
                listener.onPlayBegin(s);
            }
            if (mCallback != null) {
                mCallback.onPlayBegin(s);
            }
        }

        @Override
        public void onPlayEnd(@Nullable String s) {
            if (listener != null && mEnable) {
                listener.onPlayEnd(s);
            }
            if (mCallback != null) {
                mCallback.onPlayEnd(s);
            }
        }

        @Override
        public void onPlayStopped(@Nullable String s) {
            if (listener != null && mEnable) {
                listener.onPlayStopped(s);
            }
            if (mCallback != null) {
                mCallback.onPlayStopped(s);
            }
        }

        @Override
        public void onError(@Nullable String s, int i, String s1) {
            if (listener != null && mEnable) {
                listener.onError(s, i, s1);
            }
            if (mCallback != null) {
                mCallback.onError(s, i, s1);
            }
        }

        @Override
        public void onPlayPieceStart(String s, int i, int i1) {
            if (listener != null && mEnable) {
                listener.onPlayPieceStart(s, i, i1);
            }
        }

        @Override
        public void onPlayPieceEnd(String s, int i, int i1) {
            if (listener != null && mEnable) {
                listener.onPlayPieceEnd(s, i, i1);
            }
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {
            if (listener != null && mEnable) {
                listener.onSpeechProgressChanged(s, i);
            }
            if (mCallback != null) {
                mCallback.onSpeechProgressChanged(s, i);
            }
        }
    };

    private TTSManager(Context context) {
        mTTSClient = TTSClientFactory.createSingletonClient(context, mAppId, mAppKey, mAppSecret);
        mTTSClient.setSpeakSpeed(TTS_SPEAK_SPEED);
    }

    public TTSClient getTTSClient() {
        return mTTSClient;
    }

    public static TTSManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TTSManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TTSManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public void setTTSListener(TTSListener listener) {
        this.listener = listener;
    }

    public void speak(String text, Callback callback, boolean enableGlobalListener) {
        if (callback != null) {
            mCallback = callback;
        }
        mEnable = enableGlobalListener;
        if (mTTSClient != null) {
            mTTSClient.play(text, "playVoice", mListener);
        }
    }

    public void speak(String text, boolean enableGlobalListener) {
        if (mCallback != null) {
            mCallback = null;
        }
        mEnable = enableGlobalListener;
        if (mTTSClient != null) {
            if (enableGlobalListener) {
                mTTSClient.play(text, "playVoice", mListener);
            } else {
                mTTSClient.play(text);
            }
        }
    }

    public void enableGlobalListener(boolean enable){
        mEnable = enable;
    }

    public void speak(String text) {
        if (mTTSClient != null) {
            mTTSClient.play(text);
        }
    }

    public boolean isPlaying() {
        if (mTTSClient != null) {
            return mTTSClient.isPlaying();
        }
        return false;
    }

    public void stop() {
        if (mTTSClient != null) {
            mTTSClient.stop();
        }
    }

    public void pause() {
        if (mTTSClient != null) {
            mTTSClient.pause();
        }
    }

    public void resume() {
        if (mTTSClient != null) {
            mTTSClient.resume();
        }
    }

    public void destroy() {
        if (mTTSClient != null) {
            mTTSClient.stop();
            mTTSClient.destroy();
            mTTSClient = null;
        }
        INSTANCE = null;
    }
}
