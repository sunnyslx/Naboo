package com.idx.naboo.utils;

import com.google.gson.Gson;
import com.idx.naboo.dish.data.ImoranResponseDish;
import com.idx.naboo.figure.data.ImoranResponseFigure;
import com.idx.naboo.home.Time_home;
import com.idx.naboo.music.data.ImoranResponseMusic;
import com.idx.naboo.news.data.ImoranResponseNews;
import com.idx.naboo.news.data.NewsDetail;
import com.idx.naboo.video.cmd.MoranResponseCmd;
import com.idx.naboo.video.data.ImoranResponseVideo;
import com.idx.naboo.weather.data.ImoranResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * 将返回的JSON数据解析成实体类
 * Created by sunny on 18-3-15.
 */

public class ImoranResponseToBeanUtils {
    private static final String TAG = ImoranResponseToBeanUtils.class.getSimpleName();

    //Imoran to music
    public static ImoranResponseMusic handleMusicData(String response) {
        try {
            Gson gson = new Gson();
            ImoranResponseMusic music = gson.fromJson(response, ImoranResponseMusic.class);
            return music;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Imoran music data is null
    public static boolean isImoranMusicNull(ImoranResponseMusic music){
        boolean flag = false;
        if (music!=null && music.getMusicData()!=null && music.getMusicData().getMusicContent()!=null) {
            flag = true;
        }
        return flag;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static ImoranResponse handleWeatherData(String weatherData) {
        try {
            Gson gson = new Gson();
            ImoranResponse weather = gson.fromJson(weatherData, ImoranResponse.class);
//            Log.i(TAG, "handleWeatherResponse: weather = " + weather);
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Time_home handleTimeHome(String time_home){
        try {
            Gson gson = new Gson();
            Time_home time_ = gson.fromJson(time_home, Time_home.class);
//            Log.i(TAG, "handleWeatherResponse: weather = " + weather);
            return time_;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isImoranWeatherNull(ImoranResponse weather){
        boolean flag=false;
        if (weather!=null && weather.getDataWeather()!=null && weather.getDataWeather().getContent()!=null){
            flag=true;
        }
        return flag;
    }
    public static ImoranResponseVideo handleVedioData(String videoData){
        try {
            Gson gson=new Gson();
            ImoranResponseVideo video=gson.fromJson(videoData,ImoranResponseVideo.class);
            return video;
        }catch (Exception e){
              e.printStackTrace();
        }
        return null;
    }
    public static boolean isImoranVideNull(ImoranResponseVideo video){
        boolean flag=false;
        if (video!=null && video.getVideoData()!=null && video.getVideoData().getContent()!=null){
            flag=true;
        }
        return flag;
    }
    public static MoranResponseCmd handCmd(String  json){
        try {
            Gson gson=new Gson();
            MoranResponseCmd cmd=gson.fromJson(json,MoranResponseCmd.class);
            return cmd;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isCmdNull(MoranResponseCmd cmd){
        boolean flag=false;
        if (cmd!=null && cmd.getCmdData()!=null && cmd.getCmdData().getCmdContent()!=null){
            flag=true;
        }
        return flag;
    }
    public static ImoranResponseDish handleDishData(String dishData){
        try {
            Gson gson=new Gson();
            return  gson.fromJson(dishData,ImoranResponseDish.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isImpranDishNull(ImoranResponseDish dish){
        boolean flag=false;
        if (dish!=null && dish.getDishData()!=null && dish.getDishData().getDishContent()!=null){
            flag=true;
        }
        return flag;
    }
    /**
     * 将返回的JSON数据解析成Figure实体类
     */
    //将接收到的json文件解析成Figure实体类
    public static ImoranResponseFigure handleFigureData(String response) {
        try {
            Gson gson = new Gson();
            ImoranResponseFigure figure = gson.fromJson(response, ImoranResponseFigure.class);
            return figure;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //判断数据是否为空
    public static boolean isImoranFigureNull(ImoranResponseFigure figure){
        boolean flag = false;
        if (figure != null && figure.getFigureData().getFigureContent().getFigureReply() != null &&
                figure.getFigureData().getFigureContent().getFigureReply().getFigure() != null ) {
            flag = true;
        }
        return flag;
    }

    /**
     * 将返回的JSON数据解析成News实体类
     */
    //将接收到的json文件解析成news实体类
    public static List<NewsDetail> handleNewsData(String response) {
        List<NewsDetail> list = null;
        try {
            list = new ArrayList<>();
            if (list != null){
                list.clear();
            }
            Gson gson = new Gson();
            ImoranResponseNews news = gson.fromJson(response, ImoranResponseNews.class);
            list = news.getNewsData().getNewsContent().getNewsReply().getNewsDetail();

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //拿到NewsIndex的值
    public static int handleNewsIndex(String response){
        int indexNumber = 0;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            indexNumber = responseNews.getNewsData().getNewsContent().getSemantic().getIndexNumber();
        }catch (Exception e){
            e.printStackTrace();
        }
        return indexNumber;
    }

    //拿到NewsLabel的值
    public static String[] handleLabel(String response){
        String[] list1 = null;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            list1 = responseNews.getNewsData().getNewsContent().getSemantic().getNewsType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list1;
    }

    public static String handleNewsType(String response){
        String type = null;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            type = responseNews.getNewsData().getNewsContent().getType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return type;
    }

    public static String handleDomain(String response){
        String domian = null;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            domian = responseNews.getNewsData().getDomain();
        }catch (Exception e){
            e.printStackTrace();
        }
        return domian;
    }
    //判断数据是否为空
    public static boolean isImoranNewsNull(ImoranResponseNews news){
        boolean flag = false;
        if (news!=null && news.getNewsData()!=null && news.getNewsData().getNewsContent().getNewsReply().getNewsDetail().get(0).getTitle()!=null) {
            flag = true;
        }
        return flag;
    }
}
