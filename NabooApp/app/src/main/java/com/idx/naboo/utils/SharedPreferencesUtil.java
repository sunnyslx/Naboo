package com.idx.naboo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by hayden on 18-3-15.
 */

public class SharedPreferencesUtil {
    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPreferencesUtil(Context context){
        mSharedPreferences = context.getSharedPreferences("bind_wifi",Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static void setData(Context context,String fragmentName){
        SharedPreferences mSharedPreferences = context.getSharedPreferences("fragment",Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("fragmentName",fragmentName);
        mEditor.apply();
    }

    public static String getData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("fragment",Context.MODE_PRIVATE);
        Log.i("123456789", "getData: 取出来的sp为"+sharedPreferences.getString("fragmentName",""));
        return sharedPreferences.getString("fragmentName","");
    }
    public void saveUUID(String key,String value){
        if(!EmptyCheckUtils.isEmptyOrNull(key)){
            throw new IllegalArgumentException("key 值不能为空");
        }
        mEditor.putString(key,value);
        mEditor.commit();
        Log.d("Franck", "saveBindWifi: ");
    }

    public String getUUID(String key){
        if (!EmptyCheckUtils.isEmptyOrNull(key)){
            throw new IllegalArgumentException("key 值不能为空");
        }
        String result = mSharedPreferences.getString(key,null);
        Log.i(TAG, "getUUID: key = "+key+",value = "+result);
        return result;
    }


    public void saveDrawableResId(String key,int id)
    {
        mEditor.putInt(key, id);
        mEditor.commit();
    }

    public int loadDrawable(String key)
    {
        return mSharedPreferences.getInt(key, 0);
    }
}
