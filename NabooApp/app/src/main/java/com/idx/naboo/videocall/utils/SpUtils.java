package com.idx.naboo.videocall.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类 存储数据操作类
 * Created by danny on 3/21/18.
 */

public class SpUtils {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "video_call";

    /**
     * 保存数据的方法
     *
     * @param appContext 上下文对象
     * @param key 存储数据key值
     * @param value 存储数据
     */
    public static void put(Context appContext, String key, String value) {
        SharedPreferences sp = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 得到保存数据的方法
     *
     * @param appContext 上下文对象
     * @param key 获取数据key值
     * @param defaultObject 默认值
     * @return
     */
    public static String get(Context appContext, String key, String defaultObject) {
        SharedPreferences sp = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultObject);
    }

    /**
     * 保存数据的方法
     *
     * @param appContext 上下文对象
     * @param key 存储数据key值
     * @param value 存储数据
     */
    public static void putBoolean(Context appContext, String key, boolean value) {
        SharedPreferences sp = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 得到保存数据的方法
     *
     * @param appContext 上下文对象
     * @param key 获取数据key值
     * @param defaultObject 默认值
     * @return
     */
    public static boolean getBoolean(Context appContext, String key, boolean defaultObject) {
        SharedPreferences sp = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultObject);
    }
}
