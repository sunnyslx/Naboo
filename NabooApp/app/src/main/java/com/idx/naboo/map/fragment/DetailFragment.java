package com.idx.naboo.map.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idx.naboo.NabooActions;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.Interface.ParentCallBack;
import com.idx.naboo.map.adapter.MyImgAdapter;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;
import com.idx.naboo.map.restaurant.ImoranRestaurantResponse;
import com.idx.naboo.map.restaurant.Restaurant;
import com.idx.naboo.map.viewspot.ImoranViewResponse;
import com.idx.naboo.map.viewspot.ViewSpot;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hayden on 18-4-17.
 */

public class DetailFragment extends Fragment implements ParentCallBack {

    private ImoranRestaurantResponse restaurantResponse;
    private ImoranViewResponse viewResponse;
    private Gson gson;
    private ChildCallBack childCallBack;
    private TextView resTitle,resType,resDistance,resAddress,resPhone,resPrice;
    private RecyclerView resImagesList;
    private RatingBar resRating;
    private LatLng currentLatlng;
    private MyImgAdapter adapter;
    private Button routeMake;
    private Restaurant restaurant;
    private ViewSpot viewSpot;
    private String type;

    public static Fragment newInstance(String json,String locBack){
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("json",json);
        bundle.putString("current_latlng",locBack);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String json = getArguments().getString("json");
        gson = new Gson();
        dealWithJson(json);
        String currentString = getArguments().getString("current_latlng");
        currentLatlng = gson.fromJson(currentString, new TypeToken<LatLng>() {
        }.getType());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof ChildCallBack){
            childCallBack = (ChildCallBack) getActivity();
        }
        //定义一个Adapter
        adapter = new MyImgAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_resdetail_fragment,container,false);
        //获取相关控件
        initView(view);
        return view;
    }

    /**
     * 获取相关控件
     */
    private void initView(View view) {
        //返回箭头
        ImageButton back = view.findViewById(R.id.map_toLeft_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知主视图改变fragment的显示
                childCallBack.callFromDetail(true,null,-1);
            }
        });
        //餐厅名称
        resTitle = view.findViewById(R.id.res_title);
        //餐厅评分
        resRating = view.findViewById(R.id.res_rating);
        //餐厅类型
        resType = view.findViewById(R.id.res_type);
        //餐厅距离
        resDistance = view.findViewById(R.id.res_distance);
        //餐厅地址
        resAddress = view.findViewById(R.id.res_address);
        //餐厅电话
        resPhone = view.findViewById(R.id.res_phone);
        //餐厅人均
        resPrice = view.findViewById(R.id.res_price);
        //餐厅图片
        resImagesList = view.findViewById(R.id.res_imgList);
        //创建LinearLayoutManager 对象
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //设置RecyclerView 布局
        resImagesList.setLayoutManager(layoutmanager);
        //路径规划
        routeMake = view.findViewById(R.id.make_route);
        routeMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaviBean naviBean = null;
                if(type.equals("restaurant")){
                    NaviLatLng endLatLng = new NaviLatLng(restaurant.getLatitude(),restaurant.getLongitude());
                    naviBean = new NaviBean(getResources().getString(R.string.map_location),"",restaurant.getName(),null,null,endLatLng);
                }else{
                    NaviLatLng endLatLng = new NaviLatLng(viewSpot.getLatitude(),viewSpot.getLongitude());
                    naviBean = new NaviBean(getResources().getString(R.string.map_location),"",viewSpot.getName(),null,null,endLatLng);
                }
                childCallBack.callFromDetail(false,naviBean,-1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //在这儿解析显示
        if(type.equals("restaurant")){
            parseResJson();
        }else if(type.equals("viewspot")){
            parseViewJson();
        }
    }

    /**
     * 解析传过来的餐厅Response
     */
    private void parseResJson() {
        //获取餐厅对象
        restaurant = restaurantResponse.getData().getContent().getReply().getLocationList().get(0);
        Log.i("aa", "parseResJson: "+restaurantResponse.getData().getContent().getReply().getLocationList().get(0).getImg_url());
        //设置餐厅名称
        resTitle.setText(restaurant.getName());
        //设置餐厅评分
        resRating.setRating((float) restaurant.getRating());
        //设置餐厅类型
        resType.setText(restaurant.getFood_type());
        //设置餐厅距离
        float distance = AMapUtils.calculateLineDistance(currentLatlng,new LatLng(restaurant.getLatitude(),restaurant.getLongitude()));
        if (distance > 1000) {
            double dis = (double) distance / 1000;
            BigDecimal b = new BigDecimal(dis);
            double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            resDistance.setText("" + f1 +"km");
        } else {
            BigDecimal b = new BigDecimal(distance);
            double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            resDistance.setText("" + f1 +"m");
        }
        //设置餐厅地址
        resAddress.setText(restaurant.getAddress());
        //设置餐厅电话
        resPhone.setText(restaurant.getTelephone());
        //设置餐厅人均消费
        resPrice.setText("人均"+restaurant.getPrice()+"元");
        //设置参数
        adapter.setInfoList(restaurant.getDishInfoList(),0);
        //设置Adapter
        resImagesList.setAdapter(adapter);
        //通知
        adapter.notifyDataSetChanged();
    }

    /**
     * 解析传过来的景点Response
     */
    private void parseViewJson() {
        //获取景点对象
        viewSpot = viewResponse.getData().getContent().getReply().getViewSpotList().get(0);
        //设置景点名称
        resTitle.setText(viewSpot.getName());
        //设置景点评分
        resRating.setRating((float) viewSpot.getRating());
        //设置景点级别
        if(viewSpot.getGrade()<2){
            resType.setText(getResources().getString(R.string.map_oneview));
        }else if(2<=viewSpot.getGrade()&&viewSpot.getGrade()<3){
            resType.setText(getResources().getString(R.string.map_twoview));
        }else if(3<=viewSpot.getGrade()&&viewSpot.getGrade()<4){
            resType.setText(getResources().getString(R.string.map_threeview));
        }else if(4<=viewSpot.getGrade()&&viewSpot.getGrade()<5){
            resType.setText(getResources().getString(R.string.map_fourview));
        }else{
            resType.setText(getResources().getString(R.string.map_fiveview));
        }
        //设置景点距离
        //位置处理
        float distance = AMapUtils.calculateLineDistance(currentLatlng,new LatLng(viewSpot.getLatitude(),viewSpot.getLongitude()));
        if (distance > 1000) {
            double dis = (double) distance / 1000;
            BigDecimal b = new BigDecimal(dis);
            double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            resDistance.setText("" + f1 +"km");
        } else {
            BigDecimal b = new BigDecimal(distance);
            double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            resDistance.setText("" + f1 +"m");
        }
        //设置餐厅地址
        resAddress.setText(viewSpot.getAddress());
        //设置餐厅电话
        if(viewSpot.getTelephone().equals("")){
            resPhone.setText(getResources().getString(R.string.map_nophone));
        }else{
            resPhone.setText(viewSpot.getTelephone());
        }
        //设置餐厅人均消费
        resPrice.setText(getResources().getString(R.string.map_priceperson));
        //设置参数
        adapter.setViewImgs(viewSpot.getImages(),1);
        //设置Adapter
        resImagesList.setAdapter(adapter);
        //通知
        adapter.notifyDataSetChanged();
    }

    @Override
    public void callMapMarkPoint(boolean isVoice, String json, int position) {
    }

    @Override
    public void callMapDrawRoute(NaviBean naviBean, int position, String wayStyle, String tts) {

    }

    @Override
    public void callLeftChangeList(List<PoiItem> poiItemList, String key, boolean isPoi) {

    }

    @Override
    public void callDetailChangeList(String json) {
        dealWithJson(json);
        if(type.equals("restaurant")){
            parseResJson();
        }else if (type.equals("viewspot")){
            parseViewJson();
        }
    }

    /**
     * 用来处理是哪种类型进来了
     * @param json 传递的json
     */
    private void dealWithJson(String json) {
        Log.i("parseJson", "dealWithJson: ");
        JsonData jsonData = JsonUtil.createJsonData(json);
        if(jsonData!=null) {
            type = jsonData.getType();
            if (jsonData.getDomain().equals(NabooActions.Map.TARGET_RESTAURANT) && jsonData.getType().equals("restaurant")) {
                restaurantResponse = gson.fromJson(json, ImoranRestaurantResponse.class);
            } else if (jsonData.getDomain().equals(NabooActions.Map.TARGET_VIEWSPOT) && jsonData.getType().equals("viewspot")) {
                viewResponse = gson.fromJson(json,ImoranViewResponse.class);
            }
        }
    }

    @Override
    public void callRouteChangeList(String startName, String endName, List<RouteMessageBean> beanList) {}

    @Override
    public void callMapChooseRouteNum(int index) {

    }

    @Override
    public void callMapChooseRouteStyle(String style) {

    }

    @Override
    public void callBusRouteChange(String start,String end,BusRouteResult result) {

    }

    @Override
    public void callMapToNavi() {

    }

    @Override
    public void callRouteChangeTextColor(int index) {

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
    public void onDestroy() {
        super.onDestroy();
        if(restaurantResponse!=null){
            restaurantResponse = null;
        }
        if(viewResponse!=null){
            viewResponse=null;
        }
        if(resImagesList!=null){
           resImagesList = null;
        }
        if(currentLatlng!=null){
            currentLatlng = null;
        }
        if(restaurant!=null){
            restaurant=null;
        }
    }
}
