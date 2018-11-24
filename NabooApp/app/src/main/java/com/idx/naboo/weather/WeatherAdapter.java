package com.idx.naboo.weather;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.idx.naboo.R;
import com.idx.naboo.weather.data.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 18-4-13.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>{
    private static final String TAG=WeatherAdapter.class.getSimpleName();
    private int mCurrentItem=0;

    private boolean isClick=false;
    private WeatherItemInterface mWeatherItemInterface;
    private List<Weather> mWeather=new ArrayList<>();
    public WeatherAdapter(List<Weather> mWeather){
        this.mWeather=mWeather;
    }

    @Override
    @Nullable
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_list,null);
        WeatherViewHolder viewHolder=new WeatherViewHolder(view, mWeatherItemInterface);
        viewHolder.weather_date=(TextView)view.findViewById(R.id.weather_tomorrow_time);
        viewHolder.weather_week=(TextView)view.findViewById(R.id.weather_tomorrow_week);
        viewHolder.weather_icon=(ImageView)view.findViewById(R.id.weather_tomorrow_icon);
        viewHolder.weather_temp=(TextView)view.findViewById(R.id.weather_tomorrow_temp);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {

        holder.weather_date.setText(HandlerWeatherUtil.parseDateLand(mWeather.get(position).getDate()));
        if (position==0){
            holder.weather_week.setText("今天");
        }else {
            holder.weather_week.setText(HandlerWeatherUtil.parseDateWeek(mWeather.get(position).getDate()));
        }
        holder.weather_temp.setText(mWeather.get(position).getWeatherDetail().getTemperature().getMin() + "℃ ~  "
                + mWeather.get(position).getWeatherDetail().getTemperature().getMax() + "℃");
        holder.weather_icon.setImageResource(HandlerWeatherUtil.getWeatherSmallIcon(
                Integer.parseInt(mWeather.get(position).getWeatherDetail().getWeatherStateCode().getCodeDay())));
        if (mCurrentItem==position && isClick){
            holder.weather_date.setTextColor(Color.parseColor("#ffffff"));
            holder.weather_week.setTextColor(Color.parseColor("#ffffff"));
            holder.weather_temp.setTextColor(Color.parseColor("#ffffff"));
        }else {
            holder.weather_date.setTextColor(Color.parseColor("#b3ffffff"));
            holder.weather_week.setTextColor(Color.parseColor("#b3ffffff"));
            holder.weather_temp.setTextColor(Color.parseColor("#b3ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return mWeather.size();
    }


    public class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //日期（xx月xx日）
        private TextView weather_date;
        //日期（周xx）
        private TextView weather_week;
        //温度
        private TextView weather_temp;
        //天气图标
        private ImageView weather_icon;

        public WeatherViewHolder(View itemView,WeatherItemInterface weatherItemInterface) {
            super(itemView);
            mWeatherItemInterface = weatherItemInterface;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mWeatherItemInterface.onItemClick(view,getPosition());
        }
    }

    public void  setWeatherOnItemClick(WeatherItemInterface weatherItemInterface){
        this.mWeatherItemInterface = weatherItemInterface;
    }
    public void setCurrentItem(int currentItem){
        this.mCurrentItem=currentItem;
    }
    public void setClick(boolean click){
        this.isClick=click;
    }
}
