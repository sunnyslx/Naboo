package com.idx.naboo.dish.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idx.naboo.R;

import java.util.List;

/**
 * Created by sunny on 18-5-7.
 */

public class MaterialAdapter extends BaseAdapter {

    private List<String> mMaterial;

    public MaterialAdapter(List<String> mMaterial){
        this.mMaterial=mMaterial;
    }

    @Override
    public int getCount() {
        return mMaterial.size();
    }

    @Override
    public String getItem(int i) {
        return mMaterial.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MaterialHolder materialHolder=null;
        if (view==null){
            materialHolder=new MaterialHolder();
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dish_grideview_item,null);
            materialHolder.material=view.findViewById(R.id.dish_material);
            view.setTag(materialHolder);
        }else {
            materialHolder=(MaterialHolder)view.getTag();
        }
        materialHolder.material.setText(mMaterial.get(i));
        return view;
    }

    private class MaterialHolder{

        private TextView material;
    }
}
