package com.idx.naboo.user.personal_center.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.user.personal_center.order.orderbean.Order_list;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;
import com.idx.naboo.user.personal_center.order.orderbean.Reply;
import com.idx.naboo.user.personal_center.order.orderbean.Root;
import com.idx.naboo.user.personal_center.order.recyclerView.NotOrderRecyclerView;
import com.idx.naboo.utils.SharedPreferencesUtil;

import java.util.List;

/**
 * Created by ryan on 18-4-16.
 * Email: Ryan_chan01212@yeah.net
 */

public class Fragment_not_order extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private NotOrderRecyclerView adapter;
    private Gson gson;
    private String mJson;
    private static final String TAG = "Fragment_not_order";
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_not_order, container, false);
        initView();


        mJson = sharedPreferencesUtil.getUUID("mJson");
        if (mJson != null) {
            if (JsonUtil.createJsonData(mJson) != null) {
                JsonData jsonUtil = JsonUtil.createJsonData(mJson);
                String type = jsonUtil.getType();
                Log.d(TAG, "onJsonReceived: " + type);
                if (type.equals("order_list")) {
                    Root orderRoot = gson.fromJson(mJson, Root.class);
                    Reply reply = orderRoot.getData().getContent().getReply();

                    //訂單的列表
                    List<Order_list> order_list = reply.getOrder_list();
                    for (Order_list order : order_list) {
                        //遍历所有的得到订单
                        List<Orders> orders = order.getOrders();
                        if (!orders.isEmpty()) {//判断订单List 是否为空
                            Log.d(TAG, "onJsonReceived: 订单信息");
                            adapter = new NotOrderRecyclerView(getActivity(),orders);
                            adapter.setOnItemClickListener(new NotOrderRecyclerView.OnItemClickListener() {
                                @Override
                                public void onClick(int position) {
                                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);//你的fragment是
                                    intent.putExtra("position", String.valueOf(position));
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "onJsonReceived: 订单信息为空");
                        }
                    }
                }
            }
        }

        return view;
    }

    private void initView() {
        sharedPreferencesUtil = new SharedPreferencesUtil(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView_not_order);
        gson = new Gson();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }
}
