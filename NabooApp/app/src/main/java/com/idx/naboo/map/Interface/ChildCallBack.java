package com.idx.naboo.map.Interface;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;

import java.util.List;

/**
 * Created by hayden on 18-4-17.
 */

public interface ChildCallBack {
     void callFromMap(List<PoiItem> poiItems, LatLng currentLocation, String key, boolean isPoi);
     void callFromLeft(boolean isBack, int position);
     void callFromLeftAdapter(int position);
     void callFromMapAdapter(String json, LatLng currentLatlng);
     void callFromDetail(boolean isBack, NaviBean naviBean, int position);
     void callFromRouteDetail(boolean isBack, int index);
     void callFromRouteStyle(String style);
     void callFromMapRoute(String startName, String endName, List<RouteMessageBean> beanList);
     void callFromMapBusRoute(String startName, String endName, BusRouteResult busRouteResult);
     void callFromBus();
     void callFromMapToOpenBusDetail(BusPath busPath, BusRouteResult result, String startName, String endName);
     void callMainCloseLeft();
     void callFromRouteClick(String send,NaviBean naviBean, int i, String detailStyle, String s);
}
