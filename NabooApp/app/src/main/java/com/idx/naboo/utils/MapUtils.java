package com.idx.naboo.utils;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by hayden on 18-2-1.
 */

public class MapUtils {

    //声明AMapLocationClient类对象
    private static AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private static AMapLocationClientOption mLocationOption = null;
    private static final String TAG = "MapUtils";

    /**
     * 设置定位参数
     *
     * @return 定位参数类
     */
    private static AMapLocationClientOption getOption() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //是否只定位一次
        mLocationOption.setOnceLocation(true);
        return mLocationOption;
    }


    public static void getCity(final Context context) {
        //初始化client
        mLocationClient = new AMapLocationClient(context);
        //设置定位参数
        mLocationClient.setLocationOption(getOption());
        // 设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    Log.d(TAG, "onLocationChanged: 定位回调至ControllerService:"+aMapLocation.getCity());
                    callBack.call(aMapLocation);
                } else {
                    Log.d(TAG, "onLocationChanged: 网络没问题，但获取失败默认返回了 深圳市");
                    callBack.call(null);
                }
            }
        });
        // 启动定位
        if (!mLocationClient.isStarted()) {
            //开启
            new Thread() {
                public void run() {
                    if (NetStatusUtils.isMobileConnected(context) || NetStatusUtils.isWifiConnected(context)) {
                        mLocationClient.startLocation();//开启定位比较耗时，在启动的时候就调用
                    } else {
                        Log.d(TAG, "run: 网络有问题,默认返回了 深圳市");
                        callBack.call(null);
                    }
                }
            }.start();
        }
    }

    public static void setCallBack(CallBack cb){
        callBack = cb;
    }

    public interface CallBack{
        void call(AMapLocation aMapLocation);
    }

    private static CallBack callBack;
}
