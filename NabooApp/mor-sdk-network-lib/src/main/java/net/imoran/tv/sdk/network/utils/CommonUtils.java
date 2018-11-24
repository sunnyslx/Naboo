
package net.imoran.tv.sdk.network.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by mengfanshuai on 2017/6/28.
 */

public class CommonUtils {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断程序是否在前台运行
	 *
	 * @param context
	 * @return true 在前台
	 */
	public static boolean isRunningForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
		if (appProcessInfos == null) return false;
		// 枚举进程
		for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
			if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断view是否处于屏幕中
	 *
	 * @param context
	 * @param view
	 * @return
	 */
	public static boolean checkIsVisible(Context context, View view) {
		if (view == null || context == null) return false;
		// 如果已经加载了，判断广告view是否显示出来，然后曝光
		int screenWidth = getScreenMetrics(context).x;
		int screenHeight = getScreenMetrics(context).y;
		Rect rect = new Rect(0, 0, screenWidth, screenHeight);
		int[] location = new int[2];
		view.getLocationInWindow(location);
		if (view.getLocalVisibleRect(rect)) {
			return true;
		} else {
			//view已不在屏幕可见区域;
			return false;
		}
	}

	/**
	 * 获取屏幕宽度和高度，单位为px
	 *
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		return new Point(w_screen, h_screen);
	}

	/**
	 * 获取apk文件后缀名称
	 *
	 * @param pathandname
	 * @return
	 */
	public static String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return "MorTV3.0";
		}

	}

	/**
	 * 打印屏幕的一些基本信息
	 *
	 * @param context
	 */
	public static void printScreenInfo(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
// 屏幕宽度（像素）
		int width = metric.widthPixels;
// 屏幕高度（像素）
		int height = metric.heightPixels;
// 屏幕密度（1.0 / 1.5 / 2.0）
		float density = metric.density;
		float fontScale = metric.scaledDensity;
// 屏幕密度DPI（160 / 240 / 320）
		int densityDpi = metric.densityDpi;
		String info = "fontScale:" + fontScale + "机顶盒型号: " + android.os.Build.MODEL + ",\nSDK版本:"
				+ android.os.Build.VERSION.SDK + ",\n系统版本:"
				+ android.os.Build.VERSION.RELEASE + "\n屏幕宽度（像素）: " + width + "\n屏幕高度（像素）: " + height + "\n屏幕密度:  " + density + "\n屏幕密度DPI: " + densityDpi;
		LogUtils.i("screenInfo", "screenInfo:" + info);
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
	private static final String REG_PHONE = "^((13[0-9])|(14[579])|(15[0-3,5-9])|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8}$";

	/**
	 * 验证手机格式
	 */
	public static boolean isMobile(String number) {
		if (TextUtils.isEmpty(number)) {
			return false;
		} else {
			//matches():字符串是否在给定的正则表达式匹配
			return number.matches(REG_PHONE);
		}
	}

}
