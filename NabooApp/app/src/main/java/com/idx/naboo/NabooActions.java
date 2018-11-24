package com.idx.naboo;

/**
 * Created by derik on 18-4-14.
 * Email: weilai0314@163.com
 */

public interface NabooActions {
    interface Weather {
        //weather name
        String TARGET_WEATHER = "weather";
    }

    interface Dish {
        String TARGET_DISH = "dish";
    }

    interface Music {
        //music name
        String TARGET_MUSIC = "music";
    }

    interface Video {
        //video name
        String TARGET_VIDEO = "movie";
    }

    interface Map {
        //map name
        String TARGET_MAP = "map";
        //restaurant name
        String TARGET_RESTAURANT = "cate";
        //view name
        String TARGET_VIEWSPOT = "viewspot";
    }

    interface Figure {
        //news name
        String TARGET_FIGURE = "people";
    }

    interface MapRoute {
        //maproute name
        String MAP_ROUTE_NAME = "maproute";
    }

    interface MapNavi {
        //maproute name
        String MAP_NAVI_NAME = "mapnavi";
    }

    interface News {
        //news name
        String TARGET_NEWS = "news";
    }

    interface Cmd {
        //cmd name
        String TARGET_CMD = "cmd";
    }

    interface Home {
        //Home name
        String TARGET_HOME = "home";
    }

    interface Phone {
        //打电话
        String TARGET_PHONE = "phone";
    }

    interface TakeOut {
        String TARGET_TAKEOUT = "takeout";
        String TARGET_TAKEOUT_CAR = "takeoutmenucart";
        String TARGET_TYPE_SHOP = "takeoutshop";
        String TARGET_TYPE_MENU = "takeoutmenu";
    }

    interface Calendar {
        //Calendar
        String TARGET_CALENDAR = "time";
    }

    interface ChitChat {
        String TARGET_CHITCHAT = "chitchat";
    }

    interface Order {
        String TARGET_ORDER_LIST = "order";
    }

    //组合触发指令
    String BACK = "back";
    String BACK_HOME = "back_home";
}
