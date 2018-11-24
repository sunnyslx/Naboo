package com.idx.naboo.weather;


import com.idx.naboo.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 天气信息处理工具类
 */

public class HandlerWeatherUtil {

    public static int getWeatherBackground(int code){
        if (code == 100 || code==900) {return R.mipmap.weather_qing;}
        if ((code >= 101 && code<=103)) {return R.mipmap.weather_duoyun;}
        if (code==104){return R.mipmap.weather_yin;}
        if (code>=200 && code<=204){return  R.mipmap.weather_weifeng;}
        if (code>=205 && code<=212){return R.mipmap.weather_long_juan_feng;}
        if (code >= 300 && code <= 303) {return R.mipmap.weather_zhenyu;}
        if (code==304){return R.mipmap.weather_bingbao;}
        if (code==305 ||code==306){return R.mipmap.weather_xiaoyu; }
        if (code>=307 && code<=312){return R.mipmap.weather_dayu;}
        if (code==313){return R.mipmap.weather_dongyu;}
        if (code>=400 && code<=403){return R.mipmap.weather_xiaoxue;}
        if (code>=404 && code<=407){return R.mipmap.weather_yujiaxue;}
        if (code==500 || code==501){return R.mipmap.weather_wu;}
        if ( code==502){return R.mipmap.weather_wumai;}
        if (code==503){return R.mipmap.weather_yangsha;}
        if (code==504){return R.mipmap.weather_fuchen;}
        return R.drawable.weather_background;
    }
    /**
     * 天气代码 100，900 为晴 101-213，503-508，901 为阴 300-313为雨  400-407 为雪  500-502 为雾霾
     *
     * @param code 天气代码
     * @return 天气图标
     */
    public static int getWeatherImageResource(int code){
        if (code == 100 || code==900) {return R.mipmap.weather_sunshine;}
        if ((code >= 101 && code<=103) ) {return R.mipmap.weather_cloudy;}
        if (code==104){return R.mipmap.weather_cloudyday;}
        if (code>=200 && code<=204){return  R.mipmap.weather_windy;}
        if (code>=205 && code<=212){return R.mipmap.weather_tornado;}
        if (code >= 300 && code <= 303) {return R.mipmap.weather_thumder_showers;}
        if (code==304){return R.mipmap.weather_hail;}
        if (code==305 ||code==306){return R.mipmap.weather_rain; }
        if (code>=307 && code<=312){return R.mipmap.weather_rain_heavy;}
        if (code==313){return R.mipmap.weather_freezing;}
        if (code>=400 && code<=403){return R.mipmap.weather_sonw;}
        if (code>=404 && code<=407){return R.mipmap.weather_sleet;}
        if (code==500 || code==501){return R.mipmap.weather_fog;}
        if ( code==502){return R.mipmap.weather_haze;}
        if (code==503){return R.mipmap.weather_sand;}
        if (code==504){return R.mipmap.weather_dust;}
        return R.drawable.weather_unknown;
    }
    public static int getWeatherSmallIcon(int code){
        if (code == 100 || code==900) {return R.mipmap.weather_sunshine_small;}
        if ( code >= 101 && code<=103 ) {return R.mipmap.weather_cloudy;}
        if (code==104){return R.mipmap.weather_cloudyday;}
        if (code>=200 && code<=204){return  R.mipmap.weather_windy;}
        if (code>=205 && code<=212){return R.mipmap.weather_tornado;}
        if (code >= 300 && code <= 303) {return R.mipmap.weather_thumder_showers;}
        if (code==304){return R.mipmap.weather_hail;}
        if (code==305 ||code==306){return R.mipmap.weather_rain; }
        if (code>=307 && code<=312){return R.mipmap.weather_rain_heavy;}
        if (code==313){return R.mipmap.weather_freezing;}
        if (code>=400 && code<=403){return R.mipmap.weather_sonw;}
        if (code>=404 && code<=407){return R.mipmap.weather_sleet;}
        if (code==500 || code==501){return R.mipmap.weather_fog;}
        if ( code==502){return R.mipmap.weather_haze;}
        if (code==503){return R.mipmap.weather_sand;}
        if (code==504){return R.mipmap.weather_dust;}
        return R.drawable.weather_unknown;
    }
    /**
     * 解析日期，返回指定格式
     *
     * @param date 日期××××-××-××
     * @return pm 01:30
     */
    public static String parseTime(Date date){
        String result = new SimpleDateFormat("aa hh:mm").format(date);
        return result;
    }

    /**
     * 解析日期，返回指定格式
     *
     * @param date 日期××××-××-××
     * @return 周一 01 22
     */
    public static String parseDate(Date date){
        String result = new SimpleDateFormat("EE MM dd").format(date);
        return result;
    }

    /**
     * 解析日期竖屏，返回指定格式
     *
     * @param date 日期××××-××-××
     * @return 周一/Mon
     */
    public static String parseDateWeek(String date){
        String result="";
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat format2 = new SimpleDateFormat("EE");
        try {
            Date date1=format1.parse(date);
            result=format2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析日期横屏，返回指定格式
     *
     * @param date 日期××××-××-××
     * @return ××(月) ××(日)
     */
    public static String parseDateLand(String date){
        String result="";
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat format2 = new SimpleDateFormat("MM dd");
        try {
            Date date1=format1.parse(date);
            result=format2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
