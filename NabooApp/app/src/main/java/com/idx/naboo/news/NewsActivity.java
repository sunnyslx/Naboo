package com.idx.naboo.news;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.map.mapUtils.ToastUtil;
import com.idx.naboo.news.data.NewsDetail;
import com.idx.naboo.news.util.MyTextView;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.utils.HtmlUtils;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.utils.NetStatusUtils;

import net.imoran.sdk.entity.info.VUIDataEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsActivity extends BaseActivity implements TTSManager.Callback{

    private static final String TAG = "NewsActivity";
    private IService mIService;
    private int newsIndex;
    private int number;
    private String newsJson;
    private List<NewsDetail> mList=new ArrayList<>();
    private List<NewsDetail> srcList = null;
    private List<NewsDetail> list = new ArrayList<>();
    private String[] list1 = null;
    private RelativeLayout mRelativeLayout;
    private TextView title;
    private MyTextView content;
    private TextView nextNews;
    private TextView previousNews;
    private TextView source;
    private TextView date;
    private TextView toolBar;
    private TextView error;
    private RelativeLayout mLayout;
    private View line;
    //进度值
    private int progress;
    //暂存进度
    private int stagingProgress;
    private int progressIf;

    private int cun;
    private int i;
    //每一行播报平均时间长
    private int averageTime;
    /*
     * 设置播报状态值:
     * Play： 0
     * Stop： 1
     * Pause： 2
     * NoProgress: 3
     * End: 5
     * NnSupportCommand： 6
     */
    private int playState;
    //新闻标签
    private Button label;
    private NestedScrollView mScrollView;
    //播放的语句
    private String newsContent;

    //控制滚动进程
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    handler.removeCallbacks(NestedScrollView);
                    break;
                default:
                    break;
            }

        }
    };

    //获取手指在屏幕上下滑动
    private float y1;
    private float y2;

    //计算自动滑动的高度
    private int sun;
    private String mQueryId;
    private String mPageId;
    //临时json
    private String temporaryJson;
    //每一行的高度
    private int autoScrollY;
    //只计算一次新闻的相关参数
    private int once;
    private int off;
    private int delayTime;
    private double averageLineSize;
    private int length;

    //拿到service的连接
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(NewsActivity.this);
            //json为空时（一般返回时进入）
            if ((mIService.getJson()).equals("") || mIService.getJson() == null){
                Log.d(TAG, "onServiceConnected: temp = "+temporaryJson);
                if (temporaryJson != null) {
                    scenario(temporaryJson);
                    //当进度值大于追加长度时，恢复播报进度 = 当前进度长度 - 追加长度；
                    //当进度值小于追加长度时，从头开始播报；
                    if ((stagingProgress + progress - 8) < newsContent.length() && error.getVisibility() == View.GONE && playState == 0) {
                        if ((stagingProgress + progress) > 8) {
                            cun = stagingProgress + progress - 8;
                        } else {
                            cun = 0;
                        }
                        playState = 0;
                        handler.postDelayed(NestedScrollView, 1000);
                        TTSManager.getInstance(getBaseContext()).speak(newsContent.substring(cun), NewsActivity.this, false);
                    } else {
                        //回滚到最开始
                        mScrollView.scrollTo(0, 0);
                    }
                }else {
                    finish();
                }
            } else{
                //拿json文件
                newsJson = mIService.getJson();
                //解析json
                parseToNews(newsJson);
                //判断解析后的结果，有数据插入数据；无数据显示无相关新闻
                if (list != null && list.toString().length() > 2 && mList.size() > 0) {
                    //清空内容
                    clearTextViewContent();
                    //插入数据
                    initData();
                } else {
                    playState = 3;
                    toolBar.setVisibility(View.INVISIBLE);
                    line.setVisibility(View.INVISIBLE);
                    mScrollView.setVisibility(View.INVISIBLE);
                    mRelativeLayout.setVisibility(View.INVISIBLE);
                    error.setVisibility(View.VISIBLE);
                    String tts = getString(R.string.no_have_news);
                    TTSManager.getInstance(getBaseContext()).speak(tts, NewsActivity.this, true);
                    //通知service成功拿到json
                    Intent intent = new Intent(Execute.ACTION_SUCCESS);
                    sendBroadcast(intent);
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.e(TAG, "onBindingDied: 有异常");
        }
    };

    @Override
    public void onJsonReceived(String json) {
        //拿到json文件
        newsJson = json;
        String cmd = ImoranResponseToBeanUtils.handleDomain(newsJson);
        //拿到的是指令，执行指令
        if (cmd.equals("cmd")){
            chargeCmd(newsJson);
        }
        //拿到的是新闻内容，刷新ListView内容，并回滚到顶部；内容为空显示暂无相关新闻
        else {
            list = ImoranResponseToBeanUtils.handleNewsData(newsJson);
            if (list != null && list.size() > 0) {
                if (list1 != null) {
                    label.setText("");
                    list1 = null;
                    label.setVisibility(View.INVISIBLE);
                }
                mScrollView.setVisibility(View.VISIBLE);
                mRelativeLayout.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                parseToNews(newsJson);
                clearTextViewContent();
                initData();
            } else {
                playState = 3;
                handler.removeCallbacksAndMessages(null);
                toolBar.setVisibility(View.INVISIBLE);
                label.setVisibility(View.INVISIBLE);
                line.setVisibility(View.INVISIBLE);
                mScrollView.setVisibility(View.INVISIBLE);
                mRelativeLayout.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
                String tts = getString(R.string.no_have_news);
                TTSManager.getInstance(getBaseContext()).speak(tts, this,true);
                //成功拿到json
                Intent intent = new Intent(Execute.ACTION_SUCCESS);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //绑定组件
        initView();
    }


    /**
     * 新闻类的控件的绑定
     */
    private void initView(){
        title = findViewById(R.id.news_new_title);
        content = findViewById(R.id.news_new_content);
        previousNews = findViewById(R.id.previous);
        nextNews = findViewById(R.id.next);
        source = findViewById(R.id.source);
        date = findViewById(R.id.publish_date);
        toolBar= findViewById(R.id.toolbar);
        line = findViewById(R.id.toolbar_line);
        label = findViewById(R.id.news_label);
        error = findViewById(R.id.news_error);
        mScrollView = findViewById(R.id.nested_scrollview);
        mLayout = findViewById(R.id.news_relative);
        mRelativeLayout = findViewById(R.id.navigation_bar_view);
    }

    /**
     * 将数据放入指定的控件中
     */
    private void initData(){
        //处理新闻内容
        newsContent = HtmlUtils.filterHtml(mList.get(0).getContent().replace("\u3000", "")).replace("&nbsp;", "");
        newsContent = (newsContent.replace(" ", "")).replace("\n","");
        //新闻标题
        title.setText(mList.get(0).getTitle());
        //新闻内容
        content.setText("\u3000\u3000"+newsContent);
        //新闻来源
        source.setText(getString(R.string.source)+mList.get(0).getSource());
        //新闻日期
        date.setText(mList.get(0).getPublishDate());
        toolBar.setText(mList.get(0).getTitle());
        mScrollView.scrollTo(0, 0);
        //移除上一个Handler
        handler.removeCallbacksAndMessages(null);
        //初始化参数
        sun = 0;
        i = 0;
        cun = 0;
        stagingProgress = 0;
        playState = 0;
        progressIf = 0;
        once = 0;
        //Handler延迟
        handler.postDelayed(NestedScrollView,3000);
        /*
         * 监听手指的Touch事件
         */
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    y1 = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    y2 = event.getY();
                    if (playState == 0) {
                        if ((y2 - y1) > 5 || (y1 - y2) > 5) {
                            i++;
                            playState = 1;
                            TTSManager.getInstance(getBaseContext()).stop();
                            handler.removeCallbacksAndMessages(null);
                            if (i == 1) {
                                ToastUtil.showToast(NewsActivity.this, getString(R.string.auto_reader_stop));
                            }
                        }
                    }
                }
                return false;
            }
        });
        //是否存在标签
        if (list1 != null){
            label.setText(list1[0]);
            label.setVisibility(View.VISIBLE);
        }
        //控制上一条的显示，及点击事件
        if (newsIndex == 0){
            previousNews.setVisibility(View.INVISIBLE);
        }else if(newsIndex > 0){
            previousNews.setVisibility(View.VISIBLE);
            previousNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    previous();
                }
            });

        }
        //控制下一条的显示，及点击事件
        if (newsIndex == (srcList.size()-1)){
            nextNews.setVisibility(View.INVISIBLE);
        }else if (newsIndex >= 0 && newsIndex < srcList.size()-1){
            nextNews.setVisibility(View.VISIBLE);
            nextNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next();
                }
            });
        }
        /*
         * scrollview 滚动的监听事件
         * 判断滚动高度，决定自定义title bar是否显示
         * 控制标签的显示
         */
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >60){
                    toolBar.setBackgroundColor(getResources().getColor(R.color.news_background));
                    toolBar.getBackground().setAlpha(0);
                    toolBar.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    toolBar.setText(title.getText());
                    label.setVisibility(View.INVISIBLE);
                }else if (scrollY < 60){
                    toolBar.setVisibility(View.INVISIBLE);
                    line.setVisibility(View.INVISIBLE);
                }
                if (scrollY == 0 && list1 != null){
                    label.setVisibility(View.VISIBLE);
                }
            }
        });
        try {
            //停止上一个新闻语音的播报
            TTSManager.getInstance(getBaseContext()).stop();
            //初始化语音进度及状态
            progress = 0;
            playState = 0;
            //播报新闻内容
            TTSManager.getInstance(getBaseContext()).speak(getString(R.string.news_details)+newsContent, this, false);
            //成功拿到json
            Intent intent = new Intent(Execute.ACTION_SUCCESS);
            sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(NewsActivity.this, getString(R.string.news_broadcast_error), Toast.LENGTH_SHORT).show();
            handler.removeCallbacksAndMessages(null);
        }
    }
    /**
     * 1.拿到当前的内容；
     * 2.当新闻内容list大于1时存入srcList变量中
     * 3.当新闻内容list等于1时，判断有无NewIndex：
     * 有：根据拿到的NewIndex的值添加到mList中
     * 无：直接添加到mList中
     */
    private void parseToNews(String json) {
        list = ImoranResponseToBeanUtils.handleNewsData(json);
        if (ImoranResponseToBeanUtils.handleLabel(json) != null ){
            list1 = ImoranResponseToBeanUtils.handleLabel(json);
        }
        if (list != null && list.size() > 1){
            temporaryJson = json;
            scenario(temporaryJson);
            //清空srcList操作
            if (srcList!=null){
                srcList.clear();
                mList.clear();
            }
            srcList = list;
            newsIndex = 0;
            mList.add(srcList.get(0));

        } else if(list != null && list.size() == 1){
            //找到其在srcList中的位置
            number = ImoranResponseToBeanUtils.handleNewsIndex(json);
            if (number == 0) {
                srcList = list;
                newsIndex = number;
                mList.add(list.get(0));
                temporaryJson = json;
                scenario(temporaryJson);
            }else {
                newsIndex = number - 1;
                if (srcList != null && srcList.size() >= number) {
                    //清空mList操作
                    if (mList != null) {
                        mList.clear();
                    }
                    mList.add(srcList.get(newsIndex));
                }
            }

        }
    }

    /**
     * 获取指令，执行相应的方法
     */
    private void chargeCmd(String json) {
        try {
            //获取指令
            String type = ImoranResponseToBeanUtils.handleNewsType(json);
            //成功拿到json
            Intent intent = new Intent(Execute.ACTION_SUCCESS);
            sendBroadcast(intent);
            switch (type) {
                case "next":
                    //下一条
                    next();
                    break;
                case "previous":
                    //上一条
                    previous();
                    break;
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
                    //无对应指令操作
                    unDo();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unDo() {
        playState = 6;
        TTSManager.getInstance(getBaseContext()).speak(getString(R.string.no_support_cmd), this,true);
    }

    private void back() {
        finish();
    }

    //提示已暂停
    private void pause() {
        playState = 2;
        Toast.makeText(NewsActivity.this, getString(R.string.news_pause), Toast.LENGTH_SHORT).show();
        handler.removeCallbacksAndMessages(null);
        TTSManager.getInstance(getBaseContext()).speak(getString(R.string.news_pause), this, true);
    }

    //继续播报新闻
    private void resume() {
        //停止状态的处理
        if (playState == 1){
            TTSManager.getInstance(getBaseContext()).speak(getString(R.string.auto_reader_stop), this, true);
        }
        //语音暂停状态的处理
        if (playState == 0 || playState == 2 || playState == 6){
            if(cun != (stagingProgress+progress) && (stagingProgress+progress) < newsContent.length()) {
                playState = 0;
                if ((stagingProgress+progress) > 8) {
                    cun = stagingProgress+progress-8;
                }else {
                    cun = 0;
                }
                mScrollView.scrollTo(0,sun);
                handler.post(NestedScrollView);
                TTSManager.getInstance(getBaseContext()).speak(newsContent.substring(cun), this,false);
            }
        }
    }

    //上一条新闻
    private void previous() {
        newsIndex--;
        if (newsIndex >= 0) {
            if (mList != null){
                mList.clear();
            }
            mList.add(srcList.get(newsIndex));
            initData();
        }else {
            newsIndex++;
            Toast.makeText(NewsActivity.this, getString(R.string.no_previous_news),Toast.LENGTH_SHORT).show();
            TTSManager.getInstance(this).speak(getString(R.string.no_previous_news),this, true);
        }
    }

    //下一条新闻
    private void next() {
        newsIndex++;
        if(newsIndex >=0 && newsIndex < srcList.size()) {
            if (mList != null) {
                mList.clear();
            }
            mList.add(srcList.get(newsIndex));
            initData();
        }else {
            newsIndex--;
            Toast.makeText(NewsActivity.this, getString(R.string.no_next_news),Toast.LENGTH_SHORT).show();
            TTSManager.getInstance(this).speak(getString(R.string.no_next_news),this, true);
        }
    }

    /**
     * 清空TextView中的内容
     */
    private void clearTextViewContent(){
        title.setText("");
        content.setText("");
        source.setText("");
        date.setText("");
        toolBar.setText("");
        label.setText("");
    }

    private void scenario(String json){
        JsonData jsonData = JsonUtil.createJsonData(json);
        mQueryId = jsonData.getQueryId();
        String domain = jsonData.getDomain();
        String intention = jsonData.getIntention();
        String type = jsonData.getType();
        mPageId = domain +"_"+ intention +"_"+ type +"_"+"NewsActivity";
        VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
    }
    //实现scrollview的自动滚动
    private Runnable NestedScrollView = new Runnable() {

        @Override
        public void run() {
            if (once == 0) {
                off = mLayout.getMeasuredHeight() - mScrollView.getHeight();
                autoScrollY = 0;
                autoScrollY = content.getMeasuredHeight() / content.getLineCount();
                //计算语音播报的实际长度length
                Pattern p = Pattern.compile("%", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(newsContent);
                int c = 0;
                while (m.find()) {
                    c++;
                }
                length = c * 2 + content.length();
                averageLineSize = new BigDecimal((float) length / content.getLineCount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                averageTime = (int) (averageLineSize * 200);
                delayTime = averageTime / autoScrollY;
                once++;
            }
            if (off > 0) {
                if (off <= 50){
                    sun = off;
                }else {
                    mScrollView.scrollBy(0, 1);
                    sun = sun + 1;//实际滚动到的位置
                }
                if (progressIf == 1){
                    int y = (int) ((stagingProgress+progress)/averageLineSize * autoScrollY);//应该滚动到的位置
                    if ((sun - autoScrollY/2) > y){
                        delayTime = delayTime + 5;
                    }else if ((y - autoScrollY/2) > sun){
                        delayTime = delayTime - 5;
                    }else {
                        if (delayTime-5 >(averageTime/autoScrollY)){
                            delayTime = delayTime - 5;
                        }else if (delayTime < (averageTime/autoScrollY)-5){
                            delayTime = delayTime + 5;
                        }else {
                            delayTime = averageTime / autoScrollY;
                        }
                    }
                    progressIf = 0;
                }
                if (sun >= off) {
                    Log.d(TAG, "run: sun = "+sun);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } else {
                    handler.postDelayed(this, delayTime);
                }
            }
            //播报出错
            if (playState == -1){
                TTSManager.getInstance(getBaseContext()).stop();
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(NewsActivity.this, getString(R.string.broadcast_failure), Toast.LENGTH_LONG).show();
            }
            if (playState == 5){
                TTSManager.getInstance(getBaseContext()).stop();
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(NewsActivity.this, getString(R.string.news_broadcast_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        TTSManager.getInstance(getBaseContext()).stop();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 监听语音播报的进度
     * @param s
     * @param i 当前进度值
     * progress 总的进度
     */
    @Override
    public void onSpeechProgressChanged(String s, int i) {
        if (playState == 0){
            if (progress>i){
                stagingProgress = stagingProgress + progress;
            }
            progress = i;
            if ((stagingProgress+progress)%80 == 0){
                if (cun < stagingProgress + progress ) {
                    cun = stagingProgress+progress;
                    progressIf = 1;
                }
            }
        }
    }

    @Override
    public void onPlayBegin(String s) {
        Log.e(TAG, "onPlayBegin: 播报开始 playState"+playState);
    }

    @Override
    public void onPlayEnd(String s) {
        Log.e(TAG, "onPlayEnd: ");
        if (playState == 0){
            playState = 5;
        }
    }

    @Override
    public void onPlayStopped(String s) {

    }

    @Override
    public void onError(@Nullable String s, int i, String s1) {
        Log.e(TAG, "onError: 播报错误！");
        playState = -1;
    }

    /**
     * 监听state的状态
     * playState为状态值
     * @param state
     */
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
                    if ((playState == 0 || playState == 6) && (stagingProgress+progress) < newsContent.length()) {
                        handler.post(NestedScrollView);
                        playState = 0;
                        TTSManager.getInstance(getBaseContext()).speak(newsContent.substring(stagingProgress+progress), NewsActivity.this,false);
                    }
                    break;
            }
        }
    }
}


//测试网络状态
//NetStatusUtils.isNetWorkAvailableOfGet("https://www.baidu.com/", new Comparable<Boolean>() {
//@Override
//public int compareTo(@NonNull Boolean available) {
//        int i = 0;
//        if (available) {
//        android.util.Log.d(TAG, "onReceive:有网");
//        i = 0;
//        return 1;
//        } else {
//        android.util.Log.d(TAG, "onReceive:无网");
//        if (i > 5) {
//        playState = -1;
//        }
//        i++;
//        return 0;
//        }
//        }
//        });