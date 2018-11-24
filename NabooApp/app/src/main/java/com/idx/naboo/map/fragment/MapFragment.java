package com.idx.naboo.map.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviException;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.google.gson.Gson;
import com.idx.naboo.NabooActions;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.Interface.ParentCallBack;
import com.idx.naboo.map.RouteNaviActivity;
import com.idx.naboo.map.adapter.InputItemsAdapter;
import com.idx.naboo.map.adapter.InputTipsAdapter;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;
import com.idx.naboo.map.bean.StrategyBean;
import com.idx.naboo.map.mapUtils.PointConversionUtils;
import com.idx.naboo.map.mapUtils.SensorEventHelper;
import com.idx.naboo.map.mapUtils.ToastUtil;
import com.idx.naboo.map.mapUtils.Utils;
import com.idx.naboo.map.overlay.BusRouteOverlay;
import com.idx.naboo.map.overlay.PoiOverlay;
import com.idx.naboo.map.restaurant.ImoranRestaurantResponse;
import com.idx.naboo.map.restaurant.Restaurant;
import com.idx.naboo.map.restaurant.RestaurantReply;
import com.idx.naboo.map.viewspot.ImoranViewResponse;
import com.idx.naboo.map.viewspot.ViewReply;
import com.idx.naboo.map.viewspot.ViewSpot;
import com.idx.naboo.utils.NetStatusUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hayden on 18-4-14.
 */

public class MapFragment extends Fragment implements AMapLocationListener, AMap.OnMarkerClickListener, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener, AMap.OnMapTouchListener, View.OnClickListener, ParentCallBack, AMapNaviListener, RouteSearch.OnRouteSearchListener{
    private static final String TAG = "MapFragment";
    //定位蓝点颜色参数
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    //地图视图对象
    private MapView mapView;
    //地图控制对象
    private AMap aMap;
    //地图缩放级别
    private float zoom;
    //地图搜索文本框
    private EditText mSearchText;
    //地图搜索框
    private LinearLayout mSearchLayout;
    //地图搜索结果集
    private LinearLayout mListLayout;
    //传感器对象
    private SensorEventHelper mSensorHelper;
    //地图定位控制对象
    private AMapLocationClient mLocationClient;
    //我的位置对象
    private LatLng currentLocation = null;
    //我的终点对象
    private LatLng startLatlng = null;
    private NaviLatLng startNavi = null;
    private LatLng endLatlng = null;
    private NaviLatLng endNavi = null;
    //是否首次定位标识
    private boolean isFirstLoc = true;
    //所在城市名称
    private String currentCity = "";
    //定位InfoWindow内容
    private String LOCATION_MARKER_FLAG = "mylocation";
    //位置标记marker
    private Marker mSmallIcon;
    //默认缩放级别
    private int defaultSize;
    //默认动画
    private AutoTransition set;
    //搜索关键字
    private String key = "";
    //周边搜索对象
    private PoiSearch.Query query;
    //PoiSearch对象
    private PoiSearch poiSearch;
    //搜索时进度条
    private ProgressDialog progDialog = null;
    //存储周边搜索数据
    private List<PoiItem> poiItems = new ArrayList<>();
    //存储Detail类型的poiItems
    private List<PoiItem> singlePoiItems = new ArrayList<>();
    //poi返回的结果
    private PoiResult poiResult;
    //Tip对象存储
    private List<Tip> mCurrentTipList;
    private InputTipsAdapter mIntipAdapter;
    private InputItemsAdapter mInItemsapter;
    private Gson gson;
    //键盘控制器
    private InputMethodManager immanager;
    //listView的控件
    private ListView mInputListView;
    //删除控件
    private ImageView clear;
    //定义被选中Marker
    private Marker chooseMarker;
    //用来控制点击子项时，不再去后台查找
    private boolean flag;
    //用来判断是poi给的还是关键字给的adapter;
    private boolean isPoiSearch;
    //获取当前的tts对象
    private TTSManager mTTSClient;
    //定义全局的marker管理
    private PoiOverlay poiOverlay;
    //我的定位按钮
    private Button myLoc;
    //用来显示数据的回调接口
    private ChildCallBack childCallBack;
    //防止回车键时再次查询
    private boolean isEnter;
    //用来通知播报的语音
    private boolean isFirstToMap;
    //用来在定位后处理json
    private String json= "";
    //暂存Marker
    private Marker clickMarker;

    //===路径规划相关资源===
    private StrategyBean mStrategyBean;
    private AMapNavi mAMapNavi;
    //驾车算路值初始化
    int strategyFlag = 0;
    //路线起始点、途经点及终点集合对象创建
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    //当前算路存储
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();
    //出行方式
    private String way = "";
    //布局填充
    private LinearLayout mRouteLineLayoutOne, mRouteLinelayoutTwo, mRouteLineLayoutThree;
    private View mRouteViewOne, mRouteViewTwo, mRouteViewThree;
    private TextView mRouteTextStrategyOne, mRouteTextStrategyTwo, mRouteTextStrategyThree;
    private TextView mRouteTextTimeOne, mRouteTextTimeTwo, mRouteTextTimeThree;
    private TextView mRouteTextDistanceOne, mRouteTextDistanceTwo, mRouteTextDistanceThree;
    private TextView mCalculateRouteOverView;
    // 规划线路
    private int routeID = -1;
    private static final float ROUTE_UNSELECTED_TRANSPARENCY = 0.3F;
    private static final float ROUTE_SELECTED_TRANSPARENCY = 1F;
    //存储路径规划数据的List集合
    private List<RouteMessageBean> beanList;
    //此时的目的地名称
    private String endPointName = "";
    //出发点名称
    private String startPointName = "";
    //路径选择返回的数组
    private int[] intArrays = null;
    //公交线路规划
    private RouteSearch mRouteSearch = null;
    private BusRouteResult mBusRouteResult;
    private BusRouteOverlay mBusrouteOverlay;
    //处理路径规划是否初始化
    private boolean isNaviCreate;
    //定义直接搜某个详情的Marker
    private Marker detailMarker;
    //定义存储公交起点
    private NaviLatLng busStartNavi;
    //Bus没结果的标志位
    private boolean busNoResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof ChildCallBack) {
            childCallBack = (ChildCallBack) getActivity();
        }
        //资源初始化
        initData();
        //初始化定位
        initLocation();
        //获取控制器
        immanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        //视图初始化
        initView(view, savedInstanceState);
        //开始定位
        startLocation();
        //设置点标记监听
        aMap.setOnMarkerClickListener(this);
        //设置地图点击事件
        aMap.setOnMapTouchListener(this);
        return view;
    }

    /**
     * 视图控件获取
     */
    private void initView(final View view, Bundle savedInstanceState) {
        //获取mapView
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        //获取listView
        mInputListView = view.findViewById(R.id.inputtip_list);
        //获取删除视图
        clear = view.findViewById(R.id.clean_keywords);
        clear.setOnClickListener(this);
        //设置条目监听
        mInputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭键盘
                closeEdit();
                //有路径就清除
                if(routeOverlays!=null){
                    cleanRouteOverlay();
                }
                if(mBusrouteOverlay!=null){
                    mBusrouteOverlay.removeFromMap();
                }
                //隐藏搜索框
                if (mSearchLayout.getVisibility() == View.VISIBLE) {
                    mSearchLayout.setVisibility(View.GONE);
                }
                PoiItem poiItem = null;
                //设置点坐标位置
                if (isPoiSearch) {
                    //添加点Marker
                    addTipToMarker(poiItems.get(position));
                    //修改poiItems
                    poiItem = poiItems.get(position);
                    //存储到单项中
                    if(singlePoiItems.size()>0){
                        singlePoiItems.clear();
                    }
                    singlePoiItems.add(poiItem);
                } else {
                    //添加点
                    addTipToMarker(mCurrentTipList.get(position));
                    //转换
                    Tip tip = mCurrentTipList.get(position);
                    poiItem = new PoiItem("id", tip.getPoint(), tip.getName(), tip.getAddress());
                    //存储到单项中
                    if(singlePoiItems.size()>0){
                        singlePoiItems.clear();
                    }
                    singlePoiItems.add(poiItem);
                }
                if (poiItem != null) {
                    key = poiItem.getTitle();
                }
                //回调显示另一个fragment
                childCallBack.callFromMap(singlePoiItems, currentLocation, key, true);
            }
        });
        mInputListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //关闭键盘
                closeEdit();
                return false;
            }
        });
        mSearchText = view.findViewById(R.id.tv_search);
        mSearchLayout = view.findViewById(R.id.ll_search);
        mListLayout = view.findViewById(R.id.ll_list);
        //先处于失去焦点视图缩回状态
        reduce(mSearchLayout);
        //设置焦点获取监听
        mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    //展开视图
                    expand(mSearchLayout);
                }
            }
        });
        mSearchText.setOnClickListener(this);
        //设置改变时属性
        final int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        mSearchText.setInputType(inputType);
        //设置内容变化监听
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //进行一次查询
                if (!s.toString().equals("") && !s.toString().trim().equals("")) {
                    //显示删除符
                    if (clear.getVisibility() != View.VISIBLE) {
                        clear.setVisibility(View.VISIBLE);
                    }
                    key = s.toString();
                    //判断接收到的词
                    if (chooseSearchWay(key)) {
                        //查询周边模糊词
                        searchSurrend();
                    } else {
                        InputtipsQuery inputquery = new InputtipsQuery(s.toString(), currentCity);
                        Inputtips inputTips = new Inputtips(getActivity(), inputquery);
                        inputTips.setInputtipsListener(MapFragment.this);
                        inputTips.requestInputtipsAsyn();
                    }
                } else {
                    //隐藏删除符
                    if (clear.getVisibility() == View.VISIBLE) {
                        clear.setVisibility(View.GONE);
                    }
                    //隐藏listView
                    if (mListLayout.getVisibility() == View.VISIBLE) {
                        mListLayout.setVisibility(View.GONE);
                    }
                    if (mIntipAdapter != null && mCurrentTipList != null) {
                        mCurrentTipList.clear();
                        mIntipAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        //设置回车搜索监听
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //非空状态下的搜索
                    if (!mSearchText.getText().toString().trim().equals("")) {
                        isEnter = true;
                        //有路径就清除
                        if(routeOverlays!=null){
                           cleanRouteOverlay();
                        }
                        if(mBusrouteOverlay!=null){
                            mBusrouteOverlay.removeFromMap();
                        }
                        //关闭键盘
                        closeEdit();
                        //隐藏
                        mListLayout.setVisibility(View.GONE);
                        //先判断是不是周边查的
                        if (!isPoiSearch) {
                            poiItems = PointConversionUtils.tipToPoi(poiItems,mCurrentTipList);
                        }
                        //播报
                        mTTSClient.speak("已为您搜索到"+poiItems.size()+"条结果，请查看",true);
                        //开启左边fragment
                        toChangeLeftData(true);
                    } else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.map_location_error), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        //我的定位按钮
        myLoc = view.findViewById(R.id.my_location);
        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //定位到我的位置
                toMyLocation(currentLocation,16);
            }
        });
        //导航初始化
        initNavi();
    }

    /**
     * 导航初始化
     */
    private void initNavi() {
        mStrategyBean = new StrategyBean(false, false, false, false);
        mAMapNavi = AMapNavi.getInstance(getActivity().getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
    }

    /**
     * 地图点击事件
     */
    @Override
    public void onTouch(MotionEvent motionEvent) {
        //失去焦点
        closeEdit();
        //缩回视图
        reduce(mSearchLayout);
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param obj 点坐标数据
     */
    private void addTipToMarker(Object obj) {
        //清空Marker
        clearMarkers();
        chooseMarker = aMap.addMarker(new MarkerOptions());
        LatLonPoint point = null;
        //控制不让再去查
        flag = false;
        if (isPoiSearch) {
            PoiItem poiItem = (PoiItem) obj;
            point = poiItem.getLatLonPoint();
            chooseMarker.setTitle(poiItem.getTitle());
            chooseMarker.setSnippet(poiItem.getSnippet());
        } else {
            Tip tip = (Tip) obj;
            point = tip.getPoint();
            chooseMarker.setTitle(tip.getName());
            chooseMarker.setSnippet(tip.getAddress());
        }
        LatLng markerPosition = null;
        if (point != null) {
            markerPosition = new LatLng(point.getLatitude(), point.getLongitude());
            chooseMarker.setPosition(markerPosition);
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 20));
        //隐藏listView
        if (mListLayout.getVisibility() == View.VISIBLE) {
            mListLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 用多Marker展示周边多地搜索
     **/
    private void addPoiItemsMarker(List<PoiItem> poiItems) {
        poiOverlay = new PoiOverlay(aMap, poiItems);
        clickMarker = null;
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
    }

    /**
     * 控件点击事件
     *
     * @param v 控件对象
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clean_keywords:
                mSearchText.setText("");
                clearMarkers();
                closeEdit();
                reduce(mSearchLayout);
                if (clear.getVisibility() == View.VISIBLE) {
                    clear.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_search:
                //展开搜索框
                expand(mSearchLayout);
                //获取焦点
                openEdit();
                //设置可以返回tip
                flag = true;
                break;
        }
    }

    /**
     * 清除Marker坐标点
     */
    private void clearMarkers() {
        //获取地图上所有Marker
        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
        for (int i = 0; i < mapScreenMarkers.size(); i++) {
            Marker marker = mapScreenMarkers.get(i);
            if (marker.getObject() != null) {
                if (!marker.getObject().toString().equals("location")) {
                    //移除当前Marker
                    marker.remove();
                }
            } else {
                //移除当前Marker
                marker.remove();
            }
        }
        //地图刷新
        aMap.runOnDrawFrame();
    }

    /**
     * 获得焦点并打开键盘
     **/
    private void openEdit() {
        //设置输入框可聚集
        mSearchText.setFocusable(true);
        //设置触摸聚焦
        mSearchText.setFocusableInTouchMode(true);
        //请求焦点
        mSearchText.requestFocus();
        //获取焦点
        mSearchText.findFocus();
        //开启键盘
        immanager.showSoftInput(mSearchText, InputMethodManager.SHOW_FORCED);//显示键盘
    }

    /**
     * 失去焦点并关闭键盘
     **/
    private void closeEdit() {
        //失去焦点
        mSearchText.setFocusable(false);
        //关闭键盘
        immanager.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);//隐藏键盘
    }

    /**
     * 展开动画效果
     */
    private void expand(ViewGroup view) {
        //设置伸展状态时的布局
        mSearchText.setHint("您可以搜索关键字、酒店、餐厅等");
        ViewGroup.LayoutParams LayoutParams = view.getLayoutParams();
        LayoutParams.width = LayoutParams.MATCH_PARENT;
        view.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(view);
    }

    /**
     * 收缩动画效果
     */
    private void reduce(ViewGroup view) {
        //设置收缩状态时的布局
        mSearchText.setHint("搜地点、查路线");
        ViewGroup.LayoutParams LayoutParams = view.getLayoutParams();
        LayoutParams.width = dip2px(400);
        view.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(view);
    }

    void beginDelayedTransition(ViewGroup view) {
        set = new AutoTransition();
        set.setDuration(300);
        TransitionManager.beginDelayedTransition(view, set);
    }

    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 判断通过哪种方式搜索
     **/
    private boolean chooseSearchWay(String s) {
        isPoiSearch = true;
        if (s.equals("酒店") || s.equals("宾馆") || s.equals("旅社")) {
            query = new PoiSearch.Query("", "100105", currentCity);
        } else if (s.equals("美食") || s.equals("小吃街")) {
            query = new PoiSearch.Query("", "050400", currentCity);
        } else if (s.equals("商场") || s.equals("购物广场")) {
            query = new PoiSearch.Query("", "060101", currentCity);
        } else if (s.equals("地铁站")||s.equals("地铁")) {
            query = new PoiSearch.Query("", "150500", currentCity);
        } else if (s.equals("公交站")||s.equals("公交")) {
            query = new PoiSearch.Query("", "150700", currentCity);
        } else if (s.equals("火车站")||s.equals("火车")) {
            query = new PoiSearch.Query("", "150200", currentCity);
        } else if (s.equals("加油站") || s.equals("加气站")) {
            query = new PoiSearch.Query("", "010100", currentCity);
        } else if (s.equals("快餐") || s.equals("酒楼") || s.equals("菜馆") || s.equals("餐厅")) {
            query = new PoiSearch.Query("", "050000", currentCity);
        } else if (s.equals("肯德基")){
            query = new PoiSearch.Query("","050301", currentCity);
        } else if (s.equals("麦当劳")){
            query = new PoiSearch.Query("","050302", currentCity);
        } else if (s.equals("必胜客")){
            query = new PoiSearch.Query("","050303", currentCity);
        } else if (s.equals("医") || s.equals("诊所") || s.equals("急救") || s.equals("保健")) {
            query = new PoiSearch.Query("", "090000", currentCity);
        } else if (s.equals("停车") || s.equals("泊车")) {
            query = new PoiSearch.Query("", "150900", currentCity);
        } else if (s.equals("银行") || s.equals("提款机") || s.equals("取款机")) {
            query = new PoiSearch.Query("", "160100", currentCity);
        } else if (s.equals("厕") || s.equals("茅房")) {
            query = new PoiSearch.Query("", "200300", currentCity);
        } else if (s.equalsIgnoreCase("ktv") || s.contains("ktv")){
            query = new PoiSearch.Query("", "080302", currentCity);
        } else {
            //去查关键字
            isPoiSearch = false;
        }
        return isPoiSearch;
    }

    /**
     * 周边搜索监听
     **/
    private void searchSurrend() {
        query.setPageSize(7);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码
        //构造对象并发送检索
        if (currentLocation != null) {
            poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setOnPoiSearchListener(this);
            if (isPoiSearch) {
                //设置搜索区域为以lp点为圆心，其周围5000米范围
                LatLonPoint latLonPoint = new LatLonPoint(currentLocation.latitude, currentLocation.longitude);
                poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 5000, true));
            }
            //异步搜索
            poiSearch.searchPOIAsyn();
        }
    }

    /**
     * 输入提示回调
     *
     * @param tipList 列表
     * @param rCode   返回码
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (flag) {
            if (rCode == 1000) {// 正确返回
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < tipList.size(); i++) {
                    if (tipList.get(i).getPoint() == null) {
                        tipList.remove(i);
                    }
                    list.add(tipList.get(i).getName());
                }
                if(tipList.size()>0) {
                    mCurrentTipList = tipList;
                    mIntipAdapter = new InputTipsAdapter(getActivity(), startNavi, mCurrentTipList);
                    mInputListView.setAdapter(mIntipAdapter);
                    mIntipAdapter.notifyDataSetChanged();
                    if (mListLayout.getVisibility() != View.VISIBLE) {
                        mListLayout.setVisibility(View.VISIBLE);
                    }
                    if (isEnter) {
                        mListLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                ToastUtil.showerror(getActivity(), rCode);
            }
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (flag) {
            if (rCode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    if (result.getQuery().equals(query)) {// 是否是同一条
                        poiResult = result;
                        // 取得搜索到的poiItems有多少页
                        poiItems = poiResult.getPois();
                        if(poiItems.size()>0) {
                            mInItemsapter = new InputItemsAdapter(getActivity(), currentLocation, poiItems);
                            mInputListView.setAdapter(mInItemsapter);
                            if (mListLayout.getVisibility() != View.VISIBLE) {
                                mListLayout.setVisibility(View.VISIBLE);
                            }
                            if (isEnter) {
                                mListLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    mTTSClient.speak(getResources().getString(R.string.map_no_result), true);
                    ToastUtil.show(getActivity(), R.string.map_no_result);
                }
            } else {
                mTTSClient.speak(getResources().getString(R.string.map_no_net), true);
                ToastUtil.show(getActivity(), R.string.map_no_net);
            }
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        //获取UI设置对象
        UiSettings settings = aMap.getUiSettings();
        //设置默认定位按钮是否显示
        settings.setMyLocationButtonEnabled(false);
        //设置缩放按钮位置
        settings.setZoomPosition(0);
        //设置显示地图比例尺
        settings.setScaleControlsEnabled(true);
        //初始化交通信息
        initTraffic();
    }

    /**
     * 初始化交通信息
     **/
    private void initTraffic() {
        //自定义实时交通信息的颜色样式
        MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
        myTrafficStyle.setSeriousCongestedColor(0xff92000a);
        myTrafficStyle.setCongestedColor(0xffea0312);
        myTrafficStyle.setSlowColor(0xffff7508);
        myTrafficStyle.setSmoothColor(0xff00a209);
        aMap.setMyTrafficStyle(myTrafficStyle);
        //显示实时交通状况
        aMap.setTrafficEnabled(true);
        //显示3D楼块
        aMap.showBuildings(true);
        //显示底图文字
        aMap.showMapText(true);
    }

    /**
     * 资源初始化
     */
    private void initData() {
        //方向传感器类
        mSensorHelper = new SensorEventHelper(getActivity());
        mSensorHelper.registerSensorListener();
        flag = true;
        isPoiSearch = false;
        isEnter = false;
        isFirstToMap = false;
        isNaviCreate = false;
        gson = new Gson();
        defaultSize = 16;
        busNoResult = false;
        mRouteSearch = new RouteSearch(getActivity());
        //语音类
        mTTSClient = TTSManager.getInstance(getActivity());
    }


    /**
     * 初始化定位，设置回调监听
     */
    private void initLocation() {
        //初始化client
        mLocationClient = new AMapLocationClient(getActivity());
        // 设置定位监听
        mLocationClient.setLocationListener(this);
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (NetStatusUtils.isMobileConnected(getActivity()) || NetStatusUtils.isWifiConnected(getActivity())) {
            //设置定位参数
            mLocationClient.setLocationOption(getOption());
            // 启动定位
            mLocationClient.startLocation();
        } else {
            ToastUtil.show(getActivity(), "您的网络存在问题，请重试！");
        }
    }

    /**
     * 设置定位参数
     *
     * @return 定位参数类
     */
    private AMapLocationClientOption getOption() {
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        return mLocationOption;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            currentLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            if(startNavi==null){
                startNavi = new NaviLatLng(currentLocation.latitude,currentLocation.longitude);
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                //获取定位信息
                StringBuffer buffer = new StringBuffer();
                buffer.append(amapLocation.getProvince() + ""
                        + amapLocation.getCity() + ""
                        + amapLocation.getDistrict() + ""
                        + amapLocation.getStreet() + ""
                        + amapLocation.getStreetNum());
                currentCity = amapLocation.getCity();
                //获取marker内容
                LOCATION_MARKER_FLAG = buffer.toString();
                //创建我的方向位置marker
                addCircle(currentLocation, 100.0f);//添加定位精度圆
                addLocationMarker(currentLocation);//添加定位图标
                mSensorHelper.setCurrentMarker(mSmallIcon);//定位图标旋转
                toMyLocation(currentLocation,16);
                if(isFirstToMap){
                    if(!"".equals(json)) {
                        parseJson(json, -1);
                    }
                    isFirstToMap = false;
                }
            }
        } else {
            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
        }
    }

    /**
     * 我的位置小蓝点属性初始化
     **/
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        aMap.addCircle(options);
    }

    /**
     * 添加我的位置覆盖物及浮动窗
     **/
    private void addLocationMarker(LatLng latlng) {
        if (mSmallIcon != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.map_mylocation_locked)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mSmallIcon = aMap.addMarker(options);
        mSmallIcon.setObject("location");
        mSmallIcon.setTitle(LOCATION_MARKER_FLAG);
        mSmallIcon.setPerspective(true);
        //直接展示
        mSmallIcon.showInfoWindow();
    }

    /**
     * 我的位置
     **/
    private void toMyLocation(LatLng currentLocation, int size) {
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(size));
        //将地图移动到定位点
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLocation));
        //显示点的windowInfo
        mSmallIcon.showInfoWindow();
    }

    /**
     * marker标记点击事件
     **/
    @Override
    public boolean onMarkerClick(Marker marker) {
        markerClickMethod(marker);
        return true;
    }

    /**
     * 处理Marker的点击事件
     * @param marker 点击的Marker对象
     */
    void markerClickMethod(Marker marker) {
        if(clickMarker!=null) {
            //改回原状态
            poiOverlay.setMarkerIcon(clickMarker, false);
        }else{
            //改变第一个
            poiOverlay.setMarkerIcon(poiOverlay.getMarkerItem(0),false);
        }
        clickMarker = marker;
        //改变为点击态
        poiOverlay.setMarkerIcon(marker,true);
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        isFirstLoc = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void callMapMarkPoint(boolean isVoice, String jsonBack, int position) {
        if (isVoice) {
            //判断是不是第一次进来
            if(LOCATION_MARKER_FLAG.equals("mylocation")){
                isFirstToMap = true;
                json = jsonBack;
            }else {
                parseJson(jsonBack,position);
            }
        } else {
            if(position>-1){
                //处理Item的点击事件
                if(poiOverlay!=null) {
                    if (poiOverlay.getMarkerItem(position) != null) {
                        Marker marker = poiOverlay.getMarkerItem(position);
                        markerClickMethod(marker);
                    }
                }
            }else {
                //显示搜索框
                if (mSearchLayout.getVisibility() == View.GONE) {
                    mSearchLayout.setVisibility(View.VISIBLE);
                }
                //显示listView
                if (mListLayout.getVisibility() == View.GONE) {
                    mListLayout.setVisibility(View.VISIBLE);
                }
                //显示焦点
                openEdit();
                //初始化
                isEnter = false;
                flag = true;
            }
        }
    }

    /**
     * 解析Json相关功能
     */
    private void parseJson(String json,int position) {
        JsonData jsonData = JsonUtil.createJsonData(json);
        if(mBusrouteOverlay!=null){
            mBusrouteOverlay.removeFromMap();
        }
        if(routeOverlays!=null){
            cleanRouteOverlay();
        }
        //删除并置空详情Marker
        if(detailMarker!=null) {
            detailMarker.destroy();
            detailMarker = null;
        }
        //定位操作处理
        if (jsonData.getIntention().equals("locating")) {
            //判断是否我的位置
            try {
                JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
                JSONArray locs = semantic.getJSONArray("MyLoc");
                if(locs!=null){
                    mTTSClient.speak("您当前位于"+LOCATION_MARKER_FLAG,true);
                    toMyLocation(currentLocation,16);
                }
            }catch (JSONException e){
                //处理其他情况
                mTTSClient.speak(jsonData.getTts(),true);
                //得到坐标点
                jsonData = JsonUtil.setParkingOrPoi(jsonData);
                if(jsonData.getPointList().size()<1){
                    //显示一个Dialog
                    showErrorMessage();
                }else {
                    Log.i("爱美丽", "拿到的list长度为："+jsonData.getPointList().size());
                    poiItems = PointConversionUtils.pointToPoi(poiItems, jsonData.getPointList());
                    //开启左边fragment
                    toChangeLeftData(true);
                }
            }
        }
        //poi周边处理
        else if(jsonData.getIntention().equals("searching")&&(jsonData.getType().equals("poi")||jsonData.getType().equals("parking"))){
            //准备数据
            jsonData = JsonUtil.setParkingOrPoi(jsonData);
            poiItems = PointConversionUtils.pointToPoi(poiItems,jsonData.getPointList());
            if(poiItems.size()>0) {
                mTTSClient.speak("已为您搜索到周边" + jsonData.getPointList().size() + "条结果，请查看", true);
                //开启左边fragment
                toChangeLeftData(true);
            }else {
                mTTSClient.speak("对不起，暂无相关搜索结果！",true);
            }
        }
        //餐厅搜索处理
        else if ((jsonData.getDomain().equals(NabooActions.Map.TARGET_RESTAURANT)||(jsonData.getDomain().equals(NabooActions.Map.TARGET_MAP)))&&(jsonData.getType().equals("restaurant")||jsonData.getType().equals("default"))){
            ImoranRestaurantResponse imoranRestaurantResponse= gson.fromJson(json, ImoranRestaurantResponse.class);
            //根据position来开启新的
            if(position>-1){
                //单独显示子项
                RestaurantReply reply = imoranRestaurantResponse.getData().getContent().getReply();
                if(reply.getLocationList()!=null){
                    LatLng latLng = new LatLng(reply.getLocationList().get(0).getLatitude(),reply.getLocationList().get(0).getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                    //隐藏其他点
                    if(position==404){
                        Marker marker =aMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(reply.getLocationList().get(0).getName())
                                .snippet(reply.getLocationList().get(0).getAddress()));
                        marker.showInfoWindow();
                        detailMarker = marker;
                        if(poiOverlay!=null) {
                            poiOverlay.hideMarker(position);
                            if(chooseMarker!=null) {
                                chooseMarker.destroy();
                            }
                        }
                    }else {
                        if(poiOverlay!=null) {
                            poiOverlay.hideMarker(position);
                            if(chooseMarker!=null) {
                                chooseMarker.destroy();
                            }
                        }
                    }
                    mTTSClient.speak(imoranRestaurantResponse.getData().getContent().getTts(),true);
                    //通知Activity开启新的MapDetailFragment
                    childCallBack.callFromMapAdapter(json,currentLocation);
                }
            }else{
                List<Restaurant> restaurantList = null;
                //判断是否是分类
                try {
                    //到这里说明是指定条件查询
                    restaurantList = imoranRestaurantResponse.getData().getContent().getReply().getLocationList();
                    JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
                    String msg = semantic.getJSONArray("Sortby_Cate").getString(0);
                    //取第一个
                    for(int i=1;i<restaurantList.size();i++){
                        restaurantList.remove(i);
                        i--;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                poiItems = PointConversionUtils.restaurantToPoi(poiItems,restaurantList);
                mTTSClient.speak("已为您搜索到周边"+restaurantList.size()+"所餐厅，请查看",true);
                if(restaurantList.size()<1) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                            .setTitle(getResources().getString(R.string.map_dialog_noresult))//设置对话框的标题
                            .setMessage(getResources().getString(R.string.map_dialog_message))//设置对话框的内容
                            //设置对话框的按钮
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }else {
                    //开启左边fragment
                    toChangeLeftData(false);
                }
            }
        }
        //景点相关处理
        else if (jsonData.getDomain().equals(NabooActions.Map.TARGET_VIEWSPOT)&&(jsonData.getType().equals("viewspot")||jsonData.getType().equals("default"))){
            //解析Json
            ImoranViewResponse imoranViewResponse = gson.fromJson(json,ImoranViewResponse.class);
            ViewReply viewReply = imoranViewResponse.getData().getContent().getReply();
            //根据position来开启新的
            if(position>-1){
                if(viewReply.getViewSpotList()!=null){
                    //单独显示子项
                    LatLng latLng = new LatLng(viewReply.getViewSpotList().get(0).getLatitude(),viewReply.getViewSpotList().get(0).getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                    //隐藏其他点
                    //隐藏其他点
                    if(position==404){
                        Marker marker =aMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(viewReply.getViewSpotList().get(0).getName())
                                .snippet(viewReply.getViewSpotList().get(0).getAddress()));
                        marker.showInfoWindow();
                        if(poiOverlay!=null) {
                            poiOverlay.hideMarker(position);
                            if(chooseMarker!=null) {
                                chooseMarker.destroy();
                            }
                        }
                        detailMarker = marker;
                        mTTSClient.speak(viewReply.getViewSpotList().get(0).getName()+"的详情如下:"+viewReply.getViewSpotList().get(0).getIntroduction(),true);
                    }else {
                        if(poiOverlay!=null) {
                            poiOverlay.hideMarker(position);
                            if(chooseMarker!=null) {
                                chooseMarker.destroy();
                            }
                        }
                        mTTSClient.speak(viewReply.getViewSpotList().get(0).getName()+"的详情如下:",true);
                    }
                    //通知Activity开启新的MapDetailFragment
                    childCallBack.callFromMapAdapter(json,currentLocation);
                }
            }else {
                List<ViewSpot> viewSpotList = null;
                try {
                    //到这里说明是指定条件查询
                    viewSpotList = imoranViewResponse.getData().getContent().getReply().getViewSpotList();
                    JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
                    String msg = semantic.getJSONArray("Sortby_Viewspot").getString(0);
                    //取第一个
                    for(int i=1;i<viewSpotList.size();i++){
                        viewSpotList.remove(i);
                        i--;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                poiItems = PointConversionUtils.viewSpotToPoi(poiItems, viewSpotList);
                //开启左边fragment
                toChangeLeftData(false);
                mTTSClient.speak("已为您搜索到周边" + poiItems.size() + "个景点，请查看", true);
            }
        }
        //命令相关处理
        else if(jsonData.getIntention().equals("instructing")&&jsonData.getDomain().equals("cmd")){
            if(jsonData.getType().equals("zoom_in")){
                if(defaultSize>20) {
                    defaultSize = 20;
                }else{
                    defaultSize += 1;
                }
                //放大地图
                //设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(defaultSize));
            }else if(jsonData.getType().equals("zoom_out")){
                if(defaultSize<4){
                    defaultSize = 4;
                }else {
                    defaultSize -= 1;
                }
                //缩小地图
                //设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(defaultSize));
            }
            mTTSClient.speak(jsonData.getTts(),true);
        }else{
            //通知Map关闭LEFT
            childCallBack.callMainCloseLeft();
        }
    }



    /**
     *  显示周边Poi搜索结果
     */
    private void toChangeLeftData(boolean isPoi) {
        //清空地图
        clearMarkers();
        //添加到地图Marker
        if(poiItems.size()>1){
            if(poiItems.get(0).getLatLonPoint()!=null) {
                if (poiItems.get(0).getLatLonPoint().getLatitude() == poiItems.get(1).getLatLonPoint().getLatitude()) {
                    poiItems.remove(1);
                }
            }else{
                mTTSClient.speak("对不起，暂无搜索结果",true);
            }
        }
        addPoiItemsMarker(poiItems);
        //隐藏搜索框
        if (mSearchLayout.getVisibility() == View.VISIBLE) {
            mSearchLayout.setVisibility(View.GONE);
        }
        //给key赋值
        key = "附近搜索结果";
        //再次规划显示区域用来防止首次加载问题
        poiOverlay.zoomToSpan();
        //回调显示另一个fragment
        childCallBack.callFromMap(poiItems, currentLocation, key, isPoi);
    }

    @Override
    public void callMapDrawRoute(NaviBean naviBean, int position, String wayStyle, String tts) {
        //保存此时的目的地名称
        if(naviBean.getEndName()!=null) {
            endPointName = naviBean.getEndName();
        }
        //给个初始值
        if(naviBean.getStartName()!=null) {
            startPointName = naviBean.getStartName();
        }
        //隐藏其他的点
        if(poiOverlay!=null) {
            poiOverlay.hideMarker(position);
            //隐藏点选点
            if(chooseMarker!=null) {
                chooseMarker.destroy();
            }
        }
        //隐藏Search
        if(mSearchLayout.getVisibility()==View.VISIBLE){
            mSearchLayout.setVisibility(View.GONE);
        }
        if(detailMarker!=null) {
            //删除并置空详情Marker
            detailMarker.destroy();
            detailMarker = null;
        }
        //添加起点坐标
        if(startNavi==null){
            //隐藏视图
            if(mSearchLayout.getVisibility()==View.VISIBLE){
                mSearchLayout.setVisibility(View.GONE);
            }
        }
        NaviLatLng navi = naviBean.getStartNaviLatlng();
        if(navi!=null){
            //设置起点
            startList.clear();
            startList.add(navi);
            startNavi = navi;
            //保存起点名称
            busStartNavi = navi;
            if(naviBean.getStartName()!=null) {
                startPointName = naviBean.getStartName();
            }
        }else {
            if(startNavi!=null) {
                //设置起点
                startList.clear();
                startList.add(startNavi);
                //设置起点名称
                startPointName = getResources().getString(R.string.map_location);
            }
        }

        //设置途经点
        if(wayList.size()>0){
            wayList.clear();
        }
        navi = naviBean.getMidNaviLatlng();
        if(navi!=null) {
            wayList.add(navi);
        }

        //设置终点
        if(naviBean.getEndNaviLatlng()!=null) {
            endNavi = naviBean.getEndNaviLatlng();
        }
        if(endList.size()>0){
            endList.clear();
        }
        endList.add(endNavi);

        if("detailStyle".equals(wayStyle)){
            //说明是从detail规划过来的
            if(!"".equals(way)){
                if(!busNoResult) {
                    wayStyle = way;
                }else{
                    wayStyle = "DRIVE";
                }
            }else {
                wayStyle = "DRIVE";
            }
        }else if("".equals(wayStyle)){
            if("".equals(way)){
                wayStyle = "DRIVE";
            }else{
                if(wayList.size()>0){
                    wayStyle = "DRIVE";
                }else{
                    wayStyle = way;
                }
            }
        }
        //说话
        if(!"".equals(tts)){
            mTTSClient.speak(tts,true);
        }
        switch (wayStyle){
            case "WALK":
                way = "WALK";
                if(isNaviCreate) {
                    startWalkNavi();
                }
                break;
            case "BIKE":
                way = "BIKE";
                if(isNaviCreate) {
                    startBikeNavi();
                }
                break;
            case "DRIVE":
                way = "DRIVE";
                if(isNaviCreate) {
                    startDriveNavi();
                }
                break;
            case "PUBTRANS":
                way = "PUBTRANS";
                if(isNaviCreate) {
                    startBusNavi();
                }
                break;
        }
    }

    /**
     * 驾车路径规划
     */
    private void startDriveNavi() {
        showProgressDialog();
        try {
            strategyFlag = mAMapNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
    }

    /**
     * 步行路径规划
     */
    private void startWalkNavi() {
        if(isNaviCreate) {
            LatLng start = new LatLng(startNavi.getLatitude(),startNavi.getLongitude());
            LatLng end = new LatLng(endNavi.getLatitude(),endNavi.getLongitude());
            if((AMapUtils.calculateLineDistance(start,end)/1000)>100){
                mTTSClient.speak(getResources().getString(R.string.map_route_toofar),true);
            }else{
                showProgressDialog();
                mAMapNavi.calculateWalkRoute(startNavi, endNavi);
            }
        }
    }

    /**
     * 骑行路径规划
     */
    private void startBikeNavi() {
        LatLng start = new LatLng(startNavi.getLatitude(),startNavi.getLongitude());
        LatLng end = new LatLng(endNavi.getLatitude(),endNavi.getLongitude());
        if((AMapUtils.calculateLineDistance(start,end)/1000)>100){
            mTTSClient.speak(getResources().getString(R.string.map_route_toofar),true);
        }else{
            showProgressDialog();
            mAMapNavi.calculateRideRoute(startNavi, endNavi);
        }
    }

    /**
     * 公交路径规划
     */
    private void startBusNavi() {
        showProgressDialog();
        mRouteSearch.setRouteSearchListener(this);
        NaviLatLng latLng;
        if(busStartNavi!=null){
            latLng = busStartNavi;
        }else {
            latLng = startNavi;
        }
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(latLng.getLatitude(), latLng.getLongitude()),
                new LatLonPoint(endNavi.getLatitude(), endNavi.getLongitude()));
        // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BUS_DEFAULT,
                currentCity, 0);
        mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void cleanRouteOverlay() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);
            RouteOverLay overlay = routeOverlays.get(key);
            overlay.removeFromMap();
            overlay.destroy();
        }
        routeOverlays.clear();
    }

    @Override
    public void onInitNaviFailure() {}

    @Override
    public void onInitNaviSuccess() {
        isNaviCreate = true;
        if(way.equals("DRIVE")){
            //开启驾车模式
            way = "DRIVE";
            startDriveNavi();
        }else if(way.equals("PUBTRANS")){
            //开启公交模式
            way = "PUBTRANS";
            startBusNavi();
        }else if(way.equals("WALK")){
            //开启步行模式
            way = "WALK";
            startWalkNavi();
        }else if(way.equals("BIKE")){
            //开启骑行模式
            way = "BIKE";
            startBikeNavi();
        }
    }

    @Override
    public void onStartNavi(int i) {}

    @Override
    public void onTrafficStatusUpdate() {}

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {}

    @Override
    public void onGetNavigationText(int i, String s) {}

    @Override
    public void onEndEmulatorNavi() {}

    @Override
    public void onArriveDestination() {}

    /**
     * 返回步行及骑车的算路结果
     */
    @Override
    public void onCalculateRouteSuccess() {
        //清空非bus
        cleanRouteOverlay();
        //清空bus
        if(mBusrouteOverlay!=null) {
            mBusrouteOverlay.removeFromMap();
        }
        if(beanList==null){
            beanList = new ArrayList<>();
        }else {
            beanList.clear();
        }
        if (way.equals("WALK")) {
            dissmissProgressDialog();
            AMapNaviPath naviPath = mAMapNavi.getNaviPath();
            if (naviPath != null) {
                drawWalkRoute(3, naviPath);
                //封装数据
                String type = naviPath.getLabels();
                String distance = Utils.getFriendlyDistance(naviPath.getAllLength());
                String time = Utils.getFriendlyTime(naviPath.getAllTime());
                RouteMessageBean routeMessageBean = new RouteMessageBean(way,type,distance,time);
                beanList.add(routeMessageBean);
            }
            routeID = routeOverlays.keyAt(0);
            //播报语音
            mTTSClient.speak(getResources().getString(R.string.map_route_walk),true);
            //回调到Activity开启RouteFragment
            childCallBack.callFromMapRoute(startPointName,endPointName,beanList);
        }else if (way.equals("BIKE")) {
            dissmissProgressDialog();
            AMapNaviPath naviPath = mAMapNavi.getNaviPath();
            if (naviPath != null) {
                drawBikeRoute(4, naviPath);
                //封装数据
                String type = naviPath.getLabels();
                String distance = Utils.getFriendlyDistance(naviPath.getAllLength());
                String time = Utils.getFriendlyTime(naviPath.getAllTime());
                RouteMessageBean routeMessageBean = new RouteMessageBean(way,type,distance,time);
                beanList.add(routeMessageBean);
            }
            routeID = routeOverlays.keyAt(0);
            //播报语音
            mTTSClient.speak(getResources().getString(R.string.map_route_bike),true);
            //回调到Activity开启RouteFragment
            childCallBack.callFromMapRoute(startPointName,endPointName,beanList);
        }
    }

    @Override
    public void onCalculateRouteFailure(int i) {}

    @Override
    public void onReCalculateRouteForYaw() {}

    @Override
    public void onReCalculateRouteForTrafficJam() {}

    @Override
    public void onArrivedWayPoint(int i) {}

    @Override
    public void onGpsOpenStatus(boolean b) {}

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {}

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {}

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {}

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {}

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {}

    @Override
    public void hideCross() {}

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {}

    @Override
    public void hideLaneInfo() {}

    /**
     * 驾车算路回调方法
     * @param ints 路径
     */
    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        intArrays = ints;
        //清空非bus
        cleanRouteOverlay();
        //清空bus
        if(mBusrouteOverlay!=null) {
            mBusrouteOverlay.removeFromMap();
        }
        if(beanList==null){
            beanList = new ArrayList<>();
        }else{
            beanList.clear();
        }
        if (way.equals("DRIVE")) {
            HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
            for (int i = 0; i < ints.length; i++) {
                AMapNaviPath path = paths.get(ints[i]);
                if (path != null) {
                    //绘制路线
                    drawDriveRoutes(i, path);
                    //封装数据
                    String type = path.getLabels();
                    String distance = Utils.getFriendlyDistance(path.getAllLength());
                    String time = Utils.getFriendlyTime(path.getAllTime());
                    RouteMessageBean routeMessageBean = new RouteMessageBean(way,type,distance,time);
                    beanList.add(routeMessageBean);
                }
            }
            //显示第n条路径
            showDriveRouteLine(ints,0);
            dissmissProgressDialog();
            //播报
            mTTSClient.speak("已为您规划出"+paths.size()+"条驾车路径，请查看",true);
            //回调到Activity开启RouteFragment
            childCallBack.callFromMapRoute(startPointName,endPointName,beanList);
        }
    }

    /**
     * 绘制返回的驾车路径
     *
     * @param routeId 路径规划线路ID
     * @param path    AMapNaviPath
     */
    private void drawDriveRoutes(int routeId, AMapNaviPath path) {
        try {
            aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, getContext());
        try {
            routeOverLay.setWidth(60f);
        } catch (AMapNaviException e) {
            e.printStackTrace();
        }
        routeOverLay.setTrafficLine(true);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
        routeOverLay.zoomToSpan();
    }

    /**
     * 绘制返回的步行路径
     *
     * @param routeId 路径规划线路ID
     * @param path    AMapNaviPath
     */
    private void drawWalkRoute(int routeId, AMapNaviPath path) {
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, getContext());
        routeOverLay.setTrafficLine(true);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
        routeOverLay.zoomToSpan();
    }

    /**
     * 绘制返回的骑行路径
     *
     * @param routeId 路径规划线路ID
     * @param path    AMapNaviPath
     */
    private void drawBikeRoute(int routeId, AMapNaviPath path) {
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, getContext());
        routeOverLay.setTrafficLine(true);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
        routeOverLay.zoomToSpan();
    }

    /**
     * 用来显示第n条路径
     * @param number 标识
     * @param ints 路径总条数
     */
    private void showDriveRouteLine(int[] ints,int number) {
        if(ints.length>0){
            RouteOverLay routeOverLay = null;
            try {
                for (int i = 0; i < ints.length; i++) {
                    if (i == number) {//显示
                        routeOverLay = routeOverlays.get(routeOverlays.keyAt(i));
                        routeID = ints[number];
                        routeOverLay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
                    } else {//隐藏
                        routeOverLay = routeOverlays.get(routeOverlays.keyAt(i));
                        routeOverLay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 未搜索到结果时展示错误信息
     */
    private void showErrorMessage() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                .setTitle(getResources().getString(R.string.map_dialog_noresult))//设置对话框的标题
                .setMessage(getResources().getString(R.string.map_dialog_message))//设置对话框的内容
                //设置对话框的按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void callLeftChangeList(List<PoiItem> poiItemList, String key, boolean isPoi) {}

    @Override
    public void callDetailChangeList(String json) {}

    @Override
    public void callRouteChangeList(String startName, String endName, List<RouteMessageBean> beanList) {}

    /**
     * 此方法用来显示到底是第几条路径
     * @param index 索引
     */
    @Override
    public void callMapChooseRouteNum(int index) {
        //显示对应线路
        if(intArrays!=null&&intArrays.length>0){
            showDriveRouteLine(intArrays,index);
        }
    }

    /**
     * 处理传递过来的模式切换
     * @param style 模式
     */
    @Override
    public void callMapChooseRouteStyle(String style) {
        if(style.equals("DRIVE")){
            //开启驾车模式
            way = "DRIVE";
            startDriveNavi();
        }else if(style.equals("PUBTRANS")){
            //开启公交模式
            way = "PUBTRANS";
            startBusNavi();
        }else if(style.equals("WALK")){
            //开启步行模式
            way = "WALK";
            startWalkNavi();
        }else if(style.equals("BIKE")){
            //开启骑行模式
            way = "BIKE";
            startBikeNavi();
        }
    }

    @Override
    public void callBusRouteChange(String start,String end,BusRouteResult result) {

    }

    /**
     * 开始导航
     */
    @Override
    public void callMapToNavi() {
        if("PUBTRANS".equals(way)){
            mTTSClient.speak(getResources().getString(R.string.map_navi_nobus),true);
        }else{
            if (routeID != -1) {
                mAMapNavi.selectRouteId(routeID);
                Intent gpsintent = new Intent(getActivity().getApplicationContext(), RouteNaviActivity.class);
                gpsintent.putExtra("gps", false); // gps 为true为真实导航，为false为模拟导航
                startActivity(gpsintent);
            }
        }
    }

    @Override
    public void callRouteChangeTextColor(int index) {

    }

    @Override
    public void callBusDetailChange(BusPath busPath, BusRouteResult busRouteResult, String startName, String endName) {

    }

    /**
     * 回调至Map去绘制公交的点选路径
     * @param pos 点击位置
     * @param busPath 点击的路径
     * @param result 结果集
     * @param endName 终点
     */
    @Override
    public void callMapToDrawBusRoute(int pos, BusPath busPath, BusRouteResult result, String startName, String endName) {
        //绘制公交路径
        drawBusRoute(pos,busPath,result,startName,endName);
    }

    /**
     * 返回到地图主界面时用来显示搜索框
     */
    @Override
    public void callMapShowSearch() {
        if(mSearchLayout.getVisibility()==View.GONE){
            mSearchLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 清空Map中的东西
     */
    @Override
    public void callMapCleanView() {
        if(mSearchLayout.getVisibility()==View.VISIBLE){
            //关闭键盘
            closeEdit();
            mSearchLayout.setVisibility(View.GONE);
            mListLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyParallelRoad(int i) {}

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {}

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {}

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {}

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {}

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {}

    @Override
    public void onPlayRing(int i) {}

    /**
     * 返回公交查询结果
     **/
    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        dissmissProgressDialog();

        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    busNoResult = false;
                    //清理路径
                    cleanRouteOverlay();
                    mBusRouteResult = result;
                    //显示相关路径
                    if(mBusrouteOverlay!=null){
                        mBusrouteOverlay.removeFromMap();
                        mBusrouteOverlay = null;
                    }
                    mBusrouteOverlay = new BusRouteOverlay(getActivity(), aMap,
                            mBusRouteResult.getPaths().get(0), mBusRouteResult.getStartPos(),
                            mBusRouteResult.getTargetPos());
                    mBusrouteOverlay.removeFromMap();
                    if (mBusrouteOverlay != null) {
                        mBusrouteOverlay.addToMap();
                        mBusrouteOverlay.zoomToSpan();
                    }
                    //播报
                    mTTSClient.speak("为您推荐"+mBusRouteResult.getPaths().size()+"条公交路径，请查看",true);
                    //传递到RouteFragment
                    childCallBack.callFromMapBusRoute(startPointName,endPointName,mBusRouteResult);
                } else if (result.getPaths() == null) {
                    ToastUtil.show(getContext(), R.string.map_no_result);
                } else {
                    //弹出对话框
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getResources().getString(R.string.map_search_faild))
                            .setMessage(getResources().getString(R.string.map_search_busnoresult))
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    mTTSClient.speak(getResources().getString(R.string.map_search_allbusno),true);
                    alertDialog.show();
                    //设置个标志位
                    busNoResult = true;
                }
            } else {
                ToastUtil.show(getContext(), R.string.map_no_result);
            }
        } else {
            ToastUtil.showerror(getContext(), errorCode);
        }
    }

    /**
     * 绘制公交相关路径
     */
    private void drawBusRoute(int pos, BusPath busPath, BusRouteResult result, String startName, String endName) {
        if(mBusrouteOverlay!=null) {
            mBusrouteOverlay.removeFromMap();
            mBusrouteOverlay = null;
        }
        //显示相关路径
        mBusrouteOverlay = new BusRouteOverlay(getActivity(), aMap,
                result.getPaths().get(pos), result.getStartPos(),
                result.getTargetPos());
        mBusrouteOverlay.addToMap();
        mBusrouteOverlay.zoomToSpan();
        //播放语音
        mTTSClient.speak("已为您选择第"+String.valueOf(pos+1)+"条公交路径,请查看",true);
        //回调至Activity开启数据的更新
        childCallBack.callFromMapToOpenBusDetail(busPath,result,startName,endName);
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {}

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {}

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(aMap!=null){aMap.clear();
            aMap = null;}
        if(mSensorHelper!=null){mSensorHelper.unRegisterSensorListener();
            mSensorHelper = null;}
        if(mLocationClient!=null){mLocationClient.onDestroy();}
        if(currentLocation!=null){currentLocation=null;}
        if(startLatlng!=null){startLatlng = null;}
        if(startNavi!=null){startNavi=null;}
        if(endLatlng!=null){endLatlng = null;}
        if(endNavi!=null){endNavi = null;}
        if(mSmallIcon!=null){mSmallIcon=null;}
        if(poiSearch!=null){poiSearch=null;
            query=null;}
        if(poiItems!=null){poiItems.clear();poiItems=null;}
        if(singlePoiItems!=null){singlePoiItems.clear();singlePoiItems=null;}
        if(mCurrentTipList!=null){mCurrentTipList.clear();mCurrentTipList=null;}
        if(poiOverlay!=null){poiOverlay.removeFromMap();poiOverlay = null;}
        if(clickMarker!=null){clickMarker.destroy();clickMarker=null;}
        if(mStrategyBean!=null){mStrategyBean=null;}
        if(startList!=null){startList.clear();wayList.clear();endList.clear();startList=null;wayList=null;endList=null;}
        if(routeOverlays!=null){routeOverlays.clear();routeOverlays=null;}
        if(beanList!=null){beanList.clear();beanList=null;}
        if(mBusrouteOverlay!=null){mBusrouteOverlay.removeFromMap();mBusrouteOverlay=null;}
        if(mBusRouteResult!=null){mBusRouteResult=null;}
        if(detailMarker!=null){detailMarker.destroy();detailMarker=null;}
        mAMapNavi.destroy();
        if(mapView!=null) {mapView.onDestroy();
            mapView = null;}
    }
}