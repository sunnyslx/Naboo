package com.idx.naboo.map;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.google.gson.Gson;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.map.Interface.BusRouteListener;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.Interface.ParentCallBack;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;
import com.idx.naboo.map.fragment.BusDetailFragment;
import com.idx.naboo.map.fragment.DetailFragment;
import com.idx.naboo.map.fragment.LeftFragment;
import com.idx.naboo.map.fragment.RouteFragment;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.listener.DataListener;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hayden on 18-4-17.
 */


public class MapActivity extends BaseActivity implements ChildCallBack, DataListener, BusRouteListener {

    private static final String TAG = "MapActivity";
    private FrameLayout contains;
    private IService mIService;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment fragment;
    private Boolean flag;
    private Gson gson;
    private Bundle bundle;
    private ParentCallBack parentCallBack;
    private Intent intent;
    private String json;
    private TTSManager ttsManager;
    private LatLng currentLatlng;
    private boolean isFirstToLeft;
    private boolean isFirstToDetail;
    private boolean isFirstToRoute;
    private boolean isFirstToBusDetail;
    private boolean isCmdBack;
    private Fragment mContent;
    private Map<String, Fragment> fragmentMap;
    private String isTopFragment = "";
    private LinkedHashMap<String, Fragment> linkedHashMap;
    private StringBuffer sb;
    private String query_json;
    //保存mQueryId
    private String mQueryId;
    //保存mPageId
    private String mPageId;
    //保存list的queryId
    private String mListQueryId;
    //保存list的PageId;
    private String mListPageId;
    //保存item的queryId
    private String itemQueryId;
    //保存item的PageId
    private String itemPageId;
    //是否是list的返回
    private boolean isBackToList;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(MapActivity.this);
            //获取json
            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())){
                json = mIService.getJson();
                //进入场景
                changeIntoArea(json,false,false);
                //置空Json
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
                //解析Json
                checkJson();
            }else{
                //场景设置
                VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void changeIntoArea(String json,boolean isList,boolean isItem) {
        //场景设置
        JsonData jsonData = JsonUtil.createJsonData(json);
        if(jsonData!=null) {
            mQueryId = jsonData.getQueryId();
            if(isList){
                mListQueryId = mQueryId;
                isBackToList = false;
                Log.i(TAG, "changeIntoArea: 保存list场景："+mListQueryId);
            }else if(isItem){
                itemQueryId = mQueryId;
                isBackToList = true;
                Log.i(TAG, "changeIntoArea: 保存item场景："+itemQueryId);
            }
            String domain = jsonData.getDomain();
            String intention = jsonData.getIntention();
            String type = jsonData.getType();
            mPageId = domain + "_" + intention + "_" + type + "_" + "mapactivity";
            if(isList){
                mListPageId = mPageId;
                Log.i(TAG, "changeIntoArea: 保存list页："+mListPageId);
            }else if(isItem){
                itemPageId = mPageId;
                Log.i(TAG, "changeIntoArea: 保存item页："+itemPageId);
            }
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
        }
    }

    @Override
    public void onJsonReceived(String jsonBack) {
        json = jsonBack;
        //清除关于map的视图模块
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        if(fragment instanceof ParentCallBack){
            parentCallBack  = (ParentCallBack)fragment;
            parentCallBack.callMapCleanView();
        }
        checkJson();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        contains = findViewById(R.id.contains);
        manager = getSupportFragmentManager();
        flag = true;
        gson = new Gson();
        isFirstToLeft = true;
        isFirstToDetail = true;
        isFirstToRoute = true;
        isFirstToBusDetail = true;
        isBackToList = false;
        isCmdBack = false;
        ttsManager = TTSManager.getInstance(this);
        fragmentMap = new HashMap<>();
        linkedHashMap = new LinkedHashMap<>();
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        linkedHashMap.put("map_fragment" + UUID.randomUUID(), fragment);
        sb = new StringBuffer();
        isTopFragment = "MapFragment";
    }

    /**
     * 通过MapFragment传递过来，用于更新LeftFragment中ListView的视图数据
     *
     * @param poiItems        poi结果集
     * @param currentLocation 当前所处位置
     * @param key             关键字
     * @param isPoi           是否是周边Poi搜索
     */
    @Override
    public void callFromMap(List<PoiItem> poiItems, LatLng currentLocation, String key, boolean isPoi) {
        //动态替换并显示
        if (isFirstToLeft) {
            if (currentLocation == null) {
                currentLocation = new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
            }
            createLeftFragment(poiItems, currentLocation, key, isPoi);
            isFirstToLeft = false;
            if (contains.getVisibility() == View.GONE) {
                contains.setVisibility(View.VISIBLE);
            }
        } else {
            //隐藏其他
            hideAndShow(fragmentMap.get("left_fragment"), false);
            //加个判断
            if (contains.getVisibility() == View.GONE) {
                contains.setVisibility(View.VISIBLE);
            }
            callToLeftPoi(poiItems, key, isPoi);
        }
    }

    /**
     * 通过LeftFragment传递过来，用于通知MapFragment显示搜索的回调
     */
    @Override
    public void callFromLeft(boolean isBack, int position) {
        if (isBack && position == -1) {
            //隐藏leftFragment
            if (contains.getVisibility() == View.VISIBLE) {
                contains.setVisibility(View.GONE);
            }
            //回调至Map
            callToMap(false, "", -1);
        } else {
            callToMap(false, "", position);
        }
    }

    /**
     * 通过LeftFragment传递过来，用于通知更新MapFragment
     *
     * @param position 用户点击的位置
     */
    @Override
    public void callFromLeftAdapter(int position) {
        final int pos = position;
        //在这里抽出一个方法用来给moran发送数据得到json
        mIService.requestData("第" + String.valueOf(position + 1) + "个", new NLIRequest.onRequest() {
            @Override
            public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                //定位置Marker
                callToMap(true, s, pos);
            }

            @Override
            public void onError() {
            }
        });
    }

    /**
     * 此方法用来回调至MapFragment
     */
    private void callToMap(boolean isVoice, String json, int position) {
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callMapMarkPoint(isVoice, json, position);
        }
    }

    /**
     * 通过MapFragment传递过来，用于开启DetailFragment
     *
     * @param json              相关解析对象
     * @param currentLatlngBack 当前地址
     */
    @Override
    public void callFromMapAdapter(String json, LatLng currentLatlngBack) {
        //判断左边是否显示
        if (contains.getVisibility() == View.GONE) {
            contains.setVisibility(View.VISIBLE);
        }
        //动态替换并显示
        if (isFirstToDetail) {
            createDetailFragment(json, currentLatlngBack);
            isFirstToDetail = false;
        } else {
            //隐藏其他
            hideAndShow(fragmentMap.get("detail_fragment"), false);
            //进行回调
            callToDetailFragment(json);
        }
    }

    /**
     * 子DetailFragment用来通知主Activity进行fragment变更的
     */
    @Override
    public void callFromDetail(boolean isBack, NaviBean naviBean, int position) {
        if (isBack) {
            backToFront();
        } else {
            //如果是left界面点路线，得发数据
            if(isTopFragment.equals("LeftFragment")){
                //在这里抽出一个方法用来给moran发送数据得到json
                mIService.requestData("第" + String.valueOf(position + 1) + "个", new NLIRequest.onRequest() {
                    @Override
                    public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    }
                    @Override
                    public void onError() {
                    }
                });
            }
            fragment = getSupportFragmentManager().findFragmentById(R.id.map);
            if (fragment instanceof ParentCallBack) {
                parentCallBack = (ParentCallBack) fragment;
                parentCallBack.callMapDrawRoute(naviBean, position, "detailStyle", "");
            }
        }
    }

    /**
     * 此方法用来回调至LeftFragment
     *
     * @param poiItems 点坐标
     * @param key      关键字
     */
    private void callToLeftPoi(List<PoiItem> poiItems, String key, boolean isPoi) {
        fragment = fragmentMap.get("left_fragment");
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callLeftChangeList(poiItems, key, isPoi);
        }
    }

    /**
     * 此方法用来回调至DetailFragment
     *
     * @param json Json细节回复
     */
    private void callToDetailFragment(String json) {
        //拿Detail的Fragment
        fragment = fragmentMap.get("detail_fragment");
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callDetailChangeList(json);
        }
    }

    /**
     * 用来接收来自MapFragment关于路径发过来的信息
     *
     * @param startName 出发点名称
     * @param endName   终点名称
     * @param beanList  路径信息
     */
    @Override
    public void callFromMapRoute(String startName, String endName, List<RouteMessageBean> beanList) {
        //开启新的路径Fragment
        //动态替换并显示
        if (isFirstToRoute) {
            BusRouteResult busRouteResult = null;
            LatLng latLng = new LatLng(mAMapLocation.getLatitude(),mAMapLocation.getLongitude());
            createRouteFragment(startName, endName, beanList, busRouteResult,mCityNameDefault,latLng);
            isFirstToRoute = false;
        } else {
            //隐藏其他
            hideAndShow(fragmentMap.get("route_fragment"), false);
            //回调
            callToRouteFragment(startName, endName, beanList);
        }
    }

    /**
     * 从MapFragment发送到Route更新公交路线
     *
     * @param busRouteResult 公交数据
     */
    @Override
    public void callFromMapBusRoute(String start, String end, BusRouteResult busRouteResult) {
        fragment = fragmentMap.get("route_fragment");
        if (fragment != null) {
            if (fragment instanceof ParentCallBack) {
                parentCallBack = (ParentCallBack) fragment;
                //隐藏其他
                hideAndShow(fragmentMap.get("route_fragment"), false);
                parentCallBack.callBusRouteChange(start,end,busRouteResult);
            }
        } else {
            List<RouteMessageBean> beanList = null;
            if (isFirstToRoute) {
                LatLng latLng = new LatLng(mAMapLocation.getLatitude(),mAMapLocation.getLongitude());
                createRouteFragment(start, end, beanList, busRouteResult,mCityNameDefault,latLng);
                isFirstToRoute = false;
            }
        }
    }

    /**
     * 处理公交细节的返回
     */
    @Override
    public void callFromBus() {
        //显示公交的list
        backToFront();
    }

    /**
     * 将由路径中客户对路径选择的操作转发到MapFragment去更新UI
     *
     * @param isBack 判断是否是返回
     * @param index  路径索引
     */
    @Override
    public void callFromRouteDetail(boolean isBack, int index) {
        if (isBack) {
            //隐藏其他
            backToFront();
        } else {
            callMapChooseRoute(index);
        }
    }

    /**
     * 去地图更新UI
     *
     * @param index 选择的序号
     */
    void callMapChooseRoute(int index) {
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callMapChooseRouteNum(index);
        }
    }

    /**
     * 此方法用来回调至MapFragment进行出行模式的选择
     *
     * @param style 模式
     */
    @Override
    public void callFromRouteStyle(String style) {
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callMapChooseRouteStyle(style);
        }
    }

    /**
     * 此方法回调至RouteFragment
     *
     * @param endName  终点名称
     * @param beanList 路径的信息
     */
    private void callToRouteFragment(String startName, String endName, List<RouteMessageBean> beanList) {
        //拿Route的fragment
        fragment = fragmentMap.get("route_fragment");
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callRouteChangeList(startName, endName, beanList);
        }
    }

    /**
     * 创建新的LeftFragment
     *
     * @param poiItems        poi坐标点
     * @param currentLocation 当前位置信息
     * @param key             搜索关键字
     */
    private void createLeftFragment(List<PoiItem> poiItems, LatLng currentLocation, String key, boolean isPoi) {
        //隐藏前一个
        hideFrontFragment(isTopFragment);
        //开启新的fragment
        fragment = LeftFragment.newInstance(gson.toJson(poiItems), gson.toJson(currentLocation), key, isPoi);
        fragmentMap.put("left_fragment", fragment);
        //添加进去新的fragment
        transaction = manager.beginTransaction();
        transaction.add(R.id.contains, fragment);
        transaction.commit();
        //赋值为当前fragment
        isTopFragment = fragment.getClass().getSimpleName();
    }

    /**
     * 创建新的DetailFragment
     *
     * @param json              解析的数据对象
     * @param currentLatlngBack 当前坐标
     */
    private void createDetailFragment(String json, LatLng currentLatlngBack) {
        //隐藏前一个
        hideFrontFragment(isTopFragment);
        //开启新的fragment
        fragment = DetailFragment.newInstance(json, gson.toJson(currentLatlngBack));
        fragmentMap.put("detail_fragment", fragment);
        //添加进去新的fragment
        transaction = manager.beginTransaction();
        transaction.add(R.id.contains, fragment);
        transaction.commit();
        //赋值为当前fragment
        isTopFragment = fragment.getClass().getSimpleName();
    }

    /**
     * 创建新的RouteFragment
     */
    private void createRouteFragment(String startName, String endName, List<RouteMessageBean> beanList, BusRouteResult busRouteResult,String name,LatLng latLng) {
        //隐藏前一个
        hideFrontFragment(isTopFragment);
        //开启新的fragment
        fragment = RouteFragment.newInstance(startName, endName, gson.toJson(beanList), busRouteResult, name, gson.toJson(latLng));
        fragmentMap.put("route_fragment", fragment);
        //添加进去新的fragment
        transaction = manager.beginTransaction();
        transaction.add(R.id.contains, fragment);
        transaction.commit();
        //赋值为当前fragment
        isTopFragment = fragment.getClass().getSimpleName();
    }

    /**
     * 创建新的BusDetailFragment
     */
    private void createBusDetailFragment(BusPath busPath, BusRouteResult result, String startName, String endName) {
        //隐藏前一个
        hideFrontFragment(isTopFragment);
        //开启新的fragment
        fragment = BusDetailFragment.newInstance(busPath, result,startName, endName);
        fragmentMap.put("bus_fragment", fragment);
        //添加进去新的fragment
        transaction = manager.beginTransaction();
        transaction.add(R.id.contains, fragment);
        transaction.commit();
        //赋值为当前fragment
        isTopFragment = fragment.getClass().getSimpleName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTSManager.getInstance(getBaseContext()).stop();
        unbindService(conn);
    }

    protected void onStop() {
        super.onStop();
    }

    //用来处理传递过来的Json
    private void checkJson() {
        //正常解析
        if (json.length() > 2) {
            JsonData jsonData = JsonUtil.createJsonData(json);
            if (jsonData != null) {
                //如果是Detail
                if (jsonData.getIntention().equals("detailing")) {
                    //设置场景
                    changeIntoArea(json,false,true);
                    try {
                        JSONObject sem = jsonData.getContent().getJSONObject("semantic");
                        int index = sem.getInt("index_number");
                        if (index > 0) {
                            callToMap(true, json, index - 1);
                        }
                    } catch (JSONException e) {
                        //处理直接精确搜索
                        callToMap(true, json, 404);
                        e.printStackTrace();
                    }
                }
                //处理返回的情况
                else if (jsonData.getDomain().equals("cmd") && jsonData.getType().equals("back")) {
                    if(isBackToList){
                        //处理返回到List
                        VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mListPageId, mListQueryId);
                    }else{
                        //覆盖为item
                        mQueryId = itemQueryId;
                        Log.i(TAG, "checkJson: mQueryId="+mQueryId);
                        mPageId = itemPageId;
                        Log.i(TAG, "checkJson: mPageId="+mPageId);
                        VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
                    }

                    if (linkedHashMap.size() == 1) {
                        if(contains.getVisibility()==View.GONE||"map".equals(isTopFragment)){
                            this.finish();
                        }else {
                            //隐藏视图
                            contains.setVisibility(View.GONE);
                            isTopFragment = "map";
                            //回调到主页去显示一下搜索框
                            fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                            if(fragment instanceof ParentCallBack){
                                parentCallBack.callMapShowSearch();
                            }
                            isFirstToLeft = true;
                        }
                    } else {
                        if(contains.getVisibility()==View.GONE){
                            this.finish();
                        }else {
                            backToFront();
                        }
                    }
                }
                //处理导航
                else if (jsonData.getIntention().equals("navigating") || jsonData.getType().equals("navigation")) {
                    //先判断下有没有结果
                    if(jsonData.getTts().equals("抱歉,没有计算结果")){
                        ttsManager.speak(jsonData.getTts(),true);
                    }else {
                        //回调给地图去导航
                        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                        if (fragment instanceof ParentCallBack) {
                            parentCallBack = (ParentCallBack) fragment;
                            //回调去导航
                            parentCallBack.callMapToNavi();
                        }
                    }
                }
                //处理路径
                else if (jsonData.getType().equals("route")) {
                    changeIntoArea(json,false,true);
                    try {
                        //判断有没有返回结果
                        JSONArray listArray = jsonData.getContent().getJSONObject("reply").getJSONArray("route");
                        if (listArray.length() > 0) {//有结果
                            //显示视图
                            if (contains.getVisibility() == View.GONE) {
                                contains.setVisibility(View.VISIBLE);
                            }
                            //封装数据
                            NaviBean naviBean = dealWithRoute(jsonData);
                            if (naviBean != null) {
                                fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                                //绘制
                                if (fragment instanceof ParentCallBack) {
                                    parentCallBack = (ParentCallBack) fragment;
                                    int index = -1;
                                    String way = "";
                                    try {
                                        index = jsonData.getContent().getJSONObject("semantic").getInt("index_number") - 1;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        JSONArray array = jsonData.getContent().getJSONObject("semantic").getJSONArray("TRANSPORT");
                                        way = array.getString(0);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    parentCallBack.callMapDrawRoute(naviBean, index, way, jsonData.getTts());
                                }
                            }
                        } else {//没有结果
                            TTSManager.getInstance(getApplicationContext()).speak(jsonData.getTts(),true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //如果是我在那
                else if (jsonData.getIntention().equals("locating")) {
                    //场景置入
                    changeIntoArea(json,true,false);
                    callToMap(true, json, -1);
                }
                //处理cmd的命令
                else if (jsonData.getIntention().equals("instructing") && jsonData.getDomain().equals("cmd")) {
                    callToMap(true, json, -1);
                }
                //处理评价怎么样
                else if (jsonData.getIntention().equals("searching") && jsonData.getType().equals("default")) {
                    TTSManager.getInstance(getApplicationContext()).speak(jsonData.getTts(),true);
                }
                //如果是Left
                else {
                    if (contains.getVisibility() == View.GONE) {
                        contains.setVisibility(View.VISIBLE);
                    }
                    //设置场景
                    changeIntoArea(json,true,false);
                    callToMap(true, json, -1);
                }
            }
        }
        //处理路径选择的特殊情况
        else {
            //在Map里回调进行路径绘制
            if ("1".equals(json)) {
                callMapChooseRoute(0);
            } else if ("2".equals(json)) {
                callMapChooseRoute(1);
            } else if ("3".equals(json)) {
                callMapChooseRoute(2);
            }
            //在route里回调进行style选择
            fragment = fragmentMap.get("route_fragment");
            if (fragment instanceof ParentCallBack) {
                parentCallBack = (ParentCallBack) fragment;
                if ("1".equals(json)) {
                    parentCallBack.callRouteChangeTextColor(1);
                } else if ("2".equals(json)) {
                    parentCallBack.callRouteChangeTextColor(2);
                } else if ("3".equals(json)) {
                    parentCallBack.callRouteChangeTextColor(3);
                }
            }
        }
    }

    /**
     * 此方法用来返回上一层
     */

    private void backToFront() {
        Iterator<Map.Entry<String, Fragment>> iterator = linkedHashMap.entrySet().iterator();
        Map.Entry<String, Fragment> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
            sb.append(tail.getKey() + "---");
            Log.i(TAG, "有的界面："+sb.toString());
        }
        //获取对应id
        if (tail != null) {
            String key = tail.getKey();
            fragment = tail.getValue();
            //移除节点值
            if(!key.contains("map_fragment")) {
                linkedHashMap.remove(key);
            }
            //隐藏与显示
            hideAndShow(fragment, true);
        }
    }

    /**
     * 隐藏与显示
     *
     * @param frag 要显示的对象
     */
    private void hideAndShow(Fragment frag, boolean isBack) {
        //隐藏当前
        if (isBack) {
            isCmdBack = true;
            hideFrontFragment(isTopFragment);
            isCmdBack = false;
        } else {
            if(!isTopFragment.equals(frag.getClass().getSimpleName())){
                hideFrontFragment(isTopFragment);
            }
        }
        //显示现在
        if (frag.getClass().getSimpleName().equals("MapFragment")) {
            //隐藏视图
            if (contains.getVisibility() == View.VISIBLE) {
                contains.setVisibility(View.GONE);
                //显示界面
                fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if(fragment instanceof ParentCallBack){
                    parentCallBack = (ParentCallBack)fragment;
                    parentCallBack.callMapShowSearch();
                }
            }
        }
        transaction = manager.beginTransaction();
        transaction.show(frag);
        isTopFragment = frag.getClass().getSimpleName();
        //返回到List则需要重新设置设置当前场景
        if(isTopFragment.equals("LeftFragment")){
            //处理返回到List
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mListPageId, mListQueryId);
        }
        transaction.commit();
    }

    /**
     * 处理路径的数据封装
     *
     * @param jsonData 返回数据
     * @return 终点对象
     */
    private NaviBean dealWithRoute(JsonData jsonData) {
        try {
            JSONObject end = jsonData.getContent().getJSONObject("reply").getJSONArray("route").getJSONObject(0);
            //获取三个点坐标
            NaviLatLng startLatLng = new NaviLatLng(end.getDouble("start_latitude"), end.getDouble("start_longitude"));
            NaviLatLng wayLatLng = null;
            if (!"".equals(end.getString("way_spot"))) {
                wayLatLng = new NaviLatLng(end.getDouble("way_latitude"), end.getDouble("way_longitude"));
            }
            NaviLatLng endLatLng = new NaviLatLng(end.getDouble("end_latitude"), end.getDouble("end_longitude"));
            //三个名字
            String startName = end.getString("start_spot");
            String midName = end.getString("way_spot");
            String endName = end.getString("end_spot");
            //构造出对象
            return new NaviBean(startName, midName, endName, startLatLng, wayLatLng, endLatLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理由RouteFragment传递过来的关于Bus的数据
     *
     * @param pos     点击位置
     * @param busPath 点选的公交路径
     * @param result  公交路线返回结果集
     */
    @Override
    public void clickItem(int pos, BusPath busPath, BusRouteResult result, String startName, String endName) {
        //先去通知MapFragment更改相关路径
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callMapToDrawBusRoute(pos, busPath, result,startName, endName);
        }
    }

    /**
     * 开启新的Fragment
     *
     * @param busPath ；路径
     * @param result  结果集
     * @param endName 终点
     */
    @Override

    public void callFromMapToOpenBusDetail(BusPath busPath, BusRouteResult result, String startName, String endName) {
        //开启新的路径Fragment
        //动态替换并显示
        if (isFirstToBusDetail) {
            createBusDetailFragment(busPath, result,startName, endName);
            isFirstToBusDetail = false;
        } else {
            //隐藏其他
            hideAndShow(fragmentMap.get("bus_fragment"), false);
            //回调
            callToBusFragment(busPath, result,startName, endName);
        }
    }

    /**
     * 隐藏左边
     */
    @Override
    public void callMainCloseLeft() {
        contains.setVisibility(View.GONE);
    }

    /**
     * 用来回调绘制
     * @param naviBean
     * @param i
     * @param detailStyle
     * @param s
     */
    @Override
    public void callFromRouteClick(String send,NaviBean naviBean, int i, String detailStyle, String s) {
        //在这里同步一下语境
        //在这里抽出一个方法用来给moran发送数据得到json
        mIService.requestData(send, new NLIRequest.onRequest() {
            @Override
            public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                //设置语境
                //进入场景
                changeIntoArea(s,false,true);
            }

            @Override
            public void onError() {
            }
        });
        fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        if(fragment instanceof ParentCallBack){
            parentCallBack = (ParentCallBack)fragment;
            parentCallBack.callMapDrawRoute(naviBean,i,detailStyle,s);
        }
    }

    /**
     * 回调至已存在的BusDetailFragment
     *
     * @param busPath        选择路径
     * @param busRouteResult 总结果集
     * @param endName        终点名称
     */
    private void callToBusFragment(BusPath busPath, BusRouteResult busRouteResult, String startName, String endName) {
        fragment = fragmentMap.get("bus_fragment");
        if (fragment instanceof ParentCallBack) {
            parentCallBack = (ParentCallBack) fragment;
            parentCallBack.callBusDetailChange(busPath, busRouteResult,startName, endName);
        }
    }

    /**
     * 用来处理隐藏上一个fragment的操作
     *
     * @param name 要隐藏的fragment
     *
     */
    private void hideFrontFragment(String name) {
        transaction = manager.beginTransaction();
        //判断leftFragment
        if (name.equals(LeftFragment.class.getSimpleName())) {
            fragment = fragmentMap.get("left_fragment");
            transaction.hide(fragment);
            if (!isCmdBack) {
                linkedHashMap.put("left_fragment" + UUID.randomUUID(), fragment);
            }
        }
        //判断detailFragment
        if (name.equals(DetailFragment.class.getSimpleName())) {
            fragment = fragmentMap.get("detail_fragment");
            transaction.hide(fragment);
            if (!isCmdBack) {
                linkedHashMap.put("detail_fragment" + UUID.randomUUID(), fragment);
            }
        }
        //判断routeFragment
        if (name.equals(RouteFragment.class.getSimpleName())) {
            fragment = fragmentMap.get("route_fragment");
            transaction.hide(fragment);
            if (!isCmdBack) {
                linkedHashMap.put("route_fragment" + UUID.randomUUID(), fragment);
            }
        }
        //判断busFragment
        if (name.equals(BusDetailFragment.class.getSimpleName())) {
            fragment = fragmentMap.get("bus_fragment");
            transaction.hide(fragment);
            if (!isCmdBack) {
                linkedHashMap.put("bus_fragment" + UUID.randomUUID(), fragment);
            }
        }
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空资源
        if(linkedHashMap!=null){
            linkedHashMap.clear();
        }
        if(fragmentMap!=null){
            fragmentMap.clear();
        }
        if(mIService!=null){
            mIService=null;
        }
        if(manager!=null){
            manager = null;
        }
        if(transaction!=null){
            transaction=null;
        }
        if(fragment!=null){
            fragment = null;
        }
        if(currentLatlng!=null){
            currentLatlng= null;
        }
        if(mContent!=null){
            mContent = null;
        }
        //销毁资源
        this.finish();
    }
}
