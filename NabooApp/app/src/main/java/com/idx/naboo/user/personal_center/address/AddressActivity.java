package com.idx.naboo.user.personal_center.address;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.idx.naboo.service.listener.DataListener;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.user.personal_center.Personal_center;
import com.idx.naboo.user.personal_center.address.bean.DeleteAddress;
import com.idx.naboo.user.personal_center.address.bean.GetAdderssList;
import com.idx.naboo.utils.BitmapUtils;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.requestdata.FProtocol;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends BaseActivity {

    private Bitmap mBitmap_toolbar;
    private static final String TAG = "AddressActivity";
    private List<DataBean> list = new ArrayList<>();
    private IService mIService;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private RecyclerView recyclerView;
    private AddressRecyclerView adapter;
    private LinearLayout nobodyAddress;
    private LinearLayoutManager layoutManager;
    private AlertDialog dialog;

    private int x;

    //得到最后一个地址id
    private int lastAddressId;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener((DataListener) AddressActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private String name;
    private String phone;
    private String select;
    private String detailed;
    private String current;
    private String labels;
    private String addressId;
    private DataBean dataBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_address);
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        initToolbar();
        initView();

    }
    private void initView() {
        nobodyAddress = findViewById(R.id.nobodyAddress);
        recyclerView = findViewById(R.id.recyclerView_Address);
        nobodyAddress.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        searchAddress();
    }

    //查询所有地址
    private void searchAddress(){
        final GetAdderssList getAdderssList = new GetAdderssList();
        getAdderssList.setMo(sharedPreferencesUtil.getUUID("mobile"));
        Log.d(TAG, "searchAddress: 当前手机号码");
        ApiRequestUtils_1.getAddressList(this, getAdderssList, 0x08, new NetRequestCallback() {
            @Override
            public void success(int requestCode, final String data) {

                Log.d(TAG, "success: AllAddress " + data);
                Gson gson  = new Gson();
                AllAddress allAddress = gson.fromJson(data,AllAddress.class);
                if (allAddress.getData().isEmpty()){
                    nobodyAddress.setVisibility(View.VISIBLE);
                    Log.d(TAG, "success:此时无地址");
                }else {
                    Log.d(TAG, "success: ");
                    nobodyAddress.setVisibility(View.GONE);
                    list = allAddress.getData();

                    //最后一个ID
                    lastAddressId = list.get(list.size()-1).getAdddressid();
                    Log.d(TAG, "success: "+ lastAddressId);

                    adapter = new AddressRecyclerView(getApplication(),list);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new AddressRecyclerView.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int postion) {
                            DataBean dataBean = list.get(postion);
                            Intent intent = new Intent(AddressActivity.this,UpdateActivity.class);
                            intent.putExtra("name", dataBean.getConsignee());
                            intent.putExtra("phone", dataBean.getConsigneeMobile());
                            intent.putExtra("select", dataBean.getAddressRegion());
                            intent.putExtra("detailed", dataBean.getAddressDetail());
                            intent.putExtra("current_time",dataBean.getAddressCity());
                            intent.putExtra("labels",dataBean.getAddressAlias());
                            intent.putExtra("addressId",String.valueOf(dataBean.getAdddressid()));
                            Log.d(TAG, "onItemClick: "+ dataBean.getConsignee());
                            startActivityForResult(intent,6);
                            list.remove(postion);
                            x = postion;
                        }
                    });

                    adapter.setOnItemClickListenerDelete(new AddressRecyclerView.OnItemClickListenerDelete() {
                        @Override
                        public void onItemClickDelete(View view, final int position) {
                            DataBean dataBean = list.get(position);
                            showDialog1(position,dataBean);
                        }
                    });


                    adapter.setOnSelectAddressListener(new AddressRecyclerView.OnSelectAddressListener() {
                        @Override
                        public void onSelectAddressListener(String username, String phone, String address, String detail) {
                            Log.d(TAG, "选择地址: "+username+"-"+phone+"-"+address+"-"+detail);
                            if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.COMMIT_ORDER_ADDRESS,""))
                                    && SpUtils.get(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"").equals(Constant.COMMIT_ORDER_ADDRESS_VALUE)){
                                finish();
                                SpUtils.put(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"");
                            }else if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,""))
                                    && (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE)
                                    || SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE))){
                                finish();
                                SpUtils.put(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"");
                            }else {//其他操作
                                //Toast.makeText(AddressActivity.this, "其他操作", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }
            }
            @Override
            public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
                Log.d(TAG, "fail: AllAddress " + requestCode);
                nobodyAddress.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        if (type.equals("back")){
            back();
        }
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
    }

    //返回指令或返回键
    private void back() {
        if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.COMMIT_ORDER_ADDRESS,""))
                && SpUtils.get(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"")
                .equals(Constant.COMMIT_ORDER_ADDRESS_VALUE)){//由确认订单界面跳转过来
            finish();
            SpUtils.put(getApplicationContext(),Constant.COMMIT_ORDER_ADDRESS,"");
        }else if (!TextUtils.isEmpty(SpUtils.get(getApplicationContext(), Constant.TAKEOUT_SELLER,""))
                && (SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_SELLER_VALUE)
                || SpUtils.get(getApplicationContext(),Constant.TAKEOUT_SELLER,"").equals(Constant.TAKEOUT_SELLER_CAR_VALUE))){
            //由购物车或商家跳转过来
            finish();
            SpUtils.put(getApplicationContext(),Constant.TAKEOUT_SELLER,"");
        }else {//个人中心
            startActivity(new Intent(AddressActivity.this, Personal_center.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(AddressActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("地址管理");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mBitmap_toolbar = BitmapUtils.scaleBitmapFromResources(this,R.drawable.path,
                70,70);
        actionBar.setHomeAsUpIndicator(new BitmapDrawable(mBitmap_toolbar));
        TextView toolbar_textview = findViewById(R.id.toolbar_textview);
        toolbar_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this,NewAddAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 6:
                name = data.getStringExtra("name");
                phone = data.getStringExtra("phone");
                select = data.getStringExtra("select");
                detailed = data.getStringExtra("detailed");
                current = data.getStringExtra("current_time");
                labels =data.getStringExtra("labels");
                addressId = data.getStringExtra("addressId");
                dataBean = new DataBean();
                dataBean.setConsignee(name); // 姓名
                dataBean.setAdddressid(Integer.parseInt(addressId)); //地址id
                dataBean.setConsigneeMobile(phone); //手機號碼
                dataBean.setAddressDetail(detailed); //詳細地址
                dataBean.setAddressRegion(select); //地區
                dataBean.setAddressAlias(labels);
                dataBean.setAddressCity(current);
                list.add(x,dataBean);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.add_new_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                back();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    //是否删除item的dialog
    public void showDialog1(final int position, final DataBean dataBean){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_address_delete,null);
        builder.setView(layout);
        Button ok = layout.findViewById(R.id.ok);
        Button cancel = layout.findViewById(R.id.cancel);
        dialog = builder.create();
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DeleteAddress deleteAddress = new DeleteAddress();
                deleteAddress.setAddressid(String.valueOf(dataBean.getAdddressid()));
                deleteAddress.setUid(sharedPreferencesUtil.getUUID("uuid"));
                ApiRequestUtils_1.deleteAddress(AddressActivity.this, deleteAddress, 0x09, new NetRequestCallback() {
                    @Override
                    public void success(int requestCode, String data) {
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemChanged(position);
                        adapter.notifyItemRangeChanged(position, list.size() - position);
                        if (list.isEmpty()){
                            nobodyAddress.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {

                    }
                });
                //移除item

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

}
