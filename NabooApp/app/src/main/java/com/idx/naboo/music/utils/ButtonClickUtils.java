package com.idx.naboo.music.utils;

/**
 * Created by sunny on 18-5-7.
 * 解决多次点击,引发onError操作
 */

public class ButtonClickUtils {

    private static long lastClickTime;
    public  synchronized  static  boolean isFastClick(){
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }else {
            lastClickTime = time;
            return false;
        }
    }
}
