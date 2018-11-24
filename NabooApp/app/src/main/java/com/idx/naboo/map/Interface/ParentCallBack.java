package com.idx.naboo.map.Interface;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;

import java.util.List;

/**
 * Created by hayden on 18-4-17.
 */

public interface ParentCallBack {
     void callMapMarkPoint(boolean isVoice, String json, int position);
     void callMapDrawRoute(NaviBean naviBean, int position, String wayStyle, String tts);
     void callLeftChangeList(List<PoiItem> poiItemList, String key, boolean isPoi);
     void callDetailChangeList(String json);
     void callRouteChangeList(String startName, String endName, List<RouteMessageBean> beanList);
     void callMapChooseRouteNum(int index);
     void callMapChooseRouteStyle(String style);
     void callBusRouteChange(String start, String end, BusRouteResult result);
     void callMapToNavi();
     void callRouteChangeTextColor(int index);
     void callBusDetailChange(BusPath busPath, BusRouteResult busRouteResult, String startName, String endName);
     void callMapToDrawBusRoute(int pos, BusPath busPath, BusRouteResult result, String startName, String endName);
     void callMapShowSearch();
     void callMapCleanView();
}
