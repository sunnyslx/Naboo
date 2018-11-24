package com.idx.naboo.map.adapter;

/**
 * Created by hayden on 18-1-17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.help.Tip;
import com.idx.naboo.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * 输入提示adapter，展示Tips名称和地址
 */
public class InputTipsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Tip> mListTips;
    private NaviLatLng startNavi;

    public InputTipsAdapter(Context context, NaviLatLng startNaviBack, List<Tip> tipList) {
        mContext = context;
        startNavi = startNaviBack;
        mListTips = tipList;
    }

    @Override
    public int getCount() {
        if (mListTips != null) {
            return mListTips.size();
        }
        return 0;
    }


    @Override
    public Object getItem(int i) {
        if (mListTips != null) {
            return mListTips.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.map_adapter_inputtips, null);
            holder.mName = (TextView) view.findViewById(R.id.name);
            holder.mAddress = (TextView) view.findViewById(R.id.adress);
            holder.mDistance = (TextView) view.findViewById(R.id.distance);
            view.setTag(holder);
        } else{
            holder = (Holder)view.getTag();
        }
        if(mListTips == null){
            return view;
        }

        //设置名称
        holder.mName.setText(mListTips.get(i).getName());
        //设置地址
        String address = mListTips.get(i).getAddress();
        if(address == null || address.equals("") || address.trim().equals("")){
            holder.mAddress.setVisibility(View.INVISIBLE);
        }else{
            holder.mAddress.setVisibility(View.VISIBLE);
            holder.mAddress.setText(address);
        }
        //设置距离
        try {
            LatLng start = new LatLng(startNavi.getLatitude(), startNavi.getLongitude());
            LatLng end = new LatLng(mListTips.get(i).getPoint().getLatitude(), mListTips.get(i).getPoint().getLongitude());
            float distance = AMapUtils.calculateLineDistance(start, end);
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
        }catch (NullPointerException e){
            e.printStackTrace();
            holder.mDistance.setText("未知");
        }
        return view;
    }

    class Holder {
        TextView mName;
        TextView mAddress;
        TextView mDistance;
    }
}