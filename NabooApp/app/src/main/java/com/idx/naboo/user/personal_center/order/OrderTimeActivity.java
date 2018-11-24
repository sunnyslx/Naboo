package com.idx.naboo.user.personal_center.order;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.home.HomeActivity;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.user.hx.EaseLoginActivity;
import com.idx.naboo.user.iom.login.LoginActivity;
import com.idx.naboo.user.personal_center.order.orderbean.Order_list;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;
import com.idx.naboo.user.personal_center.order.orderbean.Reply;
import com.idx.naboo.user.personal_center.order.orderbean.Root;
import com.idx.naboo.user.personal_center.order.recyclerView.MyRecyclerView;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import java.util.List;

/**
 * Created by ryan on 18-5-11.
 * Email: Ryan_chan01212@yeah.net
 */

public class OrderTimeActivity extends BaseActivity {
    private static final String TAG = "OrderTimeActivity";
    private IService mIService;
    private Bitmap mBitmap_toolbar;
    private MyRecyclerView adapter;
    private TextView nobody_order;
    private RecyclerView order_recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String json;
    private Gson gson;
    private TextView all_order_textview;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private List<Orders> orders;


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(OrderTimeActivity.this);

            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())) {
                Log.i("OrderTimeActivity", "onServiceConnected: 进入到Json获取");
                json = mIService.getJson();
                sharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());
                JsonData jsonUtil = JsonUtil.createJsonData(json);
                String type = jsonUtil.getType();

                if (type.equals("order_list")) {
                     gson = new Gson();
                    Root orderRoot = gson.fromJson(json, Root.class);
                    Reply reply = orderRoot.getData().getContent().getReply();
                    //訂單的列表
                    List<Order_list> order_list = reply.getOrder_list();
                    for (Order_list order : order_list) {
                        //遍历所有的得到订单
                        List<Orders> orders = order.getOrders();
                        if (!orders.isEmpty()) {//判断订单List 是否为空
                            Log.d(TAG, "onJsonReceived: 订单信息");
                            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            order_recyclerView.setLayoutManager(linearLayoutManager);
                            adapter = new MyRecyclerView(getBaseContext(),orders);
                            order_recyclerView.setAdapter(adapter);
                            nobody_order.setVisibility(View.GONE);
                            order_recyclerView.setVisibility(View.VISIBLE);
                            sharedPreferencesUtil.saveUUID("mJson", json);
                            adapter.setOnItemClickListener(new MyRecyclerView.OnItemClickListener() {
                                @Override
                                public void onClick(int position) {
                                    Intent intent = new Intent(OrderTimeActivity.this, OrderDetailsActivity.class);//你的fragment是
                                    intent.putExtra("position", String.valueOf(position));
                                    sharedPreferencesUtil.saveUUID("OrderTime", "OrderTime");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Log.d(TAG, "onJsonReceived: 订单信息为空");
                            nobody_order.setVisibility(View.VISIBLE);
                            order_recyclerView.setVisibility(View.GONE);
                        }
                    }
                }
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);

        if (type.equals("back")){
            //startActivity(new Intent(OrderTimeActivity.this,HomeActivity.class));
            finish();
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
        }

        if (type.equals("order_list")) {

            Root orderRoot = gson.fromJson(json, Root.class);
            Reply reply = orderRoot.getData().getContent().getReply();
            //訂單的列表
            List<Order_list> order_list = reply.getOrder_list();
            for (Order_list order : order_list) {
                //遍历所有的得到订单
                orders = order.getOrders();
                if (!orders.isEmpty()) {//判断订单List 是否为空
                    Log.d(TAG, "onJsonReceived: 订单信息");
                    updateList();
                    nobody_order.setVisibility(View.GONE);
                    order_recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "onJsonReceived: 订单信息为空");
                    nobody_order.setVisibility(View.VISIBLE);
                    order_recyclerView.setVisibility(View.GONE);
                }
            }
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
        }


        }




    private void updateList(){
        linearLayoutManager = new LinearLayoutManager(this);
        order_recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyRecyclerView(this,orders);
        order_recyclerView.setAdapter(adapter);
    }

    private void clearList(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_time_activity);
        initView();
        initToolbar();
    }

    private void initView() {
        nobody_order = findViewById(R.id.nobody_order);
        order_recyclerView = findViewById(R.id.order_recyclerView);
        gson = new Gson();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("订单信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.decodeBitmapFromResources(this, R.drawable.path);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
        all_order_textview = findViewById(R.id.toolbar_textview);
        all_order_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //现在这里查询 uuid的值是否存在
                sharedPreferencesUtil.saveUUID("Order","Order");
                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))){
                    startActivity(new Intent(OrderTimeActivity.this, LoginActivity.class));
//                    finish();
                    return;
                }
                if (!EMClient.getInstance().isLoggedInBefore()){
                    startActivity(new Intent(OrderTimeActivity.this,EaseLoginActivity.class));
//                    finish();
                    return;
                }

                //获取所有的订单 json
                mIService.requestData("所有的订单", new NLIRequest.onRequest() {
                    @Override
                    public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                        json = s;
                        JsonData jsonUtil = JsonUtil.createJsonData(s);
                        String type = jsonUtil.getType();
                        Log.d(TAG, "onJsonReceived: "+ type);
                        if (type.equals("order_list")) {
                            Root orderRoot = gson.fromJson(s, Root.class);
                            Reply reply = orderRoot.getData().getContent().getReply();

                            //訂單的列表
                            List<Order_list> order_list = reply.getOrder_list();
                            for (Order_list order : order_list) {
                                //遍历所有的得到订单
                                List<Orders> orders = order.getOrders();
                                if (!orders.isEmpty()) {//判断订单List 是否为空
                                    Log.d(TAG, "onJsonReceived: 订单信息");
                                    //保存json数据
                                    sharedPreferencesUtil.saveUUID("mJson",json);
                                    startActivity(new Intent(OrderTimeActivity.this,OrderActivity.class));

                                } else {
                                    Log.d(TAG, "onJsonReceived: 订单信息为空");
                                    sharedPreferencesUtil.saveUUID("mJson","");
                                    startActivity(new Intent(OrderTimeActivity.this,OrderActivity.class));
                                }
                            }
                        }
                        sendBroadcast(new Intent(Execute.ACTION_SUCCESS));

                    }

                    @Override
                    public void onError() {

                    }
                });
//                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //startActivity(new Intent(OrderTimeActivity.this, HomeActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(OrderTimeActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }
}
