package com.idx.naboo.dish;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.dish.data.Dish;
import com.idx.naboo.dish.data.DishContent;
import com.idx.naboo.dish.data.ImoranResponseDish;
import com.idx.naboo.dish.data.MaterialAdapter;
import com.idx.naboo.dish.data.Steps;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.music.utils.ToastUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.Media;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.video.ui.VideoActivity;
import com.idx.naboo.videocall.utils.SpUtils;
import com.idx.naboo.weather.WeatherActivity;

import net.imoran.sdk.entity.info.VUIDataEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_MOVE;

public class DishActivity extends BaseActivity implements ScrollView.OnTouchListener, TTSManager.Callback {
    private final String TAG = DishActivity.class.getSimpleName();
    private IService mIService;
    private String mJson;
    private ImoranResponseDish imoranResponseDish;
    private DishContent dishContent;
    private List<Dish> mDish = new ArrayList<>();
    private List<Steps> mSteps = new ArrayList<>();
    private List<String> mMaterial;
    private TextView mdishName;
    private LinearLayout listView;
    private DishGridView gridView;
    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private JsonData mJsonData;
    //滑动状态
    private boolean isScroll;
    //所有步骤
    private String mStepContent;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    //播放状态
    private static final String TTS_PLAY = "TTS_PLAYING";
    private static final String TTS_PAUSE = "TTS_PAUSE";
    private static final String TTS_ERROR = "TTS_ERROR";
    private String mPlayState;
    //TTS播放进度
    private int mTtsprogress;
    private static final int MSG_ONE = 0x001;
    private StepView stepView;
    private int mCount;
    private int mScrolly;
    private Media.States mLastState = null;
    private boolean isSessionStart;
    private String mPageId;
    private String mQueryId;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(DishActivity.this);
            mJson = mIService.getJson();
            if (mJson != null && (!mJson.equals(""))) {
                parseToDish(mJson);
                parseQueryId(mJson);
                dialogDismiss();
                updateView();
                //延迟一秒播报tts
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isScroll){
                            //食材显示的高度,大于屏幕1/2时,滑动一次
                            if (getGridViewHeight()>=getScreenHeight(DishActivity.this)/2){
                                Message message=Message.obtain();
                                message.what=MSG_ONE;
                                if (handler !=null){
                                    handler.sendMessage(message);
                                }
                            }
                            getTTsStep();
                        }
                    }
                },1000);

            } else {
                Log.i(TAG, "onServiceConnected: json null");
                VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
                dialogDismiss();
                ttsContinue();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onJsonReceived(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (domain.equals("cmd")) {
                mJsonData = JsonUtil.createJsonData(json);
                dealWithCmd();
                getTTs(json);
            } else if (type.equals("dish")) {
                parseToDish(json);
                parseQueryId(json);
                if (mDish != null && mDish.size() > 0) {
                    mScrollView.smoothScrollTo(0, 0);
                    updateView();
                    //延迟一秒播报tts
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isScroll){
                                //食材显示的高度,大于屏幕1/2时,滑动一次
                                if (getGridViewHeight()>=getScreenHeight(DishActivity.this)/2){
                                    Message message=Message.obtain();
                                    message.what=MSG_ONE;
                                    if (handler !=null){
                                        handler.sendMessage(message);
                                    }
                                }
                                getTTsStep();
                            }
                        }
                    },1000);
                } else {
                    noMessage();
                    getTTs(json);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        setListener();
        isScroll = true;
        mTtsprogress = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ttsPause();
        unbindService(connection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isScroll = false;
        if (handler != null) {
            handler = null;
        }
    }

    private void setListener() {
        mdishName = findViewById(R.id.dish_name);
        listView = findViewById(R.id.dish_list);
        gridView = findViewById(R.id.grid_material);
        mScrollView = findViewById(R.id.dish_scrollview);
        mLinearLayout = findViewById(R.id.dish_linear);
        mScrollView.setOnTouchListener(this);
    }

    private void updateView() {
        if (mDish != null && mDish.size() > 0) {
            mdishName.setText(mDish.get(0).getName());
            listView.removeAllViews();
            if (mSteps != null && mSteps.size() > 0) {
                for (int i = 0; i < mSteps.size(); i++) {
                    stepView = new StepView(this);
                    stepView.setText(mSteps.get(i).getDes(), mSteps.get(i).getPic(), mSteps.get(i).getIndex());
                    listView.addView(stepView);
                }
            }
            if (mMaterial != null && mMaterial.size() > 0) {
                MaterialAdapter materialAdapter = new MaterialAdapter(mMaterial);
                gridView.setAdapter(materialAdapter);
                materialAdapter.notifyDataSetChanged();
            }
        } else {
            noMessage();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //表示用户在移动,结束自动播报
        if (motionEvent.getAction() == ACTION_MOVE) {
            if (mPlayState.equals(TTS_PLAY)) {
                TTSManager.getInstance(getBaseContext()).stop();
                mPlayState = TTS_PAUSE;
                mLastState = Media.States.PAUSE;
                ToastUtil.showToast(this, getResources().getString(R.string.dish_pause));
            }
        }
        return false;
    }

    //解析json数据
    private void parseToDish(String json) {
        try {
            imoranResponseDish = ImoranResponseToBeanUtils.handleDishData(json);
            if (ImoranResponseToBeanUtils.isImpranDishNull(imoranResponseDish)) {
                dishContent = imoranResponseDish.getDishData().getDishContent();
                mDish = dishContent.getDishReply().getDishe();
                mSteps = mDish.get(0).getSteps();
                mMaterial = mDish.get(0).getMaterial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加场景
    private void parseQueryId(String json) {
        imoranResponseDish = ImoranResponseToBeanUtils.handleDishData(json);
        if (ImoranResponseToBeanUtils.isImpranDishNull(imoranResponseDish)) {
            mQueryId = imoranResponseDish.getDishData().getQueryid();
            String domain = imoranResponseDish.getDishData().getDomain();
            String intention = imoranResponseDish.getDishData().getIntention();
            String type = imoranResponseDish.getDishData().getDishContent().getType();
            mPageId = domain + "_" + intention + "_" + type + "_" + "DishActivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
        }

    }

    private void getTTsStep() {
        StringBuilder stringBuilder = new StringBuilder();
        if (mSteps != null && mSteps.size() > 0) {
            for (int i = 0; i < mSteps.size(); i++) {
                stringBuilder.append(mSteps.get(i).getDes());
            }
            //将菜谱步骤丢给tts进行播报
            mStepContent = stringBuilder.toString();
            TTSManager.getInstance(getBaseContext()).speak(mSteps.get(0).getDes(), DishActivity.this, false);
            mPlayState = TTS_PLAY;
            mCount = 1;
            mLastState = Media.States.PLAY;
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
        } else {
            getTTs(mJson);
        }
    }

    //拿到tts
    private void getTTs(String json) {
        if (json != null) {
            JsonData jsonData = JsonUtil.createJsonData(json);
            if (jsonData != null) {
                String tts = jsonData.getTts();
                TTSManager.getInstance(getBaseContext()).speak(tts, true);
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            }
        }
    }

    //未找到资源界面显示
    private void noMessage() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dish_not_find));
        builder.setPositiveButton(getResources().getString(R.string.dish_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    //对话框消失
    private void dialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //处理指令
    private void dealWithCmd() {
        String cmd = mJsonData.getType();
        switch (cmd) {
            case "pause":
            case "end":
                ttsPause();
                break;
            case "continue":
            case "play":
                ttsContinue();
                break;
            case "back":
                finish();
                break;
            default:
                break;
        }
    }

    //暂停播报
    private void ttsPause() {
        mPlayState = TTS_PAUSE;
        mLastState = Media.States.PAUSE;
        TTSManager.getInstance(getBaseContext()).stop();
    }

    //继续播报
    private void ttsContinue() {
        if (mPlayState.equals(TTS_PAUSE)) {
            if (mSteps != null && mSteps.size() > 0) {
                TTSManager.getInstance(getBaseContext()).speak(mSteps.get(0).getDes().substring(mTtsprogress), DishActivity.this, false);
                mPlayState = TTS_PLAY;
                isScroll = true;
                mLastState = Media.States.PLAY;
            }
        }
    }

    //Scrollview移动
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isScroll && mPlayState.equals(TTS_PLAY)) {
                int off = mLinearLayout.getMeasuredHeight() - mScrollView.getHeight();
                mScrolly = stepView.getMeasuredHeight();
                if (msg.what == MSG_ONE) {
                    if (off > 0) {
                        mScrollView.scrollTo(0, mCount * mScrolly);
                        mCount++;
                        if (mSteps.size() > 0) {
                            mSteps.remove(0);
                            if (mSteps.size() > 0) {
                                TTSManager.getInstance(getBaseContext()).speak(mSteps.get(0).getDes(), DishActivity.this, false);
                            }
                        }
                    }
                }
            }
        }
    };

    //获取屏幕可见高度
    private int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics.heightPixels;
    }
    //获取gridview高度
    private int getGridViewHeight(){
        return gridView.getMeasuredHeight();
    }

    @Override
    public void onPlayBegin(String s) {

    }

    @Override
    public void onPlayEnd(String s) {
        Log.i(TAG, "onPlayEnd: 继续发消息");
        isScroll = true;
        Message message = Message.obtain();
        message.what = MSG_ONE;
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    @Override
    public void onPlayStopped(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String var1, int var2) {
        if (!isSessionStart) {
            mTtsprogress = var2;
        }
    }

    @Override
    public void onError(@Nullable String s, int i, String s1) {

    }

    @Override
    public void onSessionStateChanged(SessionState state) {
        super.onSessionStateChanged(state);
        Log.i(TAG, "onSessionStateChanged: state=" + state);
        if (mTtsprogress != 0) {
            switch (state) {
                case START:
                    isSessionStart = true;
                    break;
                case END:
                    isSessionStart = false;
                    if (mPlayState.equals(TTS_PLAY)) {
                        Message message = Message.obtain();
                        message.what = MSG_ONE;
                        if (handler != null) {
                            handler.sendMessage(message);
                        }
                    }
                    break;
            }
        }
    }
}
