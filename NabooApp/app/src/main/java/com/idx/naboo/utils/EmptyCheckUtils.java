package com.idx.naboo.utils;

import android.text.TextUtils;

/**
 * Created by ryan on 18-3-3.
 * Email: Ryan_chan01212@yeah.net
 */

public class EmptyCheckUtils {
    /**判断字符串是否为空对象或空字符串
     * @param key 带判断字符串
     * @return
     * @error
     */
    public static boolean isEmptyOrNull(String key){
        if (key == null){
            return false;
        }
        if (TextUtils.isEmpty(key)){
            return false;
        }
        if (key.equals("")){
            return false;
        }
        return true;
    }
}
