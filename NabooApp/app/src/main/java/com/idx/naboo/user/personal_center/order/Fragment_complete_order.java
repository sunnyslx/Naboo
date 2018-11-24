package com.idx.naboo.user.personal_center.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idx.naboo.R;
import com.idx.naboo.user.personal_center.order.recyclerView.MyRecyclerView;

/**
 * Created by ryan on 18-4-16.
 * Email: Ryan_chan01212@yeah.net
 */

public class Fragment_complete_order extends Fragment {


    View view;
    private RecyclerView recyclerView;
    private MyRecyclerView adapter;
    private static final String TAG = "Fragment_complete_order";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_complete_order,container,false);
        initView();
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null){
            Log.d(TAG, "onCreateView: " + bundle.get("str"));
        }
        return view;
    }
    private void initView() {
        recyclerView = view.findViewById(R.id.recyclerView_complete_order);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }



}
