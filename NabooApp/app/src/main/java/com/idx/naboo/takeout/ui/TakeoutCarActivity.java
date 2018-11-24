package com.idx.naboo.takeout.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.CartReportBean;
import com.idx.naboo.takeout.SaleManager;
import com.idx.naboo.takeout.adapter.TakeoutCarAdapter;
import com.idx.naboo.takeout.data.item.ImoranTMResponse;
import com.idx.naboo.takeout.data.item.TMContent;
import com.idx.naboo.takeout.data.item.TMSummary;
import com.idx.naboo.takeout.data.item.TakeoutMenu;
import com.idx.naboo.takeout.data.room.TakeoutDataSource;
import com.idx.naboo.takeout.data.room.TakeoutInjection;
import com.idx.naboo.takeout.data.room.TakeoutRepository;
import com.idx.naboo.takeout.data.room.TakeoutSeller;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.takeout.utils.ImoranResponseParseUtil;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.bean.TakeoutmenuBean;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车界面
 * Created by danny on 4/18/18.
 */

public class TakeoutCarActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG=TakeoutCarActivity.class.getSimpleName();
    private IService mIService;
    private Context mContext;

    //view
    private Button mBack;
    private Button mClearCar;
    private RelativeLayout mCarContentShow;
    private TextView mTotal;
    private TextView mExtra;
    private Button mBalance;
    private ListView mListView;
    private TextView mEmpty;
    private ImageView mEmptyImage;
    private LinearLayout mLayout;
    private TakeoutCarAdapter mAdapter;

    //Imoran数据
    private JsonData mJsonData;//Imoran返回基础数据
    private String mJson;
    private List<TakeoutMenu> mMenus;
    private ImoranTMResponse mTMResponse;
    private TMContent mTMContent;

    private TakeoutRepository mTakeoutRepository;
    private List<TakeoutSeller> mSellers;
    private String mSellerName;
    private String mSellerPhone;
    private double mTotalFee=0;
    private double mPackageFee=0;
    private DecimalFormat df;

    //提交订单
    private List<TakeoutmenuBean.TakeoutmenuEntity> mTakeoutmenuEntities;
    private TakeoutmenuBean.TakeoutmenuEntity mEntity;
    private TMSummary mSummary;
    private boolean mFlag;

    private String mQueryId;
    private String mPageId;

    private CartReportBean<TakeoutmenuBean.TakeoutmenuEntity> mCartReportBean=null;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            SaleManager.buyTakeOut(mTakeoutmenuEntities,mSummary,TakeoutCarActivity.this, Constant.TAKEOUT_SELLER_CAR_VALUE);
        }
    };

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(TakeoutCarActivity.this);
            //获取json
            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())){
                Log.i(TAG, "first bind service");
                mJson = mIService.getJson();
                mJsonData = JsonUtil.createJsonData(mJson);
                if (mJsonData != null) {
                    String tts = mJsonData.getTts();
                    TTSManager.getInstance(getBaseContext()).speak(tts, true);
                }
                //置空Json
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);

            }else {
                Log.d(TAG, "json为空: ");
                mJson=SpUtils.get(mContext,"cmd_cart","");
                setScenes(mJson);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {if (mIService!=null)mIService=null;}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeout_car);
        mContext=this;
        initView();
        mJson= SpUtils.get(mContext,"summary","");
        mSellerName=getIntent().getStringExtra("takeout_seller");
        mSellerPhone=getIntent().getStringExtra("takeout_seller_phone");
        Log.d(TAG, "onCreate: "+mSellerName+"-"+mSellerPhone);
        if (!TextUtils.isEmpty(mJson)){parseToTM(mJson);}
        TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.ok_open_cart), true);
        mJson=SpUtils.get(mContext,"cmd_cart","");
        setScenes(mJson);
//        setPresentData();
    }

    private void setScenes(String json) {
        JSONObject jsonObject=null;
        try {
            //场景设置
            jsonObject = new JSONObject(json);
            mQueryId = jsonObject.getJSONObject("data").getString("queryid");
            Log.e(TAG, "cart: " + mQueryId);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String intention = jsonObject.getJSONObject("data").getString("intention");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            mPageId = domain+"_"+intention+"_"+type+"_"+"takeoutcaractivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析菜单
     * @param json 蓦然json数据
     */
    private void parseToTM(String json) {
        mTMResponse = ImoranResponseParseUtil.parseTM(json);
        if (ImoranResponseParseUtil.isImoranTMNull(mTMResponse)) {
            mTMContent = mTMResponse.getData().getContent();
            if (mTMContent.getErrorCode() == 0) {
                if (mTMContent.getTMReply().getMenus() != null && mTMContent.getTMReply().getMenus().size() > 0) {
                    mMenus = mTMContent.getTMReply().getMenus();//拿到菜单列表
                    mSummary=mTMContent.getTMSummary();
                }
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mTakeoutRepository= TakeoutInjection.getInstance(mContext);
        mBack=findViewById(R.id.takeout_car_back);
        mClearCar=findViewById(R.id.takeout_car_clear);
        mCarContentShow=findViewById(R.id.takeout_car_content);
        mTotal=findViewById(R.id.takeout_car_total);
        mExtra=findViewById(R.id.takeout_car_total_extra);
        mBalance=findViewById(R.id.takeout_car_balance);
        mListView=findViewById(R.id.takeout_car_list_view);
        mEmpty=findViewById(R.id.takeout_car_empty);
        mEmptyImage=findViewById(R.id.takeout_car_empty_image);
        mLayout=findViewById(R.id.takeout_ll_2);
        mBack.setOnClickListener(this);
        mClearCar.setOnClickListener(this);
        mBalance.setOnClickListener(this);
        df = new DecimalFormat("######0.00");
    }

    /**
     * 查询购物车数据
     */
    private void query() {
        mTakeoutRepository.queryAllFood(mSellerName, new TakeoutDataSource.LoadAllSellerCallback() {
            @Override
            public void onSuccess(List<TakeoutSeller> sellers) {
                ((NabooApplication) getApplication()).getDataEntity().setPresent_data(null);

                mCartReportBean = new CartReportBean<>();
                CartReportBean.CartInfoBean<TakeoutmenuBean.TakeoutmenuEntity> takeoutCartInfoBean=new CartReportBean.CartInfoBean<>();
                List<TakeoutmenuBean.TakeoutmenuEntity> lists = new ArrayList<>();
                for (int i = 0; i < sellers.size(); i++) {
                    TakeoutmenuBean.TakeoutmenuEntity entity=new TakeoutmenuBean.TakeoutmenuEntity();
                    //购物车同步到服务器
                    entity.setName(sellers.get(i).food_name);
                    entity.setPrice(sellers.get(i).price);
                    entity.setTakeoutNum(sellers.get(i).count+"");
                    for (int j = 0; mMenus != null && j < mMenus.size(); j++) {
                        if (mMenus.get(j).getName().equals(sellers.get(i).food_name)) {
                            entity.setActivity_description(mMenus.get(j).getActivity_description());
                            entity.setActivity_discount(mMenus.get(j).getActivity_discount());
                            entity.setActivity_max_quantity(mMenus.get(j).getActivity_max_quantity());
                            entity.setActivity_name(mMenus.get(j).getActivity_name());
                            entity.setActivity_quantity_condition(mMenus.get(j).getActivity_quantity_condition());
                            entity.setActivity_sum_condition(mMenus.get(j).getActivity_sum_condition());
                            entity.setCategory(mMenus.get(j).getCategory());
                            entity.setCheckout_mode(mMenus.get(j).getCheckout_mode());
                            entity.setDescription(mMenus.get(j).getDescription());
                            entity.setIcon_url(mMenus.get(j).getIcon_url());
                            entity.setId(mMenus.get(j).getId());
                            entity.setImage_url(mMenus.get(j).getImage_url());
                            entity.setIs_essential(mMenus.get(j).getIs_essential());
                            entity.setMin_purchase(mMenus.get(j).getMin_purchase());
                            entity.setIsModify(null);
                            entity.setOriginal_price(mMenus.get(j).getOriginal_price());
                            entity.setPacking_fee(mMenus.get(j).getPacking_fee());
                            entity.setRating(mMenus.get(j).getRating());
                            entity.setSales(mMenus.get(j).getSales());
                            entity.setScore(mMenus.get(j).getScore());
                            entity.setStock(mMenus.get(j).getStock());
                            entity.setAttrs(null);
                        }
                    }
                    lists.add(entity);
                }
                Log.d(TAG, "onSuccess: "+lists.size());
                takeoutCartInfoBean.setData(lists);
                takeoutCartInfoBean.setType(CartReportBean.TAKEOUT);
                mCartReportBean.setCart_info(takeoutCartInfoBean);
                Log.d(TAG, "onSuccess: "+mCartReportBean);
                ((NabooApplication) getApplication()).getDataEntity().setPresent_data(mCartReportBean);


                mTotalFee=0;
                mPackageFee=0;
                Log.d(TAG, "onSuccess: ");
                isCarEmpty(false);
                mSellers=sellers;
                mAdapter=new TakeoutCarAdapter(mContext,mSellers);
                mListView.setAdapter(mAdapter);
                for (int i=0;i<sellers.size();i++){
                    int count=sellers.get(i).count;
                    double price=sellers.get(i).price;
                    int a_m_q = sellers.get(i).activity_max_quantity;
                    double o_p = sellers.get(i).old_price;
                    Log.d(TAG, "onSuccess: "+a_m_q+"-"+count);
                    if (a_m_q!=0) {
                        if (a_m_q >= count) {
                            mTotalFee += (count * price);
                        } else {
                            int old_count = count - a_m_q;
                            mTotalFee += (a_m_q * price + old_count * o_p);
                        }
                    }else {
                        mTotalFee += (count*price);
                    }
                    double package_fee=sellers.get(i).packing_fee;
                    mPackageFee+=(package_fee*count);
                }

//                double agent_fee=sellers.get(0).no_agent_fee_total;
//                double agent_fee=mSummary.getAgent_fee();
//                if (mTotalFee>agent_fee){agent_fee=0;}else {agent_fee=sellers.get(0).agent_fee;}
//                final double packing_fee=sellers.get(0).packing_fee;
//                final String temp_agent=df.format(agent_fee);
                final String temp_packing=df.format(mPackageFee);
                mExtra.setText(/*"含配送费￥"+df.format(agent_fee)+*/"含餐盒费￥"+df.format(mPackageFee));
//                mTotalFee+=agent_fee;
                mTotalFee+=mPackageFee;

                mTotal.setText("￥"+df.format(mTotalFee));
                mAdapter.setOnItemIncrementAndReduceListener(new TakeoutCarAdapter.OnItemIncrementAndReduceListener() {
                    @Override
                    public void reduce(double price, double package_fee) {
                        mTotalFee-=price;
                        mTotal.setText("￥"+df.format(mTotalFee));
                        mPackageFee-=package_fee;
                        mExtra.setText(/*"含配送费￥"+temp_agent+*/"含餐盒费￥"+df.format(mPackageFee));
                        Log.d(TAG, "reduce: "+mTotalFee);
                        if (!(Double.parseDouble(df.format(mTotalFee))/*-Double.parseDouble(temp_agent)*/-Double.parseDouble(temp_packing)>0)){
                            Log.d(TAG, "reduce: ");
                            clearCar();
                        }
                    }

                    @Override
                    public void increment(double price, double package_fee) {
                        mTotalFee+=price;
                        mTotal.setText("￥"+df.format(mTotalFee));
                        mPackageFee+=package_fee;
                        mExtra.setText(/*"含配送费￥"+temp_agent+*/"含餐盒费￥"+df.format(mPackageFee));
                    }

                    @Override
                    public void remove(int position) {
                        Log.d(TAG, "remove: ");
                        mSellers.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError() {
                if (mSellers!=null){
                    mSellers.clear();
                }
                isCarEmpty(true);
            }
        });
    }

    /**
     * 判断购物车是否清空
     * @param flag true-清空
     */
    private void isCarEmpty(boolean flag){
        if (flag){
            mCarContentShow.setVisibility(View.GONE);
            mClearCar.setVisibility(View.GONE);
            mLayout.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            mEmptyImage.setVisibility(View.VISIBLE);
        }else {
            mEmpty.setVisibility(View.GONE);
            mEmptyImage.setVisibility(View.GONE);
            mCarContentShow.setVisibility(View.VISIBLE);
            mClearCar.setVisibility(View.VISIBLE);
            mLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFlag=false;
        if (!TextUtils.isEmpty(mSellerName)) {
            if (mSellers!=null){mSellers.clear();}
            query();
        }
        bindService(new Intent(getBaseContext(), SpeakService.class),mConn,BIND_AUTO_CREATE);
//        setPresentData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConn);
        TTSManager.getInstance(getBaseContext()).stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takeout_car_back:
                back();
                break;
            case R.id.takeout_car_clear:
                clearCar();
                break;
            case R.id.takeout_car_balance:
                if (!mFlag) {
                    balance();
                }
                break;
        }
    }

    /**
     * 后退键及后退按钮处理
     */
    private void back() {
        TakeoutCarActivity.this.finish();
        if (mIService!=null){
            mIService.requestData(getResources().getString(R.string.back), new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {}

                @Override
                public void onError() {}
            });
        }
    }

    @Override
    public void onBackPressed() {back();}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTakeoutRepository!=null){mTakeoutRepository=null;}
//        if (mCartReportBean!=null){mCartReportBean=null;}
        if (mSellers!=null){mSellers.clear();mSellers=null;}
        if (mEntity!=null){mEntity=null;}
        if (mConn!=null){mConn=null;}
        if (mMenus!=null){mMenus.clear();mMenus=null;}
        if (mHandler!=null){mHandler.removeCallbacksAndMessages(null);mHandler=null;}
        if (mTakeoutmenuEntities!=null){mTakeoutmenuEntities.clear();;mTakeoutmenuEntities=null;}
        if (mSummary!=null){mSummary=null;}
        if (mTMContent!=null){mTMContent=null;}
        if (mTMResponse!=null){mTMResponse=null;}
        if (mIService!=null){mIService=null;}
        if (mContext!=null){mContext=null;}
        ((NabooApplication) getApplication()).getDataEntity().setPresent_data(null);
    }

    /**
     * 清空购物车--删除表中所有记录
     */
    private void clearCar() {
        mTakeoutRepository.deleteTakeoutSeller();
        query();
    }

    /**
     * 结算
     */
    private void balance() {
        mFlag=true;
        Log.d(TAG, "balance: ");
        mTakeoutRepository.queryAllFood(mSellerName, new TakeoutDataSource.LoadAllSellerCallback() {
            @Override
            public void onSuccess(List<TakeoutSeller> sellers) {
                mTakeoutmenuEntities=new ArrayList<>();
                for (int i = 0; i < sellers.size(); i++) {
                    //提交订单参数--List<TakeoutmenuBean.TakeoutmenuEntity> mTakeoutmenuEntities
                    mEntity=new TakeoutmenuBean.TakeoutmenuEntity();
                    mEntity.setName(sellers.get(i).food_name);
                    mEntity.setPrice(sellers.get(i).price);
                    mEntity.setTakeoutNum(sellers.get(i).count+"");
                    for (int j=0; mMenus!=null && j<mMenus.size(); j++){
                        if (mMenus.get(j).getName().equals(sellers.get(i).food_name)){
                            mEntity.setActivity_description(mMenus.get(j).getActivity_description());
                            mEntity.setActivity_discount(mMenus.get(j).getActivity_discount());
                            mEntity.setActivity_max_quantity(mMenus.get(j).getActivity_max_quantity());
                            mEntity.setActivity_name(mMenus.get(j).getActivity_name());
                            mEntity.setActivity_quantity_condition(mMenus.get(j).getActivity_quantity_condition());
                            mEntity.setActivity_sum_condition(mMenus.get(j).getActivity_sum_condition());
                            mEntity.setCategory(mMenus.get(j).getCategory());
                            mEntity.setCheckout_mode(mMenus.get(j).getCheckout_mode());
                            mEntity.setDescription(mMenus.get(j).getDescription());
                            mEntity.setIcon_url(mMenus.get(j).getIcon_url());
                            mEntity.setId(mMenus.get(j).getId());
                            mEntity.setImage_url(mMenus.get(j).getImage_url());
                            mEntity.setIs_essential(mMenus.get(j).getIs_essential());
                            mEntity.setMin_purchase(mMenus.get(j).getMin_purchase());
                            mEntity.setIsModify(null);
                            mEntity.setOriginal_price(mMenus.get(j).getOriginal_price());
                            mEntity.setPacking_fee(mMenus.get(j).getPacking_fee());
                            mEntity.setRating(mMenus.get(j).getRating());
                            mEntity.setSales(mMenus.get(j).getSales());
                            mEntity.setScore(mMenus.get(j).getScore());
                            mEntity.setStock(mMenus.get(j).getStock());
                            mEntity.setAttrs(null);
                        }
                    }
                    mTakeoutmenuEntities.add(mEntity);
                    if (i==sellers.size()-1){mHandler.sendEmptyMessage(0);}
                }
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void onJsonReceived(String json) {
        if (!mFlag) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                String domain = jsonObject.getJSONObject("data").getString("domain");
                String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
                String tts = jsonObject.getJSONObject("data").getJSONObject("content").getString("tts");
                Log.d(TAG, "onJsonReceived: " + domain + "-" + type + ":" + tts);
                if (type.equals("takeoutmenucart")){
                    TTSManager.getInstance(getBaseContext()).speak(tts, true);
                    setScenes(json);
                    SpUtils.put(mContext,"cmd_cart",json);
                    modifyGoodCount(json);
                } else if (type.equals("back") || type.equals("close_cart")) {
                    finish();
                } else if (type.equals("clear_cart")) {
                    clearCar();
                } else if (type.equals("bill") || type.equals("confirm_order") || type.equals("confirm_seat")) {
                    balance();
                }
                //置空Json
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "onJsonReceived: 已进入确认订单界面");
        }
    }

    private void modifyGoodCount(String json) {
        JSONObject jsonObjectTakeoutCart = null;
        JSONObject jsonObjectTakeoutCartModify = null;
        String takeout_cart = "";
        String takeout_cart_modify = "";
        String takeout_num = "";
        try {
            jsonObjectTakeoutCart = new JSONObject(json);
            String temp = "";
            takeout_cart = jsonObjectTakeoutCart.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("TakeoutCart").getString(0);
            if (takeout_cart.contains(" ")) {
                temp = takeout_cart.replace(" ","+");
            }else {
                temp = takeout_cart;
            }
            Log.e(TAG, "modifyGoodCount: " + temp);
            try {
                takeout_cart_modify = jsonObjectTakeoutCart.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("TakeoutCartModify").getString(0);
                Log.e(TAG, "modifyGoodCount: " + takeout_cart_modify);
                if (takeout_cart_modify.equals("delete")){//删除商品
                    mTakeoutRepository.deleteFood(temp, new TakeoutDataSource.SaveOverCallback() {
                        @Override
                        public void onSuccess() {
                            //删除完成,更新界面
                            query();
                        }
                    });
                }else if (takeout_cart_modify.equals("modify")){
                    takeout_num = jsonObjectTakeoutCart.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("TakeoutNum").getString(0);
                    Log.e(TAG, "modifyGoodCount: " + takeout_num);
                    final int finalTakeout_num2 = Integer.parseInt(takeout_num);
                    final String finalTakeout_cart1 = temp;
                    mTakeoutRepository.findFood(finalTakeout_cart1, new TakeoutDataSource.LoadSellerCallback() {
                        @Override
                        public void onSuccess(TakeoutSeller seller) {
                            mTakeoutRepository.modifyFoodCount(finalTakeout_num2, finalTakeout_cart1, new TakeoutDataSource.SaveOverCallback() {
                                @Override
                                public void onSuccess() {
                                    //修改完成,更新界面
                                    query();
                                }
                            });
                        }

                        @Override
                        public void onError() {}
                    });
                }
            }catch (JSONException e){//无TakeoutCartModify属性:第几个给我来几份,有index_number/takeout_num/takeout_cart
                takeout_num = jsonObjectTakeoutCart.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("TakeoutNum").getString(0);
                Log.d(TAG, "modifyGoodCount: " + takeout_num);
                final int finalTakeout_num = Integer.parseInt(takeout_num);
                final String finalTakeout_cart = temp;
                mTakeoutRepository.findFood(finalTakeout_cart, new TakeoutDataSource.LoadSellerCallback() {
                    @Override
                    public void onSuccess(TakeoutSeller seller) {
                        mTakeoutRepository.modifyFoodCount(finalTakeout_num, finalTakeout_cart, new TakeoutDataSource.SaveOverCallback() {
                            @Override
                            public void onSuccess() {
                                //第几个给我来几份,完成,更新界面
                                query();
                            }
                        });
                    }

                    @Override
                    public void onError() {}
                });
            }
        } catch (JSONException e) {
            try {//一件时改商品数量
                jsonObjectTakeoutCartModify = new JSONObject(json);
                takeout_cart_modify = jsonObjectTakeoutCart.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("TakeoutCartModify").getString(0);
                if (takeout_cart_modify.equals("delete")){//删除商品
                    Toast.makeText(mContext,"不存在该商品,请确认!",Toast.LENGTH_SHORT).show();
                }else if (takeout_cart_modify.equals("modify")){
                    takeout_num = jsonObjectTakeoutCartModify.getJSONObject("data").getJSONObject("content").getJSONObject("semantic").getJSONArray("TakeoutNum").getString(0);
                    final int finalTakeout_num1 = Integer.parseInt(takeout_num);
                    mTakeoutRepository.queryAllFood(mSellerName, new TakeoutDataSource.LoadAllSellerCallback() {
                        @Override
                        public void onSuccess(List<TakeoutSeller> sellers) {
                            mTakeoutRepository.modifyFoodCount(finalTakeout_num1, sellers.get(0).food_name, new TakeoutDataSource.SaveOverCallback() {
                                @Override
                                public void onSuccess() {
                                    //一件商品修改数量完成,更新界面
                                    query();
                                }
                            });
                        }

                        @Override
                        public void onError() {}
                    });
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
