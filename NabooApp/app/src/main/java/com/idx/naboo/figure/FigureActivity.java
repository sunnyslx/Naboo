package com.idx.naboo.figure;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.figure.data.Card;
import com.idx.naboo.figure.data.Figure;
import com.idx.naboo.figure.data.ImoranResponseFigure;
import com.idx.naboo.figure.data.Personal;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.map.mapUtils.ToastUtil;
import com.idx.naboo.news.util.MyTextView;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.utils.RoundImageUtils;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FigureActivity extends BaseActivity implements TTSManager.Callback{

    private IService mIService;
    private List<Figure> mFigures = new ArrayList<>();
    private List<Personal> mPersonals = new ArrayList<>();
    private List<TextView> mTextViews = new ArrayList<>();
    private List<String> figureInfo = new ArrayList<>();
    private ImoranResponseFigure mImoranResponseFigure;//人物数据封装

    private LinearLayout mLinearLayout;
    private NestedScrollView mScrollView;
    private ImageView mFigureImageView;
    private TextView mFigureName;
    private MyTextView mFigureExperience;
    private TextView mLoading;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView;

    private String figureJson;//人物json
    private String constellation;
    private String bloodType;
    private String height;
    private String weight;
    private String bornDay;
    private String mFigureBirthday;
    private String info;
    private String figureExperience;//人物简介
    private String name;
    private String tts;
    //控制自动滚动的线程
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    handler.removeCallbacks(ScrollRunnable);
                    break;
                default:
                    break;
            }
        }
    };
    //记录播放进度
    private int progress;
    //暂存进度
    private int stagingProgress;
    private int cun;
    private int i;
    private int progressIf;
    //每一行的高度
    private int autoScrollY;
    //只计算一次新闻的相关参数
    private int once;
    private int off;
    private int delayTime;
    private double averageLineSize;
    //获取手指在屏幕上下滑动
    private float y1;
    private float y2;
    /*
     * 设置播报状态值:
     * Error: -1
     * Play： 0
     * Stop： 1
     * Pause： 2
     * NoProgress: 3
     * End: 5
     * UnSupportCommand： 6
     */
    private int playState;
    //每一行播报平均时间长
    private int averageTime;
    //计算自动滑动的总高度
    private int sun;

    //人物图片下方信息个数
    private int count;

    private String mQueryId;
    private String mPageId;
    //临时json
    private String temporaryJson;

    private static final String TAG = "FigureActivity";

    //拿到service的连接
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "进入 onServiceConnected: ");
            mIService = (IService) iBinder;
            mIService.setDataListener(FigureActivity.this);
            //拿到的人物json为空时的处理
            if ((mIService.getJson()).equals("") || mIService.getJson() == null) {
                if (temporaryJson != null) {
                    scenario(temporaryJson);
                    if ((stagingProgress + progress - 6) < figureExperience.length() && mLoading.getVisibility() == View.GONE && playState == 0) {
                        //当进度值大于追加长度时，恢复播报进度 = 当前进度长度 - 追加长度；
                        //当进度值小于追加长度时，从头开始播报；
                        if ((stagingProgress + progress) > 6) {
                            cun = stagingProgress + progress - 6;
                        } else {
                            cun = 0;
                        }
                        playState = 0;
                        handler.postDelayed(ScrollRunnable, 3600);
                        TTSManager.getInstance(getBaseContext()).speak(figureExperience.substring(cun), FigureActivity.this, false);
                    } else {
                        mScrollView.scrollTo(0, 0);
                    }
                }else {
                    finish();
                }
            } else {
                figureJson = mIService.getJson();
                Log.d(TAG, "onServiceConnected: figureJSON = " + figureJson);
                //人物json不为空对json分类处理
                mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(figureJson);
                if (mImoranResponseFigure != null && !mImoranResponseFigure.getFigureData().getFigureContent().getType().equals("")) {
                    //判断当前有是person_info 、 people_detail 、 people_detail_relation 、 person 、 default中的哪个json层
                    if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure() != null) {
                        //存在person_info，长度为2时无内容，不为2时有内容
                        if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure().toString()).length() == 2) {
                            Log.d(TAG, "getFigure: getFigure if");
                            //无内容
                            noMessage();
                        } else {
                            Log.d(TAG, "getFigure: getFigure else");
                            tts = "";
                            //person_info处理内容
                            parseToFigure(figureJson);
                            //插入数据
                            initData();
                        }
                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail() != null) {
                        //存在people_detail，长度为2时无内容，不为2时有内容
                        if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail().toString()).length() == 2) {
                            Log.d(TAG, "onCreate: getDetail if");
                            noMessage();
                        } else {
                            Log.d(TAG, "onCreate: getDetail else");
                            //显示页面加载中，继续执行
                            messageLoading();
                        }

                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation() != null) {
                        //存在people_detail_relation，长度为2时无内容，不为2时有内容
                        if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation().toString()).length() == 2) {
                            Log.d(TAG, "onCreate: getRelation if");
                            noMessage();
                        } else {
                            Log.d(TAG, "onCreate: getRelation  else");
                            messageLoading();
                        }
                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople() != null) {
                        //存在person，长度为2时无内容，不为2时有内容
                        if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople().toString()).length() == 2) {
                            Log.d(TAG, "onCreate: getPeople if");
                            noMessage();
                        } else {
                            Log.d(TAG, "onCreate: getPeople  else");
                            messageLoading();
                        }
                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure() != null) {
                        //存在default，长度为2时无内容，不为2时有内容
                        if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure().toString()).length() == 2) {
                            Log.d(TAG, "onCreate: getPeople if");
                            noMessage();
                        } else {
                            Log.d(TAG, "onCreate: getPeople else");
                            messageLoading();
                        }
                    }
                } else {
                    noMessage();
                }
                if (mImoranResponseFigure != null && mImoranResponseFigure.getFigureData().getFigureContent().getType() != null) {
                    //判断当前people_detail 、 people_detail_relation 、 person 、 default 是哪个json层，不为2时有内容，此时拿人物名字再次请求蓦然
                    if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail() != null
                            && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail().toString()).length() != 2) {
                        Log.d(TAG, "onServiceConnected: if getDetail");
                        peopleDetail();
                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation() != null
                            && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation().toString()).length() != 2) {
                        Log.d(TAG, "onServiceConnected: else getRelation");
                        peopleRelation();
                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople() != null
                            && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople().toString()).length() != 2) {
                        Log.d(TAG, "onServiceConnected: else getPeople");
                        people();
                    } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure() != null
                            && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure().toString()).length() != 2) {
                        Log.d(TAG, "onServiceConnected: else getPeople");
                        defaultFigure();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onJsonReceived(String json) {
        Log.e(TAG, "进入onJsonReceived: ");
        //拿到service的json文件
        figureJson = json;
        mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(figureJson);
        //当json内容不为空时，判断是指令还是人物信息
        if (mImoranResponseFigure != null && !mImoranResponseFigure.getFigureData().getFigureContent().getType().equals("")) {
            //当domain为cmd时收到的是指令
            if (mImoranResponseFigure.getFigureData().getDomain().equals("cmd")) {
                chargeCmd(figureJson);
            } else {
                //收到的是人物信息，则清空之前的人物信息
                removeFigureInfo();
                //判断当前有是person_info 、 people_detail 、 people_detail_relation 、 person 、 default中的哪个json层
                if (ImoranResponseToBeanUtils.isImoranFigureNull(mImoranResponseFigure)) {
                    //当为person_info且长度为2时，无内容；否则有数据则解析数据，并将数据放入view
                    if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure()).toString().length() == 2) {
                        noMessage();
                    } else {
                        tts = "";
                        parseToFigure(figureJson);
                        initData();
                    }

                } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail() != null) {
                    //判断当前是否是people_detail ，内容长度不为2时，说明有内容，此时拿人物名字再次请求蓦然
                    if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail().toString()).length() == 2) {
                        noMessage();
                    } else {
                        messageLoading();
                        peopleDetail();
                    }
                } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation() != null) {
                    //判断当前是 people_detail_relation json层，内容长度不为2时，说明有内容，此时拿人物名字再次请求蓦然
                    if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation().toString()).length() == 2) {
                        noMessage();
                    } else {
                        messageLoading();
                        peopleRelation();
                    }
                } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople() != null) {
                    //判断当前是 person json层，内容长度不为2时，说明有内容，此时拿人物名字再次请求蓦然
                    if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople().toString()).length() == 2) {
                        noMessage();
                    } else {
                        messageLoading();
                        people();
                    }
                } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure() != null) {
                    //判断当前是 default 是哪个json层，内容长度不为2时，说明有内容，此时拿人物名字再次请求蓦然
                    if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure().toString()).length() == 2) {
                        noMessage();
                    } else {
                        messageLoading();
                        defaultFigure();
                    }
                }

            }
        }else {
            noMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figure);
        initView();
    }
    private void initView() {
        mLinearLayout = findViewById(R.id.figure_layout);
        mLoading = findViewById(R.id.figure_loading);
        mScrollView = findViewById(R.id.figure_scroll);
        mFigureName = findViewById(R.id.figure_name);
        mFigureExperience = findViewById(R.id.figure_experience);
        mFigureImageView = findViewById(R.id.figure_pic);
        //将显示人物属性的组件绑定
        mTextView1 = findViewById(R.id.figure_info1);
        mTextViews.add(mTextView1);
        mTextView2 = findViewById(R.id.figure_info2);
        mTextViews.add(mTextView2);
        mTextView3 = findViewById(R.id.figure_info3);
        mTextViews.add(mTextView3);
        mTextView4 = findViewById(R.id.figure_info4);
        mTextViews.add(mTextView4);
        mTextView = findViewById(R.id.figure_info);
    }
    private void initData() {
        figureExperience = mPersonals.get(0).getBrief();
        //人物姓名
        mFigureName.setText(mPersonals.get(0).getName());
        //人物详情
        mFigureExperience.setText(figureExperience);
        //加载人物图片
        Glide.with(this).load(mPersonals.get(0).getPic())
                .transform(new RoundImageUtils(this, getResources().getDimensionPixelSize(R.dimen.figure_image_radius)))
                .error(R.drawable.figure_defult)
                .into(mFigureImageView);
        mLoading.setVisibility(View.GONE);
        mFigureName.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        name = mPersonals.get(0).getName();
        //将上一个语音停止
        TTSManager.getInstance(getBaseContext()).stop();
        progress = 0;
        playState = 0;
        TTSManager.getInstance(getBaseContext()).speak(tts+"。"+name+ getString(R.string.figure_details)+figureExperience,this,false);
        //接收数据成功，发送广播通知service
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
        setFigureInfo();
        //动态插入人物信息，按信息个数动态显示TextView的个数
        if (count == 0){
            mTextView1.setVisibility(View.GONE);
            mTextView2.setVisibility(View.GONE);
            mTextView3.setVisibility(View.GONE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 1){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.GONE);
            mTextView3.setVisibility(View.GONE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 2){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
            mTextView3.setVisibility(View.GONE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 3){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
            mTextView3.setVisibility(View.VISIBLE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 4){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
            mTextView3.setVisibility(View.VISIBLE);
            mTextView4.setVisibility(View.VISIBLE);
        }
        if (mTextView == null){
            mTextView .setVisibility(View.GONE);
        }else {
            mTextView.setVisibility(View.VISIBLE);
        }
        //播放进度相关状态初始化
        mScrollView.scrollTo(0,0);
        sun = 0;
        i = 0;
        cun = 0;
        progressIf = 0;
        once = 0;
        stagingProgress = 0;
        //将上一次Handler移除
        handler.removeCallbacksAndMessages(null);
        //延迟执行当前Handler
        handler.postDelayed(ScrollRunnable,10000);
        //监听NestedScrollView滑动状态。动态改变自定义标题栏的背景色
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >0) {
                    mFigureName.setBackgroundColor(getResources().getColor(R.color.figure_name_background_color));
                }else {
                    mFigureName.setBackgroundColor(0);
                }
            }
        });
        //监听手指在NestedScrollView的滑动
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    y1 = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    y2 = event.getY();
                    //若此时为play状态，监听到滑动立即将语音停止，将Handler移除，并将状态置为stop
                    if (playState == 0) {
                        if ((y2 - y1) > 5 || (y1 - y2) > 5) {
                            i++;
                            playState = 1;
                            TTSManager.getInstance(getBaseContext()).stop();
                            handler.removeCallbacksAndMessages(null);
                            if (i == 1) {
                                ToastUtil.showToast(FigureActivity.this, getString(R.string.auto_reader_stop));
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * 解析json数据
     * @param json 拿到json数据
     */
    private void parseToFigure(String json) {
        mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(json);
        if (ImoranResponseToBeanUtils.isImoranFigureNull(mImoranResponseFigure)) {
            mFigures = mImoranResponseFigure.getFigureData()
                    .getFigureContent().getFigureReply().getFigure();
            mPersonals = mFigures.get(0).getPersonal();
            mFigureBirthday = mPersonals.get(0).getBirthday();
            info = mPersonals.get(0).getBaikeInfo();
            temporaryJson = json;
            scenario(temporaryJson);
            //提取人物百科中 生日、星座、身高、血型、体重信息
            if (info != null && info.length() > 0) {
                JSONObject object = null;
                List<Card> cards = new ArrayList<>();
                count = 0;
                try {
                    object = new JSONObject(info);
                    JSONArray array = object.getJSONArray("card");
                    //遍历拿到的array
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        Card card = new Card();
                        card.setKey(object1.getString("key"));
                        card.setValue(object1.getString("value"));
                        cards.add(card);
                    }

                    for (Card card : cards) {
                        switch (card.getKey()) {
                            //星座
                            case "m1_constellation":
                                constellation = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //血型
                            case "m1_bloodtype":
                                bloodType = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //身高
                            case "m1_height":
                                height = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //体重
                            case "m1_weight":
                                weight = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //出生日期
                            case "m1_bornDay":
                                bornDay = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                boolean status = bornDay.contains("（");
                                if (status) {
                                    String b = bornDay.substring(bornDay.indexOf("（"), bornDay.indexOf("）") + 1);
                                    bornDay = bornDay.replace(b, "");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理指令
     * @param json 指令json
     */
    private void chargeCmd(String json) {
        try {
            //获取指令
            String type = ImoranResponseToBeanUtils.handleNewsType(json);
            //接收数据成功
            Intent intent = new Intent(Execute.ACTION_SUCCESS);
            sendBroadcast(intent);
            switch (type) {
                case "continue":
                    //继续播放
                    resume();
                    break;
                case "pause":
                    //暂停
                    pause();
                    break;
                case "back":
                    //返回
                    back();
                    break;
                default:
                    //不支持指令
                    unDo();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回功能
    private void back(){
        finish();
    }

    //不支持指令
    private void unDo() {
        //给一个状态，使得播报语音时不监听状态
        playState = 6;//unSupportCommand不监听指令
        //播报语音
        TTSManager.getInstance(getBaseContext()).speak(getString(R.string.no_support_cmd), this,true);
    }

    //提示已暂停
    private void pause() {
        playState = 2;//pause状态
        Toast.makeText(FigureActivity.this, getString(R.string.figure_pause), Toast.LENGTH_SHORT).show();
        handler.removeCallbacksAndMessages(null);
        TTSManager.getInstance(getBaseContext()).speak(getString(R.string.figure_pause), this, true);
    }

    //继续播报新闻
    private void resume() {
        //若当前状态为stop时，提示已经为停止状态
        if (playState == 1){
            TTSManager.getInstance(getBaseContext()).speak(getString(R.string.auto_reader_stop), this, true);
        }
        //当前语音状态为暂停时，继续播报
        if (playState == 0 || playState == 2 || playState == 6){
            //找到暂停时的位置，及播报进度，并重新将状态置为play
            if(cun != (stagingProgress + progress) && (stagingProgress + progress) < figureExperience.length()) {
                if ((stagingProgress + progress) > 6) {
                    cun = stagingProgress + progress - 6;
                }else {
                    cun = 0;
                }
                playState = 0;//play 状态
                mScrollView.scrollTo(0, sun);
                handler.post(ScrollRunnable);
                TTSManager.getInstance(getBaseContext()).speak(figureExperience.substring(cun), this,false);
            }
        }
    }

    //加入人物百科中 生日、星座、身高、血型、体重信息
    private void setFigureInfo(){
        if (height != null){
            figureInfo.add(height);
        }
        if (weight != null){
            figureInfo.add(weight);
        }
        if(constellation != null){
            figureInfo.add(constellation);
        }
        if (bloodType != null){
            figureInfo.add(bloodType);
        }
        if (count > 0){
            for (int i=0; i<count; i++){
                mTextViews.get(i).setText(figureInfo.get(i));
            }
        }
        if (bornDay != null) {
            mTextView.setText(bornDay);
        }else if (mFigureBirthday != null){
            mTextView.setText(mFigureBirthday);
        }
    }
    //清空人物信息
    private void removeFigureInfo(){
        for (int i=0; i<count; i++){
            mTextViews.get(i).setText("");
        }
        mTextView.setText("");
        count = 0;
        constellation = null;
        height = null;
        weight = null;
        bloodType = null;
        bornDay = null;
        mFigureBirthday = null;
        figureInfo.clear();
        mFigureName.setText("");
        mFigureExperience.setText("");
    }

    /**
     * 查询的是相关人物的信息
     */
    private void peopleRelation(){
        if(mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation().get(0).getName() !=  null){
            if (name!= null){
                name = null;
                tts = null;
            }
            name = mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation().get(0).getName();
            tts = mImoranResponseFigure.getFigureData().getFigureContent().getTts();
            //拿到人物的姓名请求蓦然
            mIService.requestData(name + getString(R.string.figure_who), new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    //处理蓦然返回的json
                    mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(s);
                    if (mImoranResponseFigure != null) {
                        if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation() == null) {
                            if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure() != null
                                    && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure().toString()).length() != 2) {
                                parseToFigure(s);
                                initData();
                            } else {
                                noMessage();
                            }
                        } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation() != null) {
                            if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getRelation().toString()).length() == 2) {
                                noMessage();
                            } else {
                                parseToFigure(s);
                                initData();
                            }
                        }
                    }else {
                        noMessage();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }else {
            noMessage();
        }
    }

    /**
     * 查询的是相关人物的信息
     */
    private void people(){
        if(mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople().get(0).getName() !=  null){
            if (name!= null){
                name = null;
                tts = null;
            }
            name = mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople().get(0).getName();
            tts = mImoranResponseFigure.getFigureData().getFigureContent().getTts();
            //拿到人物的姓名，请求蓦然
            mIService.requestData(name + getString(R.string.figure_who), new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    //处理蓦然返回的json
                    mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(s);
                    if (mImoranResponseFigure != null) {
                        if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople() == null) {
                            if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure() != null
                                    && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure().toString()).length() != 2) {
                                parseToFigure(s);
                                initData();
                            } else {
                                noMessage();
                            }
                        } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople() != null) {
                            if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getPeople().toString()).length() == 2) {
                                noMessage();
                            } else {
                                parseToFigure(s);
                                initData();
                            }
                        }
                    }else {
                        noMessage();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }else {
            noMessage();
        }
    }

    /**
     * 查询的是人物的详细信息
     */
    private void peopleDetail(){
        if(mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail() !=  null){
            if (name!= null){
                name = null;
                tts = null;
            }
            name = mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail().get(0).getName();
            tts = mImoranResponseFigure.getFigureData().getFigureContent().getTts();
            //拿到人物的姓名去请求蓦然
            mIService.requestData(name + getString(R.string.figure_who), new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    //处理返回的json文件
                    mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(s);
                    if (mImoranResponseFigure != null) {
                        if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail() == null) {
                            if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure() != null
                                    && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure().toString()).length() != 2) {
                                parseToFigure(s);
                                initData();
                            } else {
                                noMessage();
                            }
                        } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail() != null) {
                            if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDetail().toString()).length() == 2) {
                                noMessage();
                            } else {
                                parseToFigure(s);
                                initData();
                            }
                        }
                    }else {
                        noMessage();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
        else {
            noMessage();
        }
    }

    /**
     * 查询的是人物
     */
    private void defaultFigure(){
        if(mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure() !=  null){
            if (name!= null){
                name = null;
                tts = null;
            }
            String[] n = mImoranResponseFigure.getFigureData().getFigureContent().getSemantic().getPerson();
            name = n[0];
            tts = mImoranResponseFigure.getFigureData().getFigureContent().getTts();
            //拿人物的名字再次去请求蓦然
            mIService.requestData(name + getString(R.string.figure_who), new NLIRequest.onRequest() {
                @Override
                public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                    //判断返回的json文件进行处理
                    mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(s);
                    if (mImoranResponseFigure != null) {
                        if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure() == null) {
                            if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure() != null
                                    && (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure().toString()).length() != 2) {
                                parseToFigure(s);
                                initData();
                            } else {
                                noMessage();
                            }
                        } else if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure() != null) {
                            if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getDefaultFigure().toString()).length() == 2) {
                                noMessage();
                            } else {
                                parseToFigure(s);
                                initData();
                            }
                        }
                    }else {
                        noMessage();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
        else {
            noMessage();
        }
    }

    /**
     * 所有信息都为空
     */
    private void noMessage(){
        playState = 3;
        tts = getString(R.string.no_message);
        mLoading.setText(tts);
        TTSManager.getInstance(getBaseContext()).speak(tts, this,true);
        //接收数据成功
        Intent intent = new Intent(Execute.ACTION_SUCCESS);
        sendBroadcast(intent);
        mLoading.setVisibility(View.VISIBLE);
        mFigureName.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * 界面显示加载中
     */
    private void messageLoading(){
        mLoading.setText(getString(R.string.figure_message_loading));
        mFigureName.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 场景
     */
    private void scenario(String json){
        JsonData jsonData = JsonUtil.createJsonData(json);
        mQueryId = jsonData.getQueryId();
        String domain = jsonData.getDomain();
        String intention = jsonData.getIntention();
        String type = jsonData.getType();
        mPageId = domain +"_"+ intention +"_"+ type +"_"+"FigureActivity";
        VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
    }

    //实现scrollview的自动滚动
    private Runnable ScrollRunnable = new Runnable() {

        @Override
        public void run() {
            if (once == 0) {
                //计算需要滚动的高度
                off = mFigureExperience.getMeasuredHeight() - mScrollView.getHeight();
                //计算一行内容的高度
                autoScrollY = mFigureExperience.getMeasuredHeight() / mFigureExperience.getLineCount();
                Log.d(TAG, "run: content_length = "+mFigureExperience.length());
                //读一行需要花的大概时间
                averageLineSize = new BigDecimal((float) mFigureExperience.length() / mFigureExperience.getLineCount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                averageTime = (int) (averageLineSize * 200);
                delayTime = averageTime / autoScrollY;
                once++;
            }
            //当高度大于0时，进入
            if (playState == 0) {
                if (off > 0) {
                    mScrollView.scrollBy(0, 1);
                    sun = sun + 1;
                    if (progressIf == 1){
                        int y = (int) (((stagingProgress+progress)/averageLineSize - 3) * autoScrollY);//应该滚动到的位置
                        Log.d(TAG, "run: y = "+y+"   sun = "+sun +"    autoScrollY = "+autoScrollY);
                        if ((sun - autoScrollY) > y){
                            delayTime = delayTime + 5;
                            Log.d(TAG, "run: delayTime = "+delayTime);
                        }else if ((y - autoScrollY) > sun){
                            delayTime = delayTime - 5;
                            Log.d(TAG, "run: delayTime = "+delayTime);
                        }else {
                            if (delayTime-5 >(averageTime/autoScrollY)){
                                delayTime = delayTime - 5;
                                Log.d(TAG, "run: delayTime = "+delayTime);
                            }else if (delayTime < (averageTime/autoScrollY)-5){
                                delayTime = delayTime + 5;
                                Log.d(TAG, "run: delayTime = "+delayTime);
                            }else {
                                delayTime = averageTime / autoScrollY;
                                Log.d(TAG, "run: delayTime = "+delayTime);
                            }
                        }
                        progressIf = 0;
                    }
                    //当滚动高度大于等于高度差时，退出线程；否则继续滚动
                    if (sun >= off) {
                        Message message = new Message();
                        message.what = -1;
                        handler.sendMessage(message);
                    } else {
                        Log.d(TAG, "run: delayTime = "+delayTime);
                        handler.postDelayed(this, delayTime);
                    }
                }
            }
            //播报出错
            if (playState == -1){
                TTSManager.getInstance(getBaseContext()).stop();
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(FigureActivity.this, getString(R.string.broadcast_failure), Toast.LENGTH_LONG).show();
            }
            if (playState == 5){
                TTSManager.getInstance(getBaseContext()).stop();
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(FigureActivity.this, getString(R.string.news_broadcast_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onResume() {
        bindService(new Intent(getBaseContext(), SpeakService.class), connection, BIND_AUTO_CREATE);
        super.onResume();
    }

    /**
     * 解绑连接
     * 停止语音
     * 移除线程
     */
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        TTSManager.getInstance(getBaseContext()).stop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSpeechProgressChanged(String ss, int i) {
        //为play状态时监听进度，其他状态不监听
        if (playState == 0){
            if (progress>i){
                stagingProgress = stagingProgress + progress;
                Log.d(TAG, "onSpeechProgressChanged: stagingProgress = "+stagingProgress);
            }
            progress = i;
            Log.d(TAG, "onSpeechProgressChanged: i = "+i);
            if ((stagingProgress+progress)%50 == 0){
                if (cun < stagingProgress + progress ) {
                    cun = stagingProgress+progress;
                    progressIf = 1;
                }
            }
        }
    }


    @Override
    public void onPlayBegin(String s) {
        Log.e(TAG, "onPlayBegin: playState"+playState);
    }

    @Override
    public void onPlayEnd(String s) {
        if (playState == 0){
            playState = 5;
        }
    }

    @Override
    public void onPlayStopped(String s) {
        Log.d(TAG, "onPlayStopped: ");
    }

    @Override
    public void onError(@Nullable String s, int i, String s1) {
        Log.d(TAG, "onError: 播报发生错误 ");
        if (playState == 0){
            playState = -1;
        }
    }


    @Override
    public void onSessionStateChanged(SessionState state) {
        super.onSessionStateChanged(state);
        //当状态是play时，state状态为START移除线程，state状态为END继续播报
        if (progress != 0){
            switch (state){
                case START:
                    handler.removeCallbacksAndMessages(null);
                    break;
                case END:
                    //当状态为Play： 0; UnSupportCommand： 6; 恢复语音播报，并将状态置为播报状态
                    if ((playState == 0 || playState == 6) && stagingProgress+progress < figureExperience.length()) {
                        handler.post(ScrollRunnable);
                        playState = 0;
                        TTSManager.getInstance(getBaseContext()).speak(figureExperience.substring(stagingProgress+progress), FigureActivity.this, false);
                    }
                    break;
            }
        }
    }
}
