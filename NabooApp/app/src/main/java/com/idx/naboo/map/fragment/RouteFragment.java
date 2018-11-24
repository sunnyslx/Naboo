package com.idx.naboo.map.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idx.naboo.R;
import com.idx.naboo.map.Interface.BusRouteListener;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.Interface.ParentCallBack;
import com.idx.naboo.map.adapter.BusResultListAdapter;
import com.idx.naboo.map.adapter.InputItemsAdapter;
import com.idx.naboo.map.adapter.InputTipsAdapter;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;
import com.idx.naboo.map.mapUtils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayden on 18-4-17.
 */

public class RouteFragment extends Fragment implements ParentCallBack, View.OnClickListener, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener{

    private ChildCallBack childCallBack;
    private Gson gson;
    private Button driveClick, busClick, walkClick, bikeClick;
    private LinearLayout routeLineOne, routeLineTwo, routeLineThree;
    private TextView routeLineOneStrategy, routeLineTwoStrategy, routeLineThreeStrategy;
    private TextView routeLineOneTime, routeLineTwoTime, routeLineThreeTime;
    private TextView routeLineOneDistance, routeLineTwoDistance, routeLineThreeDistance;
    private TextView routeLineOneOrder, routeLineTwoOrder, routeLineThreeOrder;
    private ImageView clear1,clear2;
    //公交路线
    private ListView mBusResultList;
    private BusRouteResult mBusRouteResult;
    private LinearLayout mBusResultLayout;
    private RelativeLayout map_function;
    private LinearLayout threeMessage;
    private ImageButton backToDetail;
    private EditText startLocationName;
    private EditText endLocationName;
    private String startName;
    private String endName;
    private List<RouteMessageBean> beanList;
    private boolean isFirstTime;
    //改变的选中图片
    private Drawable drawable;
    private String style;
    private BusRouteListener mBusRouteListener;

    //新添加的
    private String key = "";
    //用来判断是poi给的还是关键字给的adapter;
    private boolean isPoiSearch;
    //所在城市名称
    private String currentCity = "";
    //周边搜索对象
    private PoiSearch.Query query;
    //PoiSearch对象
    private PoiSearch poiSearch;
    //我的位置对象
    private LatLng currentLocation = null;
    private ListView mListView;
    //Tip对象存储
    private List<Tip> mCurrentTipList;
    private InputTipsAdapter mtipAdapter;
    private InputItemsAdapter mItemsApter;
    //用来控制点击子项时，不再去后台查找
    private boolean flag;
    //poi返回的结果
    private PoiResult poiResult;
    //存储周边搜索数据
    private List<PoiItem> poiItems = new ArrayList<>();
    //控件是否获取到了焦点
    private Boolean isFocusA,isFocusB;
    private LinearLayout routeMapHeader;
    private ScrollView scrollView;
    //键盘控制器
    private InputMethodManager immanager;

    public static Fragment newInstance(String startName, String endName, String beanList, BusRouteResult busRouteResult, String name, String latLng) {
        RouteFragment fragment = new RouteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("startName", startName);
        bundle.putString("endName", endName);
        bundle.putString("beanList", beanList);
        bundle.putParcelable("busRouteResult",busRouteResult);
        bundle.putString("cityName",name);
        bundle.putString("LatLng",latLng);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        startName = getArguments().getString("startName");
        endName = getArguments().getString("endName");
        String beans = getArguments().getString("beanList");
        gson = new Gson();
        beanList = gson.fromJson(beans, new TypeToken<List<RouteMessageBean>>() {
        }.getType());
        currentCity = getArguments().getString("cityName");
        String ll = getArguments().getString("LatLng");
        currentLocation = gson.fromJson(ll,new TypeToken<LatLng>(){}.getType());
        mBusRouteResult = getArguments().getParcelable("busRouteResult");
        isFocusA = false;
        isFocusB = false;
        flag = true;
        //获取控制器
        immanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof ChildCallBack) {
            childCallBack = (ChildCallBack) getActivity();
        }
        if(getActivity() instanceof BusRouteListener){
            mBusRouteListener = (BusRouteListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_route_fragment, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //做情况判断
        if(beanList==null&&mBusRouteResult!=null){
            //首次启动公交
            receiveBusData(endName,mBusRouteResult);
            dealWithPointName();
            //填充起点
            startLocationName.setText(startName);
            //填充终点
            endLocationName.setText(endName);
            //设置模式为公交
            style = "PUBTRANS";
        }else {
            //解析
            if(!"PUBTRANS".equals(style)) {
                //先设置默认模式
                style = beanList.get(0).getStyle();
                parseData();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //不在最前端显示 相当于调用了onPause();
        if (hidden) {

        }
        //在最前端显示 相当于调用了onResume();
        else {
            //先判断是哪个fragment过来的
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.contains);
            if (fragment.getClass().getSimpleName().equals("DetailFragment")) {
                parseData();
            }
        }
    }

    /**
     * 解析传递过来的response
     */
    private void parseResponse() {
        //处理名字
        dealWithPointName();
        //填充起点
        startLocationName.setText(startName);
        //填充终点
        endLocationName.setText(endName);
        //设置数据
        setRouteData();
    }

    /**
     * 处理相关的地点名称
     */
    private void dealWithPointName() {
        if(startName.contains("富士康F8栋")||startName.equals("广东省深圳市龙华区梅龙大道富士康科技集团(清泉路)")){
            startName = "我的位置";
        }else {
            if (startName.contains("市") && !startName.endsWith("市")) {
                int index = startName.indexOf("市");
                String start = startName.substring(index + 1, startName.length());
                startName = start;
            }
        }
        if(endName.contains("市")&&!endName.endsWith("市")){
            int index = endName.indexOf("市");
            String end = endName.substring(index+1,endName.length());
            endName = end;
        }
    }

    /**
     * 设置相关数据
     */
    private void setRouteData() {
        if (!style.equals("PUBTRANS")) {
            //隐藏公交视图
            if (mBusResultLayout.getVisibility() == View.VISIBLE) {
                mBusResultLayout.setVisibility(View.GONE);
            }
            //显示自己的视图
            if (routeMapHeader.getVisibility()==View.GONE){
                routeMapHeader.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
            //先判断该显示几条
            if (beanList.size() >= 1) {
                //显示路径一
                if (routeLineOne.getVisibility() == View.GONE) {
                    routeLineOne.setVisibility(View.VISIBLE);
                }
                routeLineTwo.setVisibility(View.GONE);
                routeLineThree.setVisibility(View.GONE);
                //赋值
                if (style.equals("DRIVE")) {
                    routeLineOneStrategy.setText("方案一：" + beanList.get(0).getRouteType());
                } else {
                    routeLineOneStrategy.setText("方案：" + "综合策略");
                }
                routeLineOneTime.setText("耗时：" + beanList.get(0).getRouteTime());
                routeLineOneDistance.setText("路径总长：" + beanList.get(0).getRouteDistance());
                //默认选中
                changeToRoute(1);
            }
            if (beanList.size() >= 2) {
                //显示路径二
                if (routeLineTwo.getVisibility() == View.GONE) {
                    routeLineTwo.setVisibility(View.VISIBLE);
                }
                routeLineThree.setVisibility(View.GONE);
                //赋值
                routeLineTwoStrategy.setText("方案二：" + beanList.get(1).getRouteType());
                routeLineTwoTime.setText("耗时：" + beanList.get(1).getRouteTime());
                routeLineTwoDistance.setText("路径总长：" + beanList.get(1).getRouteDistance());
            }
            if (beanList.size() >= 3) {
                //显示路径三
                if (routeLineThree.getVisibility() == View.GONE) {
                    routeLineThree.setVisibility(View.VISIBLE);
                }
                //赋值
                routeLineThreeStrategy.setText("方案三：" + beanList.get(2).getRouteType());
                routeLineThreeTime.setText("耗时：" + beanList.get(2).getRouteTime());
                routeLineThreeDistance.setText("路径总长：" + beanList.get(2).getRouteDistance());
            }
        }
    }

    @Override
    public void callMapMarkPoint(boolean isVoice, String json, int position) {
    }

    @Override
    public void callMapDrawRoute(NaviBean naviBean, int position, String wayStyle, String tts) {

    }

    @Override
    public void callLeftChangeList(List<PoiItem> pois, String key, boolean isPoi) {

    }

    @Override
    public void callDetailChangeList(String json) {

    }

    @Override
    public void callRouteChangeList(String startNameBack, String endNameBack, List<RouteMessageBean> beanListBack) {
        startName = startNameBack;
        endName = endNameBack;
        beanList = beanListBack;
        parseData();
    }

    @Override
    public void callMapChooseRouteNum(int index) {

    }

    @Override
    public void callMapChooseRouteStyle(String style) {

    }

    /**
     * 接收来自地图的公交数据
     *
     * @param result 公交数据
     */
    @Override
    public void callBusRouteChange(String start,String end, BusRouteResult result) {
        startName = start;
        receiveBusData(end,result);
    }

    /**
     * 接收到了Bus数据，准备相关视图
     * @param result Bus结果集
     */
    private void receiveBusData(String endName, BusRouteResult result) {
        //改变图标
        changeToBus();
        dealWithPointName();
        //改变起点
        startLocationName.setText(startName);
        //改变终点
        endLocationName.setText(endName);
        style = "PUBTRANS";
        mBusRouteResult = result;
        BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(getActivity(), mBusRouteResult, mBusRouteListener,startName, endName);
        mBusResultList.setAdapter(mBusResultListAdapter);
        //显示
        hideOtherTypeLayout();
        mBusResultLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void callMapToNavi() {

    }

    /**
     * 用来改变选中的路径颜色
     * @param index 选中路径索引
     */
    @Override
    public void callRouteChangeTextColor(int index) {
        changeToRoute(index);
    }

    @Override
    public void callBusDetailChange(BusPath busPath, BusRouteResult busRouteResult, String startName, String endName) {

    }

    @Override
    public void callMapToDrawBusRoute(int pos, BusPath busPath, BusRouteResult result, String startName, String endName) {

    }

    @Override
    public void callMapShowSearch() {

    }

    @Override
    public void callMapCleanView() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drive_click:
                if(!style.equals("DRIVE")) {
                    changeToDrive();
                    //通知Activity去替换为驾车模式
                    childCallBack.callFromRouteStyle("DRIVE");
                    style = "DRIVE";
                }
                break;
            case R.id.bus_click:
                if(!style.equals("PUBTRANS")) {
                    changeToBus();
                    //通知Activity去替换为公交模式
                    childCallBack.callFromRouteStyle("PUBTRANS");
                    style = "PUBTRANS";
                }
                break;
            case R.id.walk_click:
                if(!style.equals("WALK")) {
                    changeToWalk();
                    //通知Activity去替换为步行模式
                    childCallBack.callFromRouteStyle("WALK");
                    style = "WALK";
                }
                break;
            case R.id.bike_click:
                if(!style.equals("BIKE")) {
                    changeToBike();
                    //通知Activity去替换为骑行模式
                    childCallBack.callFromRouteStyle("BIKE");
                    style = "BIKE";
                }
                break;
            case R.id.route_line_one:
                changeToRoute(1);
                //通知Activity去更改UI
                childCallBack.callFromRouteDetail(false, 0);
                break;
            case R.id.route_line_two:
                changeToRoute(2);
                //通知Activity去更改UI
                childCallBack.callFromRouteDetail(false, 1);
                break;
            case R.id.route_line_three:
                changeToRoute(3);
                //通知Activity去更改UI
                childCallBack.callFromRouteDetail(false, 2);
                break;
            case R.id.map_route_back:
                childCallBack.callFromRouteDetail(true, -1);
            default:
                break;
        }
    }

    //视图改变相关操作
    private void initView(View view) {
        routeMapHeader = view.findViewById(R.id.routemap_header);
        scrollView = view.findViewById(R.id.scroll);
        clear1 = view.findViewById(R.id.clear1);
        clear2 = view.findViewById(R.id.clear2);
        mListView = view.findViewById(R.id.route_list);
        //设置获取到焦点隐藏键盘
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //关闭键盘
                closeEdit(v);
                return false;
            }
        });
        //设置子项的Item点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //校验是否重名
                //隐藏删除符
                PoiItem poiItem = null;
                //设置点坐标位置
                if (isPoiSearch) {
                    //修改poiItems
                    poiItem = poiItems.get(position);
                } else {
                    //转换
                    Tip tip = mCurrentTipList.get(position);
                    poiItem = new PoiItem("id", tip.getPoint(), tip.getName(), tip.getAddress());
                }
                if(isFocusA){
                    if(poiItem.getTitle().equals(endName)){
                        Toast.makeText(getContext(),"起终点名称不能相同",Toast.LENGTH_SHORT).show();
                    }else{
                        dealWithClick(poiItem);
                    }
                }
                if(isFocusB){
                    if(poiItem.getTitle().equals(startName)){
                        Toast.makeText(getContext(),"起终点名称不能相同",Toast.LENGTH_SHORT).show();
                    }else{
                        dealWithClick(poiItem);
                    }
                }

            }
        });
        driveClick = view.findViewById(R.id.drive_click);
        busClick = view.findViewById(R.id.bus_click);
        walkClick = view.findViewById(R.id.walk_click);
        bikeClick = view.findViewById(R.id.bike_click);
        driveClick.setOnClickListener(this);
        busClick.setOnClickListener(this);
        walkClick.setOnClickListener(this);
        bikeClick.setOnClickListener(this);
        backToDetail = view.findViewById(R.id.map_route_back);
        backToDetail.setOnClickListener(this);
        startLocationName = view.findViewById(R.id.my_location_name);
        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空起点信息
                startLocationName.setText("");
            }
        });
        clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空终点信息
                endLocationName.setText("");
            }
        });
        //设置
        //设置改变时属性
        final int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        startLocationName.setInputType(inputType);
        //设置内容变化监听
        startLocationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus&&startLocationName.getText().toString().trim().length()>0){
                    //显示删除符号
                    clear1.setVisibility(View.VISIBLE);
                    //变更值
                    isFocusA = true;
                    //隐藏路径规划
                    if(scrollView.getVisibility()==View.VISIBLE){
                        scrollView.setVisibility(View.GONE);
                    }
                    if(routeMapHeader.getVisibility()==View.VISIBLE){
                        routeMapHeader.setVisibility(View.GONE);
                    }
                }
                if(!hasFocus){
                    //隐藏删除
                    clear1.setVisibility(View.GONE);
                    isFocusA = false;
                }
            }
        });
        startLocationName.addTextChangedListener(new TextWatcher() {
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
                    if (clear1.getVisibility() != View.VISIBLE&&isFocusA) {
                        clear1.setVisibility(View.VISIBLE);
                    }
                    key = s.toString();
                    //判断接收到的词
                    if (chooseSearchWay(key)) {
                        //查询周边模糊词
                        searchSurround();
                    } else {
                        InputtipsQuery inputquery = new InputtipsQuery(s.toString(), currentCity);
                        Inputtips inputTips = new Inputtips(getActivity(), inputquery);
                        inputTips.setInputtipsListener(RouteFragment.this);
                        inputTips.requestInputtipsAsyn();
                    }
                } else {
                    //隐藏删除符
                    if (clear1.getVisibility() == View.VISIBLE) {
                        clear1.setVisibility(View.GONE);
                    }
                    //隐藏listView
                    if (mListView.getVisibility() == View.VISIBLE) {
                        mListView.setVisibility(View.GONE);
                    }
                    if (mtipAdapter != null && mCurrentTipList != null) {
                        mCurrentTipList.clear();
                        mtipAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        endLocationName = view.findViewById(R.id.end_location_name);
        endLocationName.setInputType(inputType);
        //设置内容变化监听
        endLocationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus&&endLocationName.getText().toString().trim().length()>0){
                    //显示删除符号
                    clear2.setVisibility(View.VISIBLE);
                    //变更值
                    isFocusB = true;
                    //隐藏路径规划
                    if(scrollView.getVisibility()==View.VISIBLE){
                        scrollView.setVisibility(View.GONE);
                    }
                    if(routeMapHeader.getVisibility()==View.VISIBLE){
                        routeMapHeader.setVisibility(View.GONE);
                    }
                }
                if(!hasFocus){
                    //隐藏删除
                    clear2.setVisibility(View.GONE);
                    isFocusB = false;
                }
            }
        });
        //设置内容变化监听
        endLocationName.addTextChangedListener(new TextWatcher() {
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
                    if (clear2.getVisibility() != View.VISIBLE&&isFocusB) {
                        clear2.setVisibility(View.VISIBLE);
                    }
                    key = s.toString();
                    //判断接收到的词
                    if (chooseSearchWay(key)) {
                        //查询周边模糊词
                        searchSurround();
                    } else {
                        InputtipsQuery inputquery = new InputtipsQuery(s.toString(), currentCity);
                        Inputtips inputTips = new Inputtips(getActivity(), inputquery);
                        inputTips.setInputtipsListener(RouteFragment.this);
                        inputTips.requestInputtipsAsyn();
                    }
                } else {
                    //隐藏删除符
                    if (clear2.getVisibility() == View.VISIBLE) {
                        clear2.setVisibility(View.GONE);
                    }
                    //隐藏listView
                    if (mListView.getVisibility() == View.VISIBLE) {
                        mListView.setVisibility(View.GONE);
                    }
                    if (mtipAdapter != null && mCurrentTipList != null) {
                        mCurrentTipList.clear();
                        mtipAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        //三条路径规划视图
        routeLineOne = view.findViewById(R.id.route_line_one);
        routeLineTwo = view.findViewById(R.id.route_line_two);
        routeLineThree = view.findViewById(R.id.route_line_three);
        routeLineOne.setOnClickListener(this);
        routeLineTwo.setOnClickListener(this);
        routeLineThree.setOnClickListener(this);
        //三条视图的路径方案
        routeLineOneStrategy = view.findViewById(R.id.route_line_one_strategy);
        routeLineTwoStrategy = view.findViewById(R.id.route_line_two_strategy);
        routeLineThreeStrategy = view.findViewById(R.id.route_line_three_strategy);
        //三条视图的所用时长
        routeLineOneTime = view.findViewById(R.id.route_line_one_time);
        routeLineTwoTime = view.findViewById(R.id.route_line_two_time);
        routeLineThreeTime = view.findViewById(R.id.route_line_three_time);
        //三条路径的所行距离
        routeLineOneDistance = view.findViewById(R.id.route_line_one_distance);
        routeLineTwoDistance = view.findViewById(R.id.route_line_two_distance);
        routeLineThreeDistance = view.findViewById(R.id.route_line_three_distance);
        //三条路径的序号
        routeLineOneOrder = view.findViewById(R.id.route_line_one_order);
        routeLineTwoOrder = view.findViewById(R.id.route_line_two_order);
        routeLineThreeOrder = view.findViewById(R.id.route_line_three_order);
        //公交list
        mBusResultList = (ListView) view.findViewById(R.id.bus_result_list);
        mBusResultLayout = (LinearLayout) view.findViewById(R.id.bus_result);
    }

    private void dealWithClick(PoiItem poiItem) {
        //关闭键盘
        if(isFocusA) {
            closeEdit(startLocationName);
            clear1.setVisibility(View.GONE);
        }
        if(isFocusB){
            closeEdit(endLocationName);
            clear2.setVisibility(View.GONE);
        }
        //回调到主Activity去处理
        //封装
        String send = "";
        NaviLatLng naviLatLng = null;
        NaviBean naviBean = null;
        if(isFocusA) {
            send = "我要从" + poiItem.getTitle() + "去" + endName;
            isFocusA = false;
            naviLatLng = new NaviLatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
            naviBean = new NaviBean(poiItem.getTitle(),"",null,naviLatLng,null,null);
        }
        if(isFocusB){
            if(startName.equals("我的位置")){
                send = "我要去" + poiItem.getTitle();
            }else{
                send = "我要从" + startName + "去" + poiItem.getTitle();
            }
            naviLatLng = new NaviLatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
            naviBean = new NaviBean(null,"",poiItem.getTitle(),null,null,naviLatLng);
            isFocusB = false;
        }
        mListView.setFocusable(true);
        mListView.setFocusableInTouchMode(true);
        mListView.requestFocus();
        childCallBack.callFromRouteClick(send,naviBean,-1,"detailStyle","");
    }

    private void changeToDrive() {
        //==========================驾车变色======================================
        //设置背景
        driveClick.setBackgroundResource(R.drawable.button_shape_checked);
        drawable = getResources().getDrawable(R.mipmap.map_drive_icon_select);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        driveClick.setCompoundDrawables(drawable, null, null, null);
        driveClick.setTextColor(getResources().getColor(R.color.white));
        //==========================公交恢复======================================
        busClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_bus_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        busClick.setCompoundDrawables(drawable, null, null, null);
        busClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================步行恢复======================================
        walkClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_walk_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        walkClick.setCompoundDrawables(drawable, null, null, null);
        walkClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================骑行恢复======================================
        bikeClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_ride_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        bikeClick.setCompoundDrawables(drawable, null, null, null);
        bikeClick.setTextColor(getResources().getColor(R.color.detail_type_color));
    }

    private void changeToBus() {
        //==========================公交变色======================================
        //设置背景
        busClick.setBackgroundResource(R.drawable.button_shape_checked);
        drawable = getResources().getDrawable(R.mipmap.map_bus_icon_select);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        busClick.setCompoundDrawables(drawable, null, null, null);
        busClick.setTextColor(getResources().getColor(R.color.white));
        //==========================驾车恢复======================================
        driveClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_drive_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        driveClick.setCompoundDrawables(drawable, null, null, null);
        driveClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================步行恢复======================================
        walkClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_walk_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        walkClick.setCompoundDrawables(drawable, null, null, null);
        walkClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================骑行恢复======================================
        bikeClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_ride_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        bikeClick.setCompoundDrawables(drawable, null, null, null);
        bikeClick.setTextColor(getResources().getColor(R.color.detail_type_color));
    }

    private void changeToWalk() {
        //==========================步行变色======================================
        //设置背景
        walkClick.setBackgroundResource(R.drawable.button_shape_checked);
        drawable = getResources().getDrawable(R.mipmap.map_walk_icon_select);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        walkClick.setCompoundDrawables(drawable, null, null, null);
        walkClick.setTextColor(getResources().getColor(R.color.white));
        //==========================公交恢复======================================
        busClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_bus_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        busClick.setCompoundDrawables(drawable, null, null, null);
        busClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================驾车恢复======================================
        driveClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_drive_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        driveClick.setCompoundDrawables(drawable, null, null, null);
        driveClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================骑行恢复======================================
        bikeClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_ride_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        bikeClick.setCompoundDrawables(drawable, null, null, null);
        bikeClick.setTextColor(getResources().getColor(R.color.detail_type_color));
    }

    private void changeToBike() {
        //==========================骑行变色======================================
        //设置背景
        bikeClick.setBackgroundResource(R.drawable.button_shape_checked);
        drawable = getResources().getDrawable(R.mipmap.map_ride_icon_select);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        bikeClick.setCompoundDrawables(drawable, null, null, null);
        bikeClick.setTextColor(getResources().getColor(R.color.white));
        //==========================公交恢复======================================
        busClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_bus_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        busClick.setCompoundDrawables(drawable, null, null, null);
        busClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================步行恢复======================================
        walkClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_walk_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        walkClick.setCompoundDrawables(drawable, null, null, null);
        walkClick.setTextColor(getResources().getColor(R.color.detail_type_color));
        //==========================驾车恢复======================================
        driveClick.setBackground(new ColorDrawable());
        drawable = getResources().getDrawable(R.mipmap.map_drive_icon);
        //必须设置图片大小，否则不显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        driveClick.setCompoundDrawables(drawable, null, null, null);
        driveClick.setTextColor(getResources().getColor(R.color.detail_type_color));
    }

    private void hideOtherTypeLayout() {
        if (routeLineOne.getVisibility() == View.VISIBLE) {
            routeLineOne.setVisibility(View.GONE);
        }
        if (routeLineTwo.getVisibility() == View.VISIBLE) {
            routeLineTwo.setVisibility(View.GONE);
        }
        if (routeLineThree.getVisibility() == View.VISIBLE) {
            routeLineThree.setVisibility(View.GONE);
        }
    }

    /**
     * 改变为第几条路径被选中
     *
     * @param index 索引
     */
    private void changeToRoute(int index) {
        switch (index) {
            case 1:
                //序号变蓝
                routeLineOneOrder.setBackgroundResource(R.drawable.map_order_checked_btn);
                //标题变蓝
                routeLineOneStrategy.setTextColor(getResources().getColor(R.color.colorBlue));
                //耗时变蓝
                routeLineOneTime.setTextColor(getResources().getColor(R.color.colorBlue));
                //距离变蓝
                routeLineOneDistance.setTextColor(getResources().getColor(R.color.colorBlue));
                //其它恢复
                routeLineTwoOrder.setBackgroundResource(R.drawable.map_order_btn);
                routeLineTwoStrategy.setTextColor(getResources().getColor(R.color.detail_title_color));
                routeLineTwoTime.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineTwoDistance.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineThreeOrder.setBackgroundResource(R.drawable.map_order_btn);
                routeLineThreeStrategy.setTextColor(getResources().getColor(R.color.detail_title_color));
                routeLineThreeTime.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineThreeDistance.setTextColor(getResources().getColor(R.color.result_addr_color));
                break;
            case 2:
                //序号变蓝
                routeLineTwoOrder.setBackgroundResource(R.drawable.map_order_checked_btn);
                //标题变蓝
                routeLineTwoStrategy.setTextColor(getResources().getColor(R.color.colorBlue));
                //耗时变蓝
                routeLineTwoTime.setTextColor(getResources().getColor(R.color.colorBlue));
                //距离变蓝
                routeLineTwoDistance.setTextColor(getResources().getColor(R.color.colorBlue));
                //其它恢复
                routeLineOneOrder.setBackgroundResource(R.drawable.map_order_btn);
                routeLineOneStrategy.setTextColor(getResources().getColor(R.color.detail_title_color));
                routeLineOneTime.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineOneDistance.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineThreeOrder.setBackgroundResource(R.drawable.map_order_btn);
                routeLineThreeStrategy.setTextColor(getResources().getColor(R.color.detail_title_color));
                routeLineThreeTime.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineThreeDistance.setTextColor(getResources().getColor(R.color.result_addr_color));
                break;
            case 3:
                //序号变蓝
                routeLineThreeOrder.setBackgroundResource(R.drawable.map_order_checked_btn);
                //标题变蓝
                routeLineThreeStrategy.setTextColor(getResources().getColor(R.color.colorBlue));
                //耗时变蓝
                routeLineThreeTime.setTextColor(getResources().getColor(R.color.colorBlue));
                //距离变蓝
                routeLineThreeDistance.setTextColor(getResources().getColor(R.color.colorBlue));
                //其它恢复
                routeLineOneOrder.setBackgroundResource(R.drawable.map_order_btn);
                routeLineOneStrategy.setTextColor(getResources().getColor(R.color.detail_title_color));
                routeLineOneTime.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineOneDistance.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineTwoOrder.setBackgroundResource(R.drawable.map_order_btn);
                routeLineTwoStrategy.setTextColor(getResources().getColor(R.color.detail_title_color));
                routeLineTwoTime.setTextColor(getResources().getColor(R.color.result_addr_color));
                routeLineTwoDistance.setTextColor(getResources().getColor(R.color.result_addr_color));
                break;
            default:
                break;
        }
    }

    /**
     * 解析
     */
    private void parseData() {
        style = beanList.get(0).getStyle();
        switch (style) {
            case "WALK":
                changeToWalk();
                break;
            case "BIKE":
                changeToBike();
                break;
            case "DRIVE":
                changeToDrive();
                break;
            case "PUBTRANS":
                changeToBus();
                break;
        }
        parseResponse();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBusResultList!=null){
            mBusResultList= null;
        }
        if(mBusRouteResult!=null){
            mBusRouteResult=null;
        }
        if(beanList!=null){
            beanList.clear();
            beanList = null;
        }
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
    private void searchSurround() {
        query.setPageSize(7);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码
        //构造对象并发送检索
        if (currentLocation != null) {
            poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setOnPoiSearchListener(RouteFragment.this);
            if (isPoiSearch) {
                //设置搜索区域为以lp点为圆心，其周围5000米范围
                LatLonPoint latLonPoint = new LatLonPoint(currentLocation.latitude, currentLocation.longitude);
                poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 5000, true));
            }
            //异步搜索
            poiSearch.searchPOIAsyn();
        }
    }

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
                    NaviLatLng ll = new NaviLatLng(currentLocation.latitude,currentLocation.longitude);
                    mtipAdapter = new InputTipsAdapter(getContext(), ll, mCurrentTipList);
                    mListView.setAdapter(mtipAdapter);
                    mtipAdapter.notifyDataSetChanged();
                    Log.i("爱美丽", "进来了1: "+isFocusA+","+isFocusB);
                    if (mListView.getVisibility() != View.VISIBLE&&(isFocusA||isFocusB)) {
                        Log.i("爱美丽", "显示了1");
                        mListView.setVisibility(View.VISIBLE);
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
                            mItemsApter = new InputItemsAdapter(getActivity(), currentLocation, poiItems);
                            mListView.setAdapter(mItemsApter);
                            Log.i("爱美丽", "进来了2: ");
                            if (mListView.getVisibility() != View.VISIBLE&&(isFocusA||isFocusB)) {
                                Log.i("爱美丽", "显示了2");
                                mListView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    ToastUtil.show(getActivity(), R.string.map_no_result);
                }
            } else {
                ToastUtil.show(getActivity(), R.string.map_no_net);
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 失去焦点并关闭键盘
     **/
    private void closeEdit(View view) {
        //关闭键盘
        immanager.hideSoftInputFromWindow(view.getWindowToken(), 0);//隐藏键盘
    }
}
