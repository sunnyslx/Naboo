package com.idx.naboo.map.overlay;

/**
 * Created by hayden on 18-1-17.
 */

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.idx.naboo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Poi图层类。在高德地图API里，如果要显示Poi，可以用此类来创建Poi图层。如不满足需求，也可以自己创建自定义的Poi图层。
 */
public class PoiOverlay {
    private List<PoiItem> mPois;
    private AMap mAMap;
    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
    //Marker点集合
    private int[] defaultMarkers = {R.drawable.blue_1,
            R.drawable.blue_2,
            R.drawable.blue_3,
            R.drawable.blue_4,
            R.drawable.blue_5,
            R.drawable.blue_6,
            R.drawable.blue_7,
            R.drawable.blue_8,
            R.drawable.blue_9,
            R.drawable.blue_10,
            R.drawable.blue_11,
            R.drawable.blue_12,
            R.drawable.blue_13,
            R.drawable.blue_14,
            R.drawable.blue_15,
            R.drawable.blue_16,
            R.drawable.blue_17,
            R.drawable.blue_18,
            R.drawable.blue_19,
            R.drawable.blue_20
    };
    private int[] clickMarkers = {R.drawable.red_1,
            R.drawable.red_2,
            R.drawable.red_3,
            R.drawable.red_4,
            R.drawable.red_5,
            R.drawable.red_6,
            R.drawable.red_7,
            R.drawable.red_8,
            R.drawable.red_9,
            R.drawable.red_10,
            R.drawable.red_11,
            R.drawable.red_12,
            R.drawable.red_13,
            R.drawable.red_14,
            R.drawable.red_15,
            R.drawable.red_16,
            R.drawable.red_17,
            R.drawable.red_18,
            R.drawable.red_19,
            R.drawable.red_20
    };

    /**
     * 通过此构造函数创建Poi图层。
     *
     * @param amap 地图对象。
     * @param pois 要在地图上添加的poi。列表中的poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类<strong> <a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
     * @since V2.1.0
     */
    public PoiOverlay(AMap amap, List<PoiItem> pois) {
        mAMap = amap;
        mPois = pois;
    }

    /**
     * 添加Marker到地图中。
     */
    public void addToMap() {
        try {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mAMap.addMarker(getMarkerOptions(i));
                marker.setObject(i);
                if(i==0) {
                    marker.showInfoWindow();
                }
                mPoiMarks.add(marker);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 去掉PoiOverlay上所有的Marker。
     */
    public void removeFromMap() {
        for (Marker mark : mPoiMarks) {
            mark.remove();
        }
    }

    /**
     * 隐藏地图除指定点外的所有点
     */
    public void hideMarker(int position){
        for(int i=0;i<mPoiMarks.size();i++){
            if(i==position){
                mPoiMarks.get(i).setVisible(true);
                mPoiMarks.get(i).showInfoWindow();
            }else{
                mPoiMarks.get(i).setVisible(false);
                mPoiMarks.get(i).hideInfoWindow();
            }
        }
    }

    /**
     * 移动镜头到当前的视角。
     */
    public void zoomToSpan() {
        try {
            if (mPois != null && mPois.size() > 0) {
                if (mPois.size() == 1) {
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mPois.get(0).getLatLonPoint().getLatitude(),
                            mPois.get(0).getLatLonPoint().getLongitude()), 18f));
                } else {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
                    for(int i=0;i<mPoiMarks.size();i++){
                        boundsBuilder.include(mPoiMarks.get(i).getPosition());//把所有点都include进去（LatLng类型）
                    }
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),200));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < mPois.size(); i++) {
            b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                    mPois.get(i).getLatLonPoint().getLongitude()));
        }
        return b.build();
    }

    private MarkerOptions getMarkerOptions(int index) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(
                        new LatLng(mPois.get(index).getLatLonPoint()
                                .getLatitude(), mPois.get(index)
                                .getLatLonPoint().getLongitude()))
                .title(getTitle(index))
                .snippet(getSnippet(index));
        if(index!=0){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(defaultMarkers[index]));
        }else{
            //是第一个就设置为红色
            markerOptions.icon(BitmapDescriptorFactory.fromResource(clickMarkers[0]));
        }
        return markerOptions;
    }

    /**
     * 给第几个Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @param marker 要更改的对象
     */
    public void setMarkerIcon(Marker marker,boolean isChecked){
        for (int i = 0; i < mPoiMarks.size(); i++) {
            if (mPoiMarks.get(i).equals(marker)) {
                //给对应位置Marker设置颜色
                if(isChecked) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(clickMarkers[i]));
                }else{
                    marker.setIcon(BitmapDescriptorFactory.fromResource(defaultMarkers[i]));
                }
            }
        }
    }

    /**
     * 返回第index的Marker的标题。
     *
     * @param index 第几个Marker。
     * @return marker的标题。
     */
    protected String getTitle(int index) {
        return mPois.get(index).getTitle();
    }

    /**
     * 返回第index的Marker的详情。
     *
     * @param index 第几个Marker。
     * @return marker的详情。
     */
    protected String getSnippet(int index) {
        return mPois.get(index).getSnippet();
    }

    /**
     * 从marker中得到poi在list的位置。
     *
     * @param marker 一个标记的对象。
     * @return 返回该marker对应的poi在list的位置。
     * @since V2.1.0
     */
//    public int getPoiIndex(Marker marker) {
//        for (int i = 0; i < mPoiMarks.size(); i++) {
//            if (mPoiMarks.get(i).equals(marker)) {
//                return i;
//            }
//        }
//        return -1;
//    }

    /**
     * 返回第index的poi的信息。
     *
     * @param index 第几个poi。
     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
     */
    public Marker getMarkerItem(int index) {
        if (index < 0 || index >= mPois.size()) {
            return null;
        }
        return mPoiMarks.get(index);
    }
}
