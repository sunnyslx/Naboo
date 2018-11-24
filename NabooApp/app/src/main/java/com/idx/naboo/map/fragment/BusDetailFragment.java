package com.idx.naboo.map.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.idx.naboo.R;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.Interface.ParentCallBack;
import com.idx.naboo.map.adapter.BusSegmentListAdapter;
import com.idx.naboo.map.bean.NaviBean;
import com.idx.naboo.map.bean.RouteMessageBean;
import com.idx.naboo.map.mapUtils.AMapUtil;

import java.util.List;

/**
 * Created by hayden on 18-4-17.
 */

public class BusDetailFragment extends Fragment implements ParentCallBack {

    private BusPath mBuspath;
    private BusRouteResult mBusRouteResult;
    private TextView mStartPointName,mEndPointName,mBusDistance, mBusTime,mBusPay;
    private ListView mBusSegmentList;
    private BusSegmentListAdapter mBusSegmentListAdapter;
    private String startName;
    private String endName;
    private ImageButton back;
    private ChildCallBack childCallBack;

    public static Fragment newInstance(BusPath busPath, BusRouteResult busRouteResult, String startName, String endName){
        BusDetailFragment fragment = new BusDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("busPath",busPath);
        bundle.putParcelable("busRoute",busRouteResult);
        bundle.putString("startName",startName);
        bundle.putString("endName",endName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBuspath = getArguments().getParcelable("busPath");
        mBusRouteResult = getArguments().getParcelable("busRoute");
        startName = getArguments().getString("startName");
        endName = getArguments().getString("endName");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof ChildCallBack){
            childCallBack = (ChildCallBack) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_bus_detail_fragment,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        //起点
        mStartPointName = (TextView)view.findViewById(R.id.my_location_name);
        //终点
        mEndPointName = (TextView)view.findViewById(R.id.end_location_name);
        //耗时
        mBusTime = (TextView) view.findViewById(R.id.bus_time);
        //距离
        mBusDistance = (TextView) view.findViewById(R.id.bus_distance);
        //打车费
        mBusPay = (TextView) view.findViewById(R.id.bus_spent);
        //填充公交信息
        mBusSegmentList = (ListView) view.findViewById(R.id.bus_segment_list);
        //返回键
        back = view.findViewById(R.id.map_bus_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知Activity去隐藏这个fragment
                childCallBack.callFromBus();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //在这儿解析显示
        initData();
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

    }

    @Override
    public void callRouteChangeList(String startName, String endName, List<RouteMessageBean> beanList) {

    }

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

    /**
     * 回调过来更新数据
     * @param busPath 选择的路线
     * @param busRouteResult 总结果集
     * @param endNameBack 终点名称
     */
    @Override
    public void callBusDetailChange(BusPath busPath, BusRouteResult busRouteResult, String startNameBack, String endNameBack) {
        mBuspath = busPath;
        mBusRouteResult = busRouteResult;
        startName = startNameBack;
        endName = endNameBack;
        initData();
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

    /**
     * 数据更新操作
     */
    private void initData() {
        //设置起点数据
        mStartPointName.setText(startName);
        //设置终点数据
        mEndPointName.setText(endName);
        //设置所用时间
        String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
        mBusTime.setText("用时："+dur+"  |  ");
        //设置总距离
        String dis = AMapUtil.getFriendlyLength((int) mBuspath.getDistance());
        mBusDistance.setText(dis+"  |  ");
        //设置打车费
        int taxiCost = (int) mBusRouteResult.getTaxiCost();
        mBusPay.setText("打车约" + taxiCost + "元"+"  ");
        //公交数据更新
        mBusSegmentListAdapter = new BusSegmentListAdapter(getActivity(), mBuspath.getSteps());
        mBusSegmentList.setAdapter(mBusSegmentListAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBuspath!=null){
            mBuspath = null;
        }
        if(mBusRouteResult!=null){
            mBusRouteResult = null;
        }
        if(mBusSegmentList!=null){
            mBusSegmentList = null;
        }
    }
}