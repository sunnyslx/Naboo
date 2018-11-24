package com.idx.naboo.service;

/**
 * Created by derik on 18-5-19.
 */

public class Media {

    public static final String ACTION_PLAY = "com.idx.naboo.action.play";
    public static final String ACTION_PAUSE = "com.idx.naboo.action.pause";
    public static final String ACTION_STOP = "com.idx.naboo.action.stop";
    public static final String ACTION_ERROR = "com.idx.naboo.action.error";

    public enum States {
        PLAY,
        STOP,
        PAUSE,
        ERROR
    }
}
