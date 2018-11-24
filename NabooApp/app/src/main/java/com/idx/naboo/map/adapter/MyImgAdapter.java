package com.idx.naboo.map.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.idx.naboo.R;
import com.idx.naboo.map.restaurant.DishInfo;
import com.idx.naboo.utils.RoundImageUtils;

import java.util.List;

/**
 * Created by hayden on 18-4-23.
 */
public class MyImgAdapter extends RecyclerView.Adapter{

    private List<DishInfo> infoList;
    private Context mContext;
    private String[] viewImgs;
    private int length;
    private int flag;

    public MyImgAdapter(Context context){
        mContext = context;
    }

    public void setInfoList(List<DishInfo> list,int flagBack){
        infoList = list;
        length = list.size();
        flag = flagBack;
    }

    public void setViewImgs(String[] imgs,int flagBack){
        viewImgs = imgs;
        length = imgs.length;
        flag = flagBack;
    }

    @Override
    public int getItemCount() {
        return length;
    }

    @Override
    // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    public MyHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.map_detail_imgs,null);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    // 填充onCreateViewHolder方法返回的holder中的控件
    public void onBindViewHolder(RecyclerView.ViewHolder holderBack, int position) {
        MyHolder holder = (MyHolder)holderBack;
        if (flag==0){
            Glide.with(mContext).load(infoList.get(position).getImgUrl())
                    .transform(new RoundImageUtils(mContext,10))
                    .into(holder.imageView);
        }else if(flag==1){
            Glide.with(mContext).load(viewImgs[position])
                    .transform(new RoundImageUtils(mContext,10))
                    .into(holder.imageView);
        }
    }

    // 定义内部类继承ViewHolder
    class MyHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        private MyHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.res_img);
        }

    }
}
