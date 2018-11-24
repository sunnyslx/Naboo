package com.idx.naboo.map.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.idx.naboo.R;
import com.idx.naboo.map.Interface.BusRouteListener;
import com.idx.naboo.map.mapUtils.AMapUtil;

import java.util.List;

public class BusResultListAdapter extends BaseAdapter {
	private Context mContext;
	private List<BusPath> mBusPathList;
	private BusRouteResult mBusRouteResult;
	private BusRouteListener mBusRouteListener;
	private String startName,endName;

	public BusResultListAdapter(Context context, BusRouteResult busrouteresult, BusRouteListener listener, String start, String end) {
		mContext = context;
		mBusRouteResult = busrouteresult;
		mBusPathList = busrouteresult.getPaths();
		mBusRouteListener = listener;
		startName = start;
		endName = end;
	}
	
	@Override
	public int getCount() {
		return mBusPathList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBusPathList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.map_item_bus_result, null);
			holder.title = (TextView) convertView.findViewById(R.id.bus_path_title);
			holder.des = (TextView) convertView.findViewById(R.id.bus_path_des);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final BusPath item = mBusPathList.get(position);
		holder.title.setText(AMapUtil.getBusPathTitle(item));
		holder.des.setText(AMapUtil.getBusPathDes(item));
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBusRouteListener.clickItem(position,mBusPathList.get(position),mBusRouteResult,startName,endName);
			}
		});
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView title;
		TextView des;
	}

}
