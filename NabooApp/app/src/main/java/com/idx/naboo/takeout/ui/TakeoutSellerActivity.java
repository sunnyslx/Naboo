package com.idx.naboo.takeout.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.SaleManager;
import com.idx.naboo.takeout.adapter.TakeoutSellerAdapter;
import com.idx.naboo.takeout.data.item.Activities;
import com.idx.naboo.takeout.data.item.ImoranTMResponse;
import com.idx.naboo.takeout.data.item.MenuList;
import com.idx.naboo.takeout.data.item.TMContent;
import com.idx.naboo.takeout.data.item.TMSummary;
import com.idx.naboo.takeout.data.item.TakeoutMenu;
import com.idx.naboo.takeout.data.room.TakeoutDataSource;
import com.idx.naboo.takeout.data.room.TakeoutInjection;
import com.idx.naboo.takeout.data.room.TakeoutRepository;
import com.idx.naboo.takeout.data.room.TakeoutSeller;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.takeout.utils.ImoranResponseParseUtil;
import com.idx.naboo.takeout.utils.ScreenSizeUtils;
import com.idx.naboo.utils.RoundImageUtils;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.bean.TakeoutmenuBean;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 商家界面
 * Created by danny on 4/18/18.
 */

public class TakeoutSellerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG=TakeoutSellerActivity.class.getSimpleName();
    private IService mIService;
    private Context mContext;

    //view
    private Button mBack;
    private RatingBar mRating;
    private TextView mSend;
    private TextView mDispatcher;
    private TextView mTime;
    private RecyclerView mRecyclerView;
    private LinearLayout mXiaoFu;
    private TextView mGapPrice;
    private Button mBalance;
    private TextView mGoodsCount;
    private RelativeLayout mDataShow;
    private LinearLayout mUnNetworkShow;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout mIntoCar;
    private TextView mFree;

    //Imoran数据
    private JsonData mJsonData;//Imoran返回基础数据
    private String mJson;
    private ImoranTMResponse mTMResponse;
    private TMContent mTMContent;
    private List<TakeoutMenu> mMenus;
    private TMSummary mSummary;
    private TakeoutSellerAdapter mSellerAdapter;
    private List<Activities> mActivities;

    //提交订单
    private List<TakeoutmenuBean.TakeoutmenuEntity> mTakeoutmenuEntities;
    private TakeoutmenuBean.TakeoutmenuEntity mEntity;

    private TakeoutRepository mRepository;
    private String mCurrentSeller;//当前商店名
    private String mCurrentSellerPhone;//当前商店电话

    private boolean mFlag;

    private int mCurrentCount=0;//购物车中显示商品个数
    private double mCurrentPrice=0;//总价
    private DecimalFormat df;
    private DecimalFormat df1;
    private List<MenuList> mMenuLists;

    private String mQueryId;
    private String mPageId="";

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            SaleManager.buyTakeOut(mTakeoutmenuEntities,mSummary,TakeoutSellerActivity.this, Constant.TAKEOUT_SELLER_SELLER_VALUE);
        }
    };

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(TakeoutSellerActivity.this);
            //获取json
            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())){
                Log.i(TAG, "first bind service");
                mJson = mIService.getJson();
                SpUtils.put(mContext,Constant.TAKEOUT_SELLER_SP,mJson);
                //置空Json
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
                //解析Json
                parseToTM(mJson);
                playTTS();
                mJson="";
            }else if (!TextUtils.isEmpty(mJson)){
                SpUtils.put(mContext,Constant.TAKEOUT_SELLER_SP,mJson);
                //解析Json
                parseToTM(mJson);
                playTTS();
                mJson="";
            }else {
                mJson=SpUtils.get(mContext,Constant.TAKEOUT_SELLER_SP,"");
                //解析Json
                parseToTM(mJson);
                mJson="";
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {if (mIService!=null)mIService=null;}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeout_seller);
        mContext=this;
        initView();
        mJson=getIntent().getStringExtra("takeout_shop_click");//拿到点击得到的json数据
    }

    /**
     * 解析商品
     * @param json 蓦然返回数据
     */
    private void parseToTM(String json) {
        mTMResponse = ImoranResponseParseUtil.parseTM(json);
        if (ImoranResponseParseUtil.isImoranTMNull(mTMResponse)) {
            mTMContent = mTMResponse.getData().getContent();

            mQueryId = mTMResponse.getData().getQueryid();
            String domain = mTMResponse.getData().getDomain();
            String intention = mTMResponse.getData().getIntention();
            String type=mTMResponse.getData().getContent().getType();
            mPageId=domain+"_"+intention+"_"+type+"_"+"takeoutselleractivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);

            if (mTMContent.getErrorCode() == 0) {
                if (mTMContent.getTMReply().getMenus() != null && mTMContent.getTMReply().getMenus().size() > 0) {
                    //拿到菜单列表
                    mMenus = mTMContent.getTMReply().getMenus();
                    mSummary=mTMContent.getTMSummary();
                    mMenuLists=new ArrayList<>();
                    for (int i=0;i<mMenus.size();i++){
                        MenuList menuList=new MenuList();
                        menuList.setName(mMenus.get(i).getName());
                        Map<Integer,Integer> map=new HashMap<>();
                        map.put(0,0);
                        menuList.setMap(map);
                        mMenuLists.add(menuList);
                    }
                    updateView();
                    onResumeDataShow();
                }else {
                    noData();
                }
            }else {
                noData();
            }
        }
    }

    /**
     * 更新界面
     */
    private void updateView(){
        if(mMenus!=null && mMenus.size()>0 && mSummary!=null){
            mBack.setText(mTMContent.getTMSummary().getRestaurant_name());
            mSend.setText(getResources().getString(R.string.takeout_open_send)+mTMContent.getTMSummary().getDeliver_amount());
            mDispatcher.setText(getResources().getString(R.string.takeout_dispatcher)+mTMContent.getTMSummary().getAgent_fee());
            mTime.setText(mTMContent.getTMSummary().getDeliver_spent()+getResources().getString(R.string.takeout_dispatcher_time));
            mUnNetworkShow.setVisibility(View.GONE);
            mDataShow.setVisibility(View.VISIBLE);
            mLayoutManager=new LinearLayoutManager(mContext);
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mCurrentSeller=mSummary.getRestaurant_name();
            mCurrentSellerPhone=mSummary.getPhone_list().get(0);
            mSellerAdapter=new TakeoutSellerAdapter(mContext,mMenus,mCurrentSeller);
            mRecyclerView.setAdapter(mSellerAdapter);
            mRecyclerView.setItemViewCacheSize(mMenus.size());
            //条目点击
            mSellerAdapter.setOnItemClickListener(new TakeoutSellerAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    showSellerDialog(position);
                }
            });

            //购物车显示信息
            mSellerAdapter.setOnItemIncrementAndReduceListener(new TakeoutSellerAdapter.OnItemIncrementAndReduceListener() {
                @Override
                public void reduce() {carInfo();}

                @Override
                public void increment() {carInfo();}
            });
        }
    }

    /**
     * 购物车信息
     */
    private void carInfo(){
        mRepository.queryAllFood(mCurrentSeller, new TakeoutDataSource.LoadAllSellerCallback() {
            @Override
            public void onSuccess(List<TakeoutSeller> sellers) {
                for (int i=0;i<sellers.size();i++){
                    Log.d(TAG, "buy: "+sellers.get(i).food_name);
                    int count=sellers.get(i).count;
                    double price=sellers.get(i).price;
                    mCurrentPrice+=(count*price);
                    mCurrentCount+=count;
                }
                Log.d(TAG, "buy price and count: "+mCurrentPrice+"-"+mCurrentCount);
                if (mCurrentCount==0){
                    mGoodsCount.setVisibility(View.INVISIBLE);
                    mBalance.setBackgroundResource(R.drawable.takeout_seller_shop_car);
                }
                if (mCurrentCount>0){
                    mGoodsCount.setVisibility(View.VISIBLE);
                    if (mCurrentCount<1000) {
                        mGoodsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.takeout_2_number_text_size));
                        mGoodsCount.setText("" + mCurrentCount);
                    }else {
                        mGoodsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,20.0f);
                        mGoodsCount.setText("" + mCurrentCount);
                    }
                    mBalance.setBackgroundResource(R.drawable.takeout_seller_jie_suan);
                }
                if (mTMContent!=null){
                    if (mTMContent.getTMSummary().getDeliver_amount()>mCurrentPrice) {
                        mGapPrice.setText(Html.fromHtml("还差<font color=\"#007fee\">￥" + df.format((mTMContent.getTMSummary().getDeliver_amount()-mCurrentPrice)) +"</font>起送"));
                    }else {
                        mGapPrice.setText("￥" + df.format(mCurrentPrice));
                    }
                }

                mCurrentCount=0;
                mCurrentPrice=0;
            }

            @Override
            public void onError() {
                mCurrentCount=0;
                mCurrentPrice=0;
                mGoodsCount.setVisibility(View.INVISIBLE);
                mBalance.setBackgroundResource(R.drawable.takeout_seller_shop_car);
                if (mTMContent!=null){
                    if (mTMContent.getTMSummary().getDeliver_amount()>mCurrentPrice) {
                        mGapPrice.setText(Html.fromHtml("还差<font color=\"#007fee\">￥" + df.format((mTMContent.getTMSummary().getDeliver_amount()-mCurrentPrice)) +"</font>起送"));
                    }else {
                        mGapPrice.setText("￥" + df.format(mCurrentPrice));
                    }
                }
            }
        });
    }

    /**
     * Imoran未返回数据
     */
    private void noData(){
        mUnNetworkShow.setVisibility(View.VISIBLE);
        mDataShow.setVisibility(View.GONE);
    }

    /**
     * 显示商品的对话框
     */
    private void showSellerDialog(int position) {
        if (mMenus!=null && mMenus.size()>position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            View view = LayoutInflater.from(mContext).inflate(R.layout.takeout_seller_item_dialog, null);
            ImageView image = view.findViewById(R.id.takeout_seller_item_dialog_food);
            TextView name = view.findViewById(R.id.takeout_seller_item_dialog_food_name);
            TextView sale = view.findViewById(R.id.takeout_seller_item_dialog_sale_count);
            TextView free = view.findViewById(R.id.takeout_seller_item_dialog_free);
            TextView nowPrice = view.findViewById(R.id.takeout_seller_item_dialog_now_price);
            TextView oldPrice = view.findViewById(R.id.takeout_seller_item_dialog_old_price);
            ImageButton close=view.findViewById(R.id.takeout_seller_item_dialog_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            TakeoutMenu menu = mMenus.get(position);

            Glide.with(this).load(menu.getImage_url())
                    .transform(new RoundImageUtils(mContext, 10))
                    .error(R.mipmap.takeout_order_item_food)
                    .into(image);

            String food_name=menu.getName();
            name.setText(food_name);
            sale.setText(getResources().getString(R.string.takeout_month_sale) + menu.getSales());

            double free_price=menu.getActivity_discount();
            if (free_price != 0 && free_price != 1){
                free.setVisibility(View.VISIBLE);
                free.setText(free_price*10+getResources().getString(R.string.takeout_sale_free));
            }else {
                free.setVisibility(View.INVISIBLE);
            }
            double original_price=menu.getOriginal_price();
            nowPrice.setText(getResources().getString(R.string.takeout_price) + df1.format(menu.getPrice()));
            if (original_price!=0) {
                oldPrice.setVisibility(View.VISIBLE);
                oldPrice.setText(getResources().getString(R.string.takeout_price) +df1.format(original_price));
            }else {
                oldPrice.setVisibility(View.GONE);
            }
            oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            dialog.show();
            dialog.setContentView(view);
        }
    }

    /**
     * 播放TTS
     */
    private void playTTS(){
        if (!TextUtils.isEmpty(mJson)) {
            mJsonData = JsonUtil.createJsonData(mJson);
            if (mJsonData != null) {
                String tts = mJsonData.getTts();
                TTSManager.getInstance(getBaseContext()).speak(tts, true);
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRepository= TakeoutInjection.getInstance(mContext);
        mRepository.deleteTakeoutSeller();//进入商家先将数据库清空？？？
        mBack=findViewById(R.id.takeout_seller_back);
        mRating=findViewById(R.id.takeout_seller_star);
        mSend=findViewById(R.id.takeout_seller_send);
        mDispatcher=findViewById(R.id.takeout_seller_dispatcher);
        mTime=findViewById(R.id.takeout_seller_time);
        mRecyclerView=findViewById(R.id.takeout_seller_recycler_view);
        mXiaoFu=findViewById(R.id.takeout_seller_xiao_fu_reply);
        mGapPrice=findViewById(R.id.activity_seller_gap_price);
        mBalance=findViewById(R.id.takeout_seller_balance);
        mGoodsCount=findViewById(R.id.takeout_seller_goods_count);
        mDataShow=findViewById(R.id.takeout_seller_rl_1);
        mUnNetworkShow=findViewById(R.id.takeout_seller_un_network);
        mIntoCar=findViewById(R.id.takeout_seller_into_car);
        mFree=findViewById(R.id.takeout_seller_label);
        mFree.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mBalance.setOnClickListener(this);
        mIntoCar.setOnClickListener(this);
        df = new DecimalFormat("######0.00");
        df1 = new DecimalFormat("######0.0");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takeout_seller_back://后退
                back();
                break;
            case R.id.takeout_seller_balance://结算
                if (!mFlag) {
                    balance();
                }
                break;
            case R.id.takeout_seller_into_car:
                clickIntoCarPager();
                break;
            case R.id.takeout_seller_label://优惠
                free();
                break;
        }
    }

    /**
     * 点击进入购物车
     */
    private void clickIntoCarPager() {
        if (mIService!=null){
            mIService.requestData("购物车", new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    SpUtils.put(mContext,"cmd_cart",s);
                    intoCarPager();
                }

                @Override
                public void onError() {}
            });
        }
    }

    /**
     * 优惠
     */
    private void free() {
        Log.d(TAG, "free: info");
        if (mSummary!=null){
            mActivities = mSummary.getActivities();
            if (mActivities!=null && mActivities.size()>0){
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                View view = View.inflate(this, R.layout.takeout_seller_free, null);
                ListView listView = view.findViewById(R.id.takeout_seller_free_list);
                ImageButton delete=view.findViewById(R.id.takeout_seller_free_delete);
                listView.setAdapter(new MyAdapter());
                //使得点击对话框外部不消失对话框
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.setContentView(view);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                mSendTime.setText(mTimes.get(position));
//                        dialog.dismiss();
//                    }
//                });

//                设置对话框的大小
                view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.7f);
                lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.7f);
                lp.gravity = Gravity.CENTER;
                dialogWindow.setAttributes(lp);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {dialog.dismiss();}
                });
            }else {
                Toast.makeText(mContext,getResources().getString(R.string.takeout_now_no_free),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 后退键及后退按钮处理
     */
    private void back() {
        TakeoutSellerActivity.this.finish();
        if (mIService!=null){
            mIService.requestData(getResources().getString(R.string.back), new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {}

                @Override
                public void onError() {}
            });
        }
    }

    /**
     * 结算
     */
    private void balance() {
        mFlag=true;
        mRepository.queryAllFood(mCurrentSeller, new TakeoutDataSource.LoadAllSellerCallback() {
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

    /**
     * 进入购物车
     */
    private void intoCarPager() {
        Intent intent=new Intent(mContext,TakeoutCarActivity.class);
        intent.putExtra(Constant.TAKEOUT_SELLER,mCurrentSeller);
        intent.putExtra(Constant.TAKEOUT_SELLER_PHONE,mCurrentSellerPhone);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFlag=false;
        bindService(new Intent(getBaseContext(), SpeakService.class),mConn,BIND_AUTO_CREATE);
        carInfo();
    }

    /**
     * 数据回显
     */
    private void onResumeDataShow(){
        mRepository.queryAllFood(mCurrentSeller, new TakeoutDataSource.LoadAllSellerCallback() {
            @Override
            public void onSuccess(List<TakeoutSeller> sellers) {
                for (int i=0;i<sellers.size();i++){
                    for (int j=0;j<mMenuLists.size();j++){
                        if (mMenuLists.get(j).getName().equals(sellers.get(i).food_name)){
                            Map<Integer,Integer> map=mMenuLists.get(j).getMap();
                            map.put(1,sellers.get(i).count);
//                            break;
                        }
                    }
                }
                if (mSellerAdapter!=null){
                    mSellerAdapter.setItems(mMenuLists);
                }
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConn);
        TTSManager.getInstance(getBaseContext()).stop();
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
                String intention = jsonObject.getJSONObject("data").getString("intention");
                mQueryId = jsonObject.getJSONObject("data").getString("queryid");
                Log.d(TAG, "onJsonReceived: " + type);
                if (type.equals("takeoutmenu")) {
                    mPageId=domain+"_"+intention+"_"+type+"_"+"takeoutselleractivity";
//                    VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
                    parseToMenuList(json);
                    //置空Json
                    Intent intent = new Intent(Execute.ACTION_SUCCESS);
                    sendBroadcast(intent);
                } else if (domain.equals("cmd")) {
                    SpUtils.put(mContext,"cmd_cart",json);
                    dealOtherAction(type, tts);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "onJsonReceived: 已进入确认订单界面");
        }
    }

    /**
     * 解析选购商品
     * @param json 蓦然返回数据
     */
    private void parseToMenuList(String json) {
        JSONObject jsonObject = null;
        try {
            JSONArray takeoutModifyWord = new JSONObject(json).getJSONObject("data").getJSONObject("content")
                    .getJSONObject("semantic").getJSONArray("TakeoutModifyWord");
            String modifyWord = "";
            if (takeoutModifyWord != null) {modifyWord = takeoutModifyWord.getString(0);}
            try {
                JSONArray takeoutProduct = new JSONObject(json).getJSONObject("data").getJSONObject("content")
                        .getJSONObject("semantic").getJSONArray("TakeoutProduct");
                String product = "";
                if (takeoutProduct != null) {product = takeoutProduct.getString(0);}
                try {
                    JSONArray takeoutNum = new JSONObject(json).getJSONObject("data").getJSONObject("content")
                            .getJSONObject("semantic").getJSONArray("TakeoutNum");
                    String num = "";
                    if (takeoutNum != null) {
                        num = takeoutNum.getString(0);
                    }
                    Log.d(TAG, "parseToMenuList: " + modifyWord + "-" + product + "-" + num);
                    final String tempProduct = product;
                    final String tempNum = num;
                    if (modifyWord.equals("改为") || modifyWord.equals("改成") || modifyWord.equals("变为")
                            || modifyWord.equals("变成")) {
                        if (!TextUtils.isEmpty(product)) {
                            Log.d(TAG, "parseToMenuList: " + tempNum + "=" + tempProduct);
                            mRepository.findFood(product, new TakeoutDataSource.LoadSellerCallback() {
                                @Override
                                public void onSuccess(TakeoutSeller seller) {
                                    mRepository.modifyFoodCount(Integer.parseInt(tempNum), tempProduct, new TakeoutDataSource.SaveOverCallback() {
                                        @Override
                                        public void onSuccess() {
                                            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.takeout_modify_result), true);
                                            onResumeDataShow();
                                            carInfo();
                                        }
                                    });
                                }

                                @Override
                                public void onError() {
                                }
                            });
                        }
                    }
                }catch (JSONException e){
                    //无TakeoutNum属性,暂未发现
                }
            }catch (JSONException e){
                //无TakeoutProduct属性,修改最后一件商品数量
                try {
                    JSONArray errorTakeoutNum = new JSONObject(json).getJSONObject("data").getJSONObject("content")
                            .getJSONObject("semantic").getJSONArray("TakeoutNum");
                    String errorNum="";
                    if (errorTakeoutNum != null) {errorNum = errorTakeoutNum.getString(0);}

                    JSONArray errorTakeoutShop = new JSONObject(json).getJSONObject("data").getJSONObject("content")
                            .getJSONObject("semantic").getJSONArray("TakeoutShop");
                    String errorShop="";
                    if (errorTakeoutShop != null){errorShop=errorTakeoutShop.getString(0);}

                    if (modifyWord.equals("改为") || modifyWord.equals("改成") || modifyWord.equals("变为")
                            || modifyWord.equals("变成")) {
                        if (!TextUtils.isEmpty(errorShop) && Integer.parseInt(errorNum) > 0 ) {
                            Log.d(TAG, "parseToMenuList: " + errorShop + "=" + errorNum);
                            final int num = Integer.parseInt(errorNum);
                            mRepository.queryAllFood(errorShop, new TakeoutDataSource.LoadAllSellerCallback() {
                                @Override
                                public void onSuccess(List<TakeoutSeller> sellers) {
                                    String error_food_name=sellers.get(sellers.size()-1).food_name;
                                    mRepository.modifyFoodCount(num, error_food_name, new TakeoutDataSource.SaveOverCallback() {
                                        @Override
                                        public void onSuccess() {
                                            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.takeout_modify_result), true);
                                            onResumeDataShow();
                                            carInfo();
                                        }
                                    });
                                }

                                @Override
                                public void onError() {}
                            });
                        }
                    }
                }catch (JSONException e1){
                    Toast.makeText(mContext, getResources().getString(R.string.takeout_modify_goods_count), Toast.LENGTH_SHORT).show();
                }
            }
        }catch(JSONException e) {
            try {
                mJsonData = JsonUtil.createJsonData(json);
                TTSManager.getInstance(getBaseContext()).speak(mJsonData.getTts(), true);
                jsonObject = new JSONObject(json);
                JSONArray menuList = jsonObject.getJSONObject("data").getJSONObject("content").getJSONObject("summary").getJSONArray("menuList");
                final double agent_fee = jsonObject.getJSONObject("data").getJSONObject("content").getJSONObject("summary").getDouble("agent_fee");
                final double no_agent_fee_total = jsonObject.getJSONObject("data").getJSONObject("content").getJSONObject("summary").getDouble("no_agent_fee_total");
                for (int i = 0; i < menuList.length(); i++) {
                    JSONObject obj = menuList.getJSONObject(i);

                    final String name = obj.getString("name");
                    final String takeoutNum = obj.getString("takeoutNum");
                    final double price = obj.getDouble("price");
                    final String imageUrl = obj.getString("image_url");
                    final double packing_fee = obj.getDouble("packing_fee");
                    final int activity_max_quantity = obj.getInt("activity_max_quantity");
                    final double old_price = obj.getInt("original_price");
                    Log.d(TAG, "parseToMenuList: ");
                    Log.d(TAG, "parseToMenuList: "+name+"-"+takeoutNum);
                    if (Integer.parseInt(takeoutNum) <= 300) {
                        mRepository.findFood(name, new TakeoutDataSource.LoadSellerCallback() {
                            @Override
                            public void onSuccess(TakeoutSeller seller) {
                                Log.d(TAG, "语音购买商品-已购买: " + name);
                                if (seller.count < 300) {
                                    mRepository.updateFoodCount(Integer.parseInt(takeoutNum), name, new TakeoutDataSource.SaveOverCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "parseToMenuList: 数据存储结束");
                                            onResumeDataShow();
                                            carInfo();
                                        }
                                    });
                                } else {
                                    Toast.makeText(mContext, getResources().getString(R.string.takeout_add_more), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "语音购买商品-还未买: " + name);
                                TakeoutSeller seller = new TakeoutSeller();
                                seller.seller_name = mCurrentSeller;
                                seller.food_name = name;
                                seller.food_image = imageUrl;
                                seller.count = Integer.parseInt(takeoutNum);
                                seller.price = price;
                                seller.agent_fee = agent_fee;
                                seller.packing_fee = packing_fee;
                                seller.no_agent_fee_total = no_agent_fee_total;
                                seller.old_price = old_price;
                                seller.activity_max_quantity = activity_max_quantity;
                                mRepository.insertTakeoutSeller(seller, new TakeoutDataSource.SaveOverCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "parseToMenuList: 数据存储结束");
                                        onResumeDataShow();
                                        carInfo();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.takeout_add_more), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {back();}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpUtils.put(mContext,Constant.TAKEOUT_SELLER_SP,"");
        if (mHandler!=null){mHandler.removeCallbacksAndMessages(null);mHandler=null;}
        if (mRepository!=null){mRepository=null;}
        if (mConn!=null){mConn=null;}
        if (mIService!=null){mIService=null;}
        if (mMenuLists!=null){mMenuLists.clear();mMenuLists=null;}
        if (mMenus!=null){mMenus.clear();mMenus=null;}
        if (mActivities!=null){mActivities.clear();mActivities=null;}
        if (mSummary!=null){mSummary=null;}
        if (mTMContent!=null){mTMContent=null;}
        if (mContext!=null){mContext=null;}
        if (mEntity!=null){mEntity=null;}
        if (mTakeoutmenuEntities!=null){mTakeoutmenuEntities.clear();mTakeoutmenuEntities=null;}
        if (mTMResponse!=null){mTMResponse=null;}
        if (mLayoutManager!=null){mLayoutManager=null;}
    }

    /**
     * cmd指令处理
     * @param type 类型
     * @param tts 语音
     */
    private void dealOtherAction(String type, String tts) {
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        switch (type){
            case "next_page"://下一页
                next(tts);
                sendBroadcast(intent);
                break;
            case "last_page"://上一页
                pre(tts);
                sendBroadcast(intent);
                break;
            case "open_cart"://购物车
                intoCarPager();
                break;
            case "back"://返回
                TTSManager.getInstance(getBaseContext()).speak(tts, true);
                TakeoutSellerActivity.this.finish();
                sendBroadcast(intent);
                break;
            case "bill"://结算
            case "confirm_order"://确认订单-下单-买单
            case "confirm_seat"://提交订单
                balance();
                sendBroadcast(intent);
                break;
            default:
                sendBroadcast(intent);
                break;
        }
    }

    /**
     * 下一页
     * @param tts 语音回复内容
     */
    private void next(String tts) {
        int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int count = lastItemPosition - firstItemPosition;
        Log.d(TAG, "next: "+ lastItemPosition);
        if (lastItemPosition!=mMenus.size()-1) {
            mLayoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
            mLayoutManager.setStackFromEnd(true);
        }
        if (mMenus.size()-count < lastItemPosition){
            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.takeout_skip_ending),true);
        }else {
            TTSManager.getInstance(getBaseContext()).speak(tts,true);
        }
    }

    /**
     * 上一页
     * @param tts 语音回复内容
     */
    private void pre(String tts) {
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
        int count = lastItemPosition-firstItemPosition;
        Log.d(TAG, "pre: " + firstItemPosition + "-" +lastItemPosition);
        if (firstItemPosition>count){
            mLayoutManager.scrollToPositionWithOffset(firstItemPosition-count, 0);
            TTSManager.getInstance(getBaseContext()).speak(tts,true);
        }else {
            mLayoutManager.scrollToPositionWithOffset(0, 0);
            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.takeout_skip_starting),true);
        }
        mLayoutManager.setStackFromEnd(true);
    }

    class MyAdapter extends BaseAdapter {
        private int[] mTitleBg=new int[]{R.drawable.takeout_seller_free_1
                , R.drawable.takeout_seller_free_2, R.drawable.takeout_seller_free_3
                , R.drawable.takeout_seller_free_4, R.drawable.takeout_seller_free_5};
        private String[] mTitle=new String[]{"满减","首单","特价","减赠","折扣"};

        @Override
        public int getCount() {return mActivities.size();}

        @Override
        public Object getItem(int position) {return mActivities.get(position);}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder=null;
            if (convertView==null){
                holder=new Holder();
                convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.takeout_seller_free_item,parent,false);
                holder.mTitle=convertView.findViewById(R.id.takeout_seller_free_item_activity);
                holder.mDes=convertView.findViewById(R.id.takeout_seller_free_item_name);
                convertView.setTag(holder);
            }else {
                holder= (Holder) convertView.getTag();
            }
            int i=new Random().nextInt(5);
            holder.mTitle.setText(mTitle[i]);
            holder.mTitle.setBackgroundResource(mTitleBg[i]);
            holder.mDes.setText(mActivities.get(position).getName()+"    ");
            return convertView;
        }
    }

    static class Holder{
        TextView mTitle;
        TextView mDes;
    }
}
