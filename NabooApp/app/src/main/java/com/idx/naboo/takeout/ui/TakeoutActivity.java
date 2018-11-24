package com.idx.naboo.takeout.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.adapter.TakeoutAdapter;
import com.idx.naboo.takeout.data.takeout.ImoranTSResponse;
import com.idx.naboo.takeout.data.takeout.TSContent;
import com.idx.naboo.takeout.data.takeout.TakeoutShop;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.takeout.utils.ImoranResponseParseUtil;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.sdk.bean.base.BaseContentEntity;
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
import java.util.List;

/**
 * 外卖主页面
 * Created by danny on 4/18/18.
 */

public class TakeoutActivity extends BaseActivity {
    private static final String TAG = TakeoutActivity.class.getSimpleName();
    private IService mIService;
    private Context mContext;
    private String mJson;

    //解析蓦然json数据
    private JsonData mJsonData;//Imoran返回基础数据
    private ImoranTSResponse mTSResponse;//外卖数据封装bean
    private TSContent mTSContent;
    private List<TakeoutShop> mShops;
    private TakeoutAdapter mTakeoutAdapter;

    //view
    private RecyclerView mRecyclerView;
    private LinearLayout mTextView;//小富提示
    private LinearLayout mUnNetwork;
    private LinearLayoutManager mLayoutManager;

    private String mQueryId;
    private String mPageId;

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(TakeoutActivity.this);
            //获取json
            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())){
                Log.i(TAG, "first bind service");
                mJson = mIService.getJson();
                SpUtils.put(mContext, Constant.TAKEOUT_SHOP,mJson);
                //置空Json
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
                //解析Json
                parseToTS(mJson);
                playTTS();
            }else {
                mJson=SpUtils.get(mContext,Constant.TAKEOUT_SHOP,"");
                //解析Json
                parseToTS(mJson);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {if (mIService!=null)mIService=null;}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeout);
        mContext=this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class),mConn,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTSManager.getInstance(getBaseContext()).stop();
        unbindService(mConn);
        ((NabooApplication) getApplication()).getDataEntity().setScenes(null);
        VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), "pageId_default", "");
    }

    @Override
    public void onJsonReceived(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String domain=jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            String tts = jsonObject.getJSONObject("data").getJSONObject("content").getString("tts");
            Log.d(TAG, "onJsonReceived: "+type);
            if (type.equals("takeoutshop")){
                mJsonData= JsonUtil.createJsonData(json);
                parseToTS(json);
                SpUtils.put(mContext,Constant.TAKEOUT_SHOP,json);
                TTSManager.getInstance(getBaseContext()).speak(mTSContent.getTTS(),true);
            }else if (domain.equals("cmd")){
                dealOtherAction(type,tts);
            }
            //置空Json
            Intent intent = new Intent(Execute.ACTION_SUCCESS);
            sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * cmd指令处理
     * @param type cmd类型
     * @param tts 语音
     */
    private void dealOtherAction(String type,String tts) {
        switch (type){
            case "next_page"://下一页
                next(tts);
                break;
            case "last_page"://上一页
                pre(tts);
                break;
            case "back"://返回
                TakeoutActivity.this.finish();
                TTSManager.getInstance(getBaseContext()).speak(tts,true);
                break;
            default:
                break;
        }
    }

    /**
     * 下一页
     * @param tts 语音回复内容
     */
    private void next(String tts) {
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
        int count = lastItemPosition - firstItemPosition;
        Log.d(TAG, "next: " + lastItemPosition);
        if (lastItemPosition!=mShops.size()-1) {
            mLayoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
            mLayoutManager.setStackFromEnd(true);
        }
        if (mShops.size()-count < lastItemPosition){
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
        Log.d(TAG, "pre: " + firstItemPosition);
        if (firstItemPosition>count){
            mLayoutManager.scrollToPositionWithOffset(firstItemPosition-count, 0);
            TTSManager.getInstance(getBaseContext()).speak(tts,true);
        }else {
            mLayoutManager.scrollToPositionWithOffset(0, 0);
            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.takeout_skip_starting),true);
        }
        mLayoutManager.setStackFromEnd(true);
    }

    /**
     * 后退键处理
     */
    private void back() {
        TakeoutActivity.this.finish();
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
        SpUtils.put(mContext, Constant.TAKEOUT_SHOP,"");
        if (mConn!=null){mConn=null;}
        if (mLayoutManager!=null){mLayoutManager=null;}
        if (mShops!=null){mShops.clear();mShops=null;}
        if (mContext!=null){mContext=null;}
        if (mTSContent!=null){mTSContent=null;}
        if (mTSResponse!=null){mTSResponse=null;}
        if (mIService!=null){mIService=null;}
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
     * 更新界面
     */
    private void  updateView(){
        if(mShops!=null && mShops.size()>0){
            Log.d(TAG, "updateView: "+mShops.size());
            mUnNetwork.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mLayoutManager=new LinearLayoutManager(mContext);
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mTakeoutAdapter=new TakeoutAdapter(mContext,mShops);
            mRecyclerView.setAdapter(mTakeoutAdapter);
            mRecyclerView.setItemViewCacheSize(mShops.size());
            mTakeoutAdapter.setOnItemClickListener(new TakeoutAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    if (mIService!=null){
                        mIService.requestData("第" + (position+1) + "家", new NLIRequest.onRequest() {
                            @Override
                            public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                                Intent intent=new Intent(mContext,TakeoutSellerActivity.class);
                                intent.putExtra("takeout_shop_click",s);
                                startActivity(intent);
                            }

                            @Override
                            public void onError() {}
                        });
                    }
                }
            });
        }
    }

    /**
     * 解析外卖数据
     *
     * @param json Imoran返回数据
     */
    private void parseToTS(String json) {
        mTSResponse = ImoranResponseParseUtil.parseTS(json);
        if (ImoranResponseParseUtil.isImoranTSNull(mTSResponse)) {
            mTSContent = mTSResponse.getData().getTSContent();

            mQueryId = mTSResponse.getData().getQueryid();
            String domain = mTSResponse.getData().getDomain();
            String intention = mTSResponse.getData().getIntention();
            String type=mTSResponse.getData().getTSContent().getType();
            mPageId=domain+"_"+intention+"_"+type+"_"+"takeoutactivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);

            if (mTSContent.getErrorCode() == 0) {
                if (mTSContent.getTSReply().getTakeoutShops() != null && mTSContent.getTSReply().getTakeoutShops().size() > 0) {
                    mShops = mTSContent.getTSReply().getTakeoutShops();
                    for (int i=0;i<mShops.size();i++){
                        Log.i(TAG, "item: "+mShops.get(i).getRestaurantName());
                    }
                    updateView();
                }else {
                    noData();
                }
            }else {
                noData();
            }
        }
    }

    /**
     * Imoran未返回数据
     */
    private void noData(){
        mUnNetwork.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * 初始化布局控件
     */
    private void initView() {
        mRecyclerView=findViewById(R.id.takeout_recycler_view);
        mTextView=findViewById(R.id.takeout_xiao_fu_reply);
        mUnNetwork=findViewById(R.id.takeout_un_network);
    }
}
