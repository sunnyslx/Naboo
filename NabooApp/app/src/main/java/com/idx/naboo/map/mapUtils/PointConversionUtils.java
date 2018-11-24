package com.idx.naboo.map.mapUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.idx.naboo.data.Point;
import com.idx.naboo.map.restaurant.Restaurant;
import com.idx.naboo.map.viewspot.ViewSpot;

import java.util.List;

/**
 * Created by hayden on 18-4-19.
 */

public class PointConversionUtils {

    /**
     * 处理Point的类型转换
     * @param poiItems 原list
     * @param pointList 原pointList
     * @return 修改后list
     */
    public static List<PoiItem> pointToPoi(List<PoiItem> poiItems,List<Point> pointList){
        if (poiItems.size() > 0) {
            poiItems.clear();
        }
        PoiItem poiItem = null;
        for (int i = 0; i < pointList.size(); i++) {
            LatLonPoint latLonPoint = new LatLonPoint(pointList.get(i).getLatitude(),pointList.get(i).getLongitude());
            poiItem = new PoiItem("id", latLonPoint, pointList.get(i).getName(), pointList.get(i).getAddress());
            poiItems.add(poiItem);
        }
        return poiItems;
    }

    /**
     * 处理Tip的类型转换
     * @param poiItems 原list
     * @param tipList 原tipList
     * @return 修改后list
     */
    public static List<PoiItem> tipToPoi(List<PoiItem> poiItems,List<Tip> tipList){
        if (poiItems.size() > 0) {
            poiItems.clear();
        }
        for (int i = 0; i < tipList.size(); i++) {
            Tip  tip = tipList.get(i);
            if(tip.getPoint()!=null) {
                PoiItem poiItem = new PoiItem("id", tip.getPoint(), tip.getName(), tip.getAddress());
                poiItems.add(poiItem);
            }
        }
        return poiItems;
    }

    /**
     * 处理Restaurant的类型转换
     * @param poiItems 原list
     * @param restaurants 原restaurant
     * @return 修改后list
     */
    public static List<PoiItem> restaurantToPoi(List<PoiItem> poiItems,List<Restaurant> restaurants){
        if (poiItems.size() > 0) {
            poiItems.clear();
        }
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            LatLonPoint latLonPoint = new LatLonPoint(restaurant.getLatitude(),restaurant.getLongitude());
            PoiItem poiItem = new PoiItem("id", latLonPoint, restaurant.getName(), restaurant.getAddress());
            poiItems.add(poiItem);
        }
        return poiItems;
    }

    /**
     * 处理ViewSpot的类型转换
     * @param poiItems 原list
     * @param viewSpots 原viewSpots
     * @return 修改后list
     */
    public static List<PoiItem> viewSpotToPoi(List<PoiItem> poiItems,List<ViewSpot> viewSpots){
        if (poiItems.size() > 0) {
            poiItems.clear();
        }
        for (int i = 0; i < viewSpots.size(); i++) {
            ViewSpot viewSpot = viewSpots.get(i);
            LatLonPoint latLonPoint = new LatLonPoint(viewSpot.getLatitude(),viewSpot.getLongitude());
            PoiItem poiItem = new PoiItem("id", latLonPoint, viewSpot.getName(), viewSpot.getAddress());
            poiItems.add(poiItem);
        }
        return poiItems;
    }
}
