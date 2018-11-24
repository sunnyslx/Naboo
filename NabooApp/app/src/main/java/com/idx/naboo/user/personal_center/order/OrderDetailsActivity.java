package com.idx.naboo.user.personal_center.order;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;

import com.idx.naboo.takeout.ui.TakeoutPayActivity;

import com.idx.naboo.user.personal_center.address.AllAddress;
import com.idx.naboo.user.personal_center.address.ApiRequestUtils_1;
import com.idx.naboo.user.personal_center.address.bean.GetAdderssList;
import com.idx.naboo.user.personal_center.order.orderbean.Items;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;
import com.idx.naboo.user.personal_center.order.orderbean.Reply;
import com.idx.naboo.user.personal_center.order.orderbean.Root;
import com.idx.naboo.user.personal_center.order.recyclerView.DetailsRecyclerView;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;

import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.requestdata.FProtocol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailsActivity extends BaseActivity {

    private static final String TAG = "OrderDetailsActivity";

    public CountDownTimer countDownTimer;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private String mJson;
    private Orders orders;
    private long currenttime;
    private long order_time_number;
    private Bitmap mBitmap_toolbar;
    private TextView order_id;
    private TextView order_time;
    private TextView order_send_time;
    private TextView address;
    private TextView name_phone;
    private TextView orderName;
    private TextView totalPrice;
    private TextView orderPhone;
    private RecyclerView recyclerView;
    private DetailsRecyclerView adapter;
    private TextView countdown;
    private TextView cancel_order;
    private TextView cancel_pay;
    private LinearLayout toolbar_line;
    private String payData;
    private String payTime;
    private String payDataTime;
    private long remaining_time;
    private double total;
    private IService mIService;


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener( OrderDetailsActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        currenttime = System.currentTimeMillis();
        initToolbar();

        initView();


    }
    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("back")){
            if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("Order"))){
                startActivity(new Intent(OrderDetailsActivity.this,OrderTimeActivity.class));
                sharedPreferencesUtil.saveUUID("Order","");

            }else {
                startActivity(new Intent(OrderDetailsActivity.this,OrderActivity.class));

            }
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(OrderDetailsActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(conn);
    }




    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("订单详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this, R.drawable.path,
                70, 70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
        countdown = findViewById(R.id.countdown);   //支付倒计时
        cancel_pay = findViewById(R.id.cancel_pay);      //取消支付
        cancel_order = findViewById(R.id.cancel_order);  //取消订单

        countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this,TakeoutPayActivity.class);
                intent.putExtra("time",order_time_number);
                intent.putExtra("total",total);
                startActivity(intent);
            }
        });

        cancel_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("Order"))){
                    startActivity(new Intent(OrderDetailsActivity.this,OrderTimeActivity.class));
                    sharedPreferencesUtil.saveUUID("Order","");

                }else {
                    startActivity(new Intent(OrderDetailsActivity.this,OrderActivity.class));

                }
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        //取消订单 倒计时
        //toolbar_line = findViewById(R.id.toolbar_line);

        Gson gson = new Gson();
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        mJson = sharedPreferencesUtil.getUUID("mJson");

        //订单号
        order_id = findViewById(R.id.order_id);
        //订单时间
        order_time = findViewById(R.id.order_time);
        //期望配送时间
        order_send_time = findViewById(R.id.order_send_time);
        //地址
        address = findViewById(R.id.address);
        // 姓名 手机号码
        name_phone = findViewById(R.id.name_phone);
        //商家名
        orderName = findViewById(R.id.orderName);
        //商家号码
        orderPhone = findViewById(R.id.orderPhone);

        recyclerView = findViewById(R.id.recyclerView);
        totalPrice = findViewById(R.id.totalPrice);

        //获取地址
        final GetAdderssList getAdderssList = new GetAdderssList();
        getAdderssList.setMo(sharedPreferencesUtil.getUUID("mobile"));
        Log.d(TAG, "searchAddress: 当前手机号码");
        ApiRequestUtils_1.getAddressList(this, getAdderssList, 0x08, new NetRequestCallback() {
            @Override
            public void success(int requestCode, final String data) {

                Log.d(TAG, "success: AllAddress " + data);
                Gson gson  = new Gson();
                AllAddress allAddress = gson.fromJson(data,AllAddress.class);

                address.setText(allAddress.getData().get(0).getAddressRegion() + allAddress.getData().get(0).getAddressDetail());
                name_phone.setText(allAddress.getData().get(0).getConsignee() + " " + allAddress.getData().get(0).getConsigneeMobile());
                    }

                @Override
                public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
                    Log.d(TAG, "fail: AllAddress " + requestCode);

                }
            });


        Intent intent = getIntent();
        int position = Integer.parseInt(intent.getStringExtra("position"));

        mJson = sharedPreferencesUtil.getUUID("mJson");
        if (mJson!=null) {
            JsonData jsonUtil = JsonUtil.createJsonData(mJson);
            String type = jsonUtil.getType();
            Log.d(TAG, "onJsonReceived: " + type);
            if (type.equals("order_list")) {
                Root orderRoot = gson.fromJson(mJson, Root.class);
                Reply reply = orderRoot.getData().getContent().getReply();
                //得到订单item
                orders = reply.getOrder_list().get(0).getOrders().get(position);
                List<Items> items = orders.getItems();
                adapter = new DetailsRecyclerView(items);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);

                //年月日
                payData = orders.getCreateTime().substring(0,10);

                //时分秒
                payTime = orders.getCreateTime().substring(11);

                //总时间
                payDataTime = payData + " " + payTime;

                order_send_time.setText(orders.getAttributes().get(0).getString().get(0));

                order_id.setText("订单号码: " + orders.getMerchantId());
                order_time.setText(payDataTime);
                orderName.setText(orders.getOrderName());
                orderPhone.setText("商家电话: " + orders.getAttributes().get(4).getString().get(0));

                total = orders.getTotalPrice() / 100;
                totalPrice.setText("¥" + total);

                try {
                    order_time_number = stringToLong(payDataTime,"yyyy-MM-dd HH:mm:ss");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (currenttime - order_time_number > 900000 ){
                    countdown.setVisibility(View.GONE);
                    cancel_pay.setVisibility(View.GONE);
                    cancel_order.setVisibility(View.VISIBLE);
                }else {
                    //进行倒计时
                    countdown.setVisibility(View.VISIBLE);
                    cancel_pay.setVisibility(View.VISIBLE);
                    cancel_order.setVisibility(View.GONE);

                    //倒计时 时长
                    remaining_time = (14*60*1000)-(currenttime - order_time_number);
                    Log.d(TAG, "显示倒计时");
                    countDownTimer = new CountDownTimer(remaining_time, 1000) {
                        /**
                         * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
                         * @param millisUntilFinished
                         */
                        @Override
                        public void onTick(long millisUntilFinished) {
                            countdown.setText(formatTime(millisUntilFinished));
                        }

                        /**
                         * 倒计时完成时被调用
                         */
                        @Override
                        public void onFinish() {
                            countdown.setVisibility(View.GONE);
                            cancel_pay.setVisibility(View.GONE);
                            cancel_order.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }
            }
        }


//        toolbar_line.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // startActivity(new Intent(OrderDetailsActivity.this,OrderTimeActivity.class));
//                finish();
//            }
//        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("Order"))){
            startActivity(new Intent(OrderDetailsActivity.this,OrderTimeActivity.class));
            sharedPreferencesUtil.saveUUID("Order","");

        }else {
            startActivity(new Intent(OrderDetailsActivity.this,OrderActivity.class));

        }
        finish();    }

    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "支付（剩余"+ "0" + minute + ":" + "0" + second + ")";
            } else {
                return "支付（剩余"+ "0" + minute + ":" + second + ")";
            }
        }else {
            if (second < 10) {
                return "支付（剩余" + minute + ":" + "0" + second + ")";
            } else {
                return "支付（剩余" + minute + ":" + second + ")";
            }
        }
    }

    //string类型转换为long类型
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date.getTime();
    }

}


