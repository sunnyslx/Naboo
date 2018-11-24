package com.idx.naboo.map.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idx.naboo.R;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.Interface.ParentCallBack;
import com.idx.naboo.map.adapter.LeftItemsAdapter;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;

import java.util.List;

/**
 * Created by hayden on 18-4-17.
 */

public class LeftFragment extends Fragment implements ParentCallBack {

    private List<PoiItem> poiItems;
    private LeftItemsAdapter leftItemsAdapter;
    private ListView listView;
    private Gson gson;
    private LatLng currentLatlng;
    private TextView mSearchText;
    private String key;
    private ChildCallBack childCallBack;

    public static Fragment newInstance(String poiList,String currentLocation,String key, boolean isPoi){
        LeftFragment fragment = new LeftFragment();
        Bundle bundle = new Bundle();
        bundle.putString("poiList", poiList);
        bundle.putString("currentLatlng", currentLocation);
        bundle.putString("key",key);
        bundle.putBoolean("isPoi",isPoi);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        gson = new Gson();
        //拿到判断
        String poiItem = getArguments().getString("poiList");
        //取出地图传入的数据
        poiItems = gson.fromJson(poiItem,new TypeToken<List<PoiItem>>() {}.getType());
        String currentL = getArguments().getString("currentLatlng");
        key = getArguments().getString("key");
        currentLatlng = gson.fromJson(currentL,new TypeToken<LatLng>(){}.getType());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof ChildCallBack){
            childCallBack = (ChildCallBack) getActivity();
        }
        leftItemsAdapter = new LeftItemsAdapter(getContext(),poiItems,currentLatlng,getArguments().getBoolean("isPoi"),childCallBack);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_left_fragment,container,false);
        listView = view.findViewById(R.id.poi_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                childCallBack.callFromLeft(false,position);
            }
        });
        listView.setAdapter(leftItemsAdapter);
        mSearchText = view.findViewById(R.id.result_text);
        mSearchText.setText(key);
        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childCallBack.callFromLeft(true,-1);
            }
        });
        if(listView.getVisibility()==View.GONE){
            listView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void callMapMarkPoint(boolean isVoice, String json, int position) {
    }

    @Override
    public void callMapDrawRoute(NaviBean naviBean, int position, String wayStyle, String tts) {
    }

    @Override
    public void callLeftChangeList(List<PoiItem> pois, String key, boolean isPoi) {
        if(pois!=null) {
            poiItems = pois;
            //更新listView
            leftItemsAdapter = new LeftItemsAdapter(getContext(), poiItems, currentLatlng, isPoi, childCallBack);
            //设置
            listView.setAdapter(leftItemsAdapter);
            //显示视图
            mSearchText.setText(key);
        }
    }

    @Override
    public void callDetailChangeList(String json) {

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
    public void callBusRouteChange(String start,String endName, BusRouteResult result) {

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
        if(poiItems!=null){
            poiItems.clear();
        }
        if(listView!=null){
            listView = null;
        }
        if(currentLatlng!=null){
            currentLatlng=null;
        }
    }
}
