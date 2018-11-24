package com.idx.naboo.map.adapter;

/**
 * Created by hayden on 18-1-17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.idx.naboo.R;
import com.idx.naboo.map.Interface.ChildCallBack;
import com.idx.naboo.map.bean.NaviBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 输入提示adapter，展示leftItem名称和地址
 */
public class LeftItemsAdapter extends BaseAdapter {
    private Context mContext;
    private List<PoiItem> mListItems;
    private LatLng currentLatlng;
    private LatLonPoint latLonPoint;
    private boolean isPoi;
    private ChildCallBack childCallBack;

    public LeftItemsAdapter(Context context, List<PoiItem> itemList,LatLng currentLat,boolean isPoiBack,ChildCallBack callBack) {
        mContext = context;
        mListItems = itemList;
        currentLatlng = currentLat;
        isPoi = isPoiBack;
        childCallBack = callBack;
    }

    @Override
    public int getCount() {
        if (mListItems != null) {
            return mListItems.size();
        }
        return 0;
    }


    @Override
    public Object getItem(int i) {
        if(mListItems != null){
           return mListItems.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.map_left_adapter,viewGroup,false);
            final Button makeRoute = view.findViewById(R.id.make_route);
            makeRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (Integer) holder.detail.getTag();
                    PoiItem poiItem = mListItems.get(i);
                    NaviLatLng endNavi = new NaviLatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
                    NaviBean naviBean = new NaviBean(mContext.getResources().getString(R.string.map_location),"",poiItem.getTitle(),null,null,endNavi);
                    childCallBack.callFromDetail(false,naviBean,i);
                }
            });
            Button checkDetail = view.findViewById(R.id.check_detail);
            holder.detail = checkDetail;
            checkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (Integer) holder.detail.getTag();
                    //回调给Activity
                    childCallBack.callFromLeftAdapter(i);
                }
            });
            if(isPoi){
                if(checkDetail.getVisibility()==View.VISIBLE){
                    checkDetail.setVisibility(View.GONE);
                }
            }else {
                if(checkDetail.getVisibility()==View.GONE){
                    checkDetail.setVisibility(View.VISIBLE);
                }
            }
            holder.mOrder = (TextView) view.findViewById(R.id.map_order);
            holder.mName = (TextView) view.findViewById(R.id.map_title);
            holder.mAddress = (TextView) view.findViewById(R.id.map_address);
            holder.mDistance = (TextView) view.findViewById(R.id.map_distance);
            view.setTag(holder);
        } else{
            holder = (Holder)view.getTag();
        }
        if(mListItems == null){
            return view;
        }
        //赋值
        //序号
        holder.mOrder.setText(String.valueOf(position+1));
        //标题
        holder.mName.setText(mListItems.get(position).getTitle());
        //地址
        String address = mListItems.get(position).getSnippet();
        if(address == null || address.equals("")){
            holder.mAddress.setVisibility(View.GONE);
        }else{
            holder.mAddress.setVisibility(View.VISIBLE);
            holder.mAddress.setText(address);
        }
        //距离
        //位置处理
        try {
            float distance = AMapUtils.calculateLineDistance(currentLatlng, new LatLng(mListItems.get(position).getLatLonPoint().getLatitude(), mListItems.get(position).getLatLonPoint().getLongitude()));
            if (distance > 1000) {
                double dis = (double) distance / 1000;
                BigDecimal b = new BigDecimal(dis);
                double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.mDistance.setText("" + f1 + "km");
            } else {
                BigDecimal b = new BigDecimal(distance);
                double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.mDistance.setText("" + f1 + "m");
            }
            holder.detail.setTag(position);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return view;
    }

    class Holder {
        TextView mOrder;
        TextView mName;
        TextView mAddress;
        TextView mDistance;
        Button detail;
    }
}