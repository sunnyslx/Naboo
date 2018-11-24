package com.idx.naboo.user.personal_center.order;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.user.personal_center.Personal_center;
import com.idx.naboo.user.personal_center.order.orderbean.Order_list;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;
import com.idx.naboo.user.personal_center.order.orderbean.Reply;
import com.idx.naboo.user.personal_center.order.orderbean.Root;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends BaseActivity {

    private static final String TAG = "OrderActivity";
    private Bitmap mBitmap_toolbar;
    private ViewPager viewPager;
    private TextView all_orders;
    private TextView not_orders;
    private TextView complete_orders;
    private List<Fragment> fragmentList;
    private List<TextView> list;
    private Fragment_all_order f1;
    private Fragment_not_order f2;
    private Fragment_complete_order f3;
    private FragmentManager fm;
    private OrderPagerAdapter orderPagerAdapter;
    private IService mIService;
    private LinearLayout line_order;
    private LinearLayout nobodyOrder;
    private Gson gson;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener( OrderActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Bundle bundle;
    private SharedPreferencesUtil sharedPreferencesUtil;


    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(OrderActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(conn);
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("back")){
            if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("Order"))){
                sharedPreferencesUtil.saveUUID("Order","");
//                startActivity(new Intent(OrderActivity.this, OrderTimeActivity.class));
                finish();
            }else {
                startActivity(new Intent(OrderActivity.this, Personal_center.class));
                finish();
            }
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_order);
        initToolbar();
        initView();
    }

    private void initView() {
        line_order = findViewById(R.id.line_order_information);
        viewPager = findViewById(R.id.viewpager);
        all_orders = findViewById(R.id.all_orders);
        not_orders = findViewById(R.id.not_orders);
        nobodyOrder = findViewById(R.id.nobodyOrder);
        gson = new Gson();
        complete_orders = findViewById(R.id.complete_orders);
        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        //判断 是否有数据
        String mJson = sharedPreferencesUtil.getUUID("mJson");
        if(mJson != null) {
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
                            //保存json数据
                            nobodyOrder.setVisibility(View.GONE);
                            viewPager.setVisibility(View.VISIBLE);
                            line_order.setVisibility(View.VISIBLE);
                        } else {
                            nobodyOrder.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.GONE);
                            line_order.setVisibility(View.GONE);
                        }
                    }
                }
            }else {
                nobodyOrder.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                line_order.setVisibility(View.GONE);
            }
        }
        fragmentList = new ArrayList<>();
        f1 = new Fragment_all_order();
        f2 = new Fragment_not_order();
        f3 = new Fragment_complete_order();
        fragmentList.add(f1);
        fragmentList.add(f2);
        fragmentList.add(f3);
        list = new ArrayList<>();
        list.add(all_orders);
        list.add(not_orders);
        list.add(complete_orders);
        fm = getSupportFragmentManager();
        orderPagerAdapter = new OrderPagerAdapter(fm, fragmentList);
        viewPager.setAdapter(orderPagerAdapter);
        //默认进入第一次是第一页
        viewPager.setCurrentItem(0);
        not_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
        all_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_select));
        complete_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
        all_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                all_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_select));
                not_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
                complete_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
            }
        });
        not_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                all_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
                not_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_select));
                complete_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
            }
        });
        complete_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
                all_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
                not_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
                complete_orders.setBackground(getResources().getDrawable(R.drawable.textview_order_pay_select));
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == position){
                        list.get(i).setBackground(getResources().getDrawable(R.drawable.textview_order_pay_select));

                    }else {
                        list.get(i).setBackground(getResources().getDrawable(R.drawable.textview_order_pay_notselect));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("订单信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this,R.drawable.path,
                70,70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: " + sharedPreferencesUtil.getUUID("Order"));
                if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("Order"))){
                    sharedPreferencesUtil.saveUUID("Order","");
//                    startActivity(new Intent(OrderActivity.this, OrderTimeActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(OrderActivity.this, Personal_center.class));
                    finish();
                }


                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (f1 != null){
            f1 = null;
        }
        if (f2 != null){
            f2 = null;
        }
        if (f3 != null){
            f3 = null;
        }
    }
}
