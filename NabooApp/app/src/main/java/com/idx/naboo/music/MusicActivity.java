package com.idx.naboo.music;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.android.common.logging.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.music.data.ContentMusic;
import com.idx.naboo.music.data.ImoranResponseMusic;
import com.idx.naboo.music.data.Song;
import com.idx.naboo.music.utils.ButtonClickUtils;
import com.idx.naboo.music.utils.ToastUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.Media;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.entity.info.VUIDataEntity;
import net.imoran.sdk.service.nli.NLIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import static com.idx.naboo.music.utils.MediaPlayerUtils.getMediaPlayer;


public class MusicActivity extends BaseActivity implements View.OnClickListener
        , SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {
    private static final String TAG = MusicActivity.class.getSimpleName();
    private IService mIService;
    private String mJson;
    //解析蓦然json数据
    private ImoranResponseMusic imoranResponseMusic;
    private ContentMusic contentMusic;
    //多媒体对象
    private MediaPlayer mediaPlayer;
    //音乐对象
    private List<Song> mSong = new ArrayList<>();
    //音乐播放列表
    private int mSongIndex;
    //开始时间，音乐时长，音乐名，歌手
    private TextView mStartText, mEndText, mTitle, mArtist;
    private SeekBar mSeekbar;
    //音乐暂停播放按钮，音乐播放模式按钮
    private ImageView mPlayPause, mMode, mNext, mPrevious;
    //背景图片
    private ImageView mBackground;
    //蓦然json数据
    private JsonData jsonData;
    public static final int MSG_SEEKBAR_UPDATE = 0X001;
    public static final int MSG_VIEW_UPDATE = 0X002;
    //音乐总时长
    private String time;
    //音乐模式
    private String dis;
    //音乐当前播放时长
    private String show;
    //线程标志位
    private boolean flag;
    private String mIndexNumber;
    private AlertDialog.Builder builder;
    private MusicAdapter mMusicAdapter;
    private ListView mListView;
    private Thread thread;
    private AlertDialog dialog;
    private boolean isError;
    //播放模式
    public static final String ORDER_PLAY = "order";//顺序播放
    public static final String RANDOM_PLAY = "random";//随机播放
    public static final String CYCLE_PLAY = "cycle";//单曲循环
    private String mode = ORDER_PLAY;//播放模式,默认为顺序播放
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(MusicActivity.this);
            mJson = mIService.getJson();
            if (mJson != null && (!mJson.equals(""))) {
                getDomainType(mJson);
                parseToMusic(mJson);
                parseQueryId(mJson);
                getTTs(mJson);
                if (mSong != null && mSong.size() > 0) {
                    mSongIndex = 0;
                    play(mSongIndex);
                } else {
                    noMessage();
                }
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            } else {
                VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
                dialogDismiss();
                //播放状态,继续播放
                if (mLastState != null && mLastState.equals(Media.States.PLAY)) {
                    continuePlay();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private Media.States mLastState = null;
    private boolean isPlaying = false;
    private String mQueryId;
    private String mPageId;
    private int mErrorCount;

    @Override
    public void onJsonReceived(String json) {
        jsonData = JsonUtil.createJsonData(json);
        mJson = json;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            getDomainType(mJson);
            if (type.equals("song")) {
                getIndexNumber(jsonData);
                getTTs(json);
            } else if (domain.equals("cmd")) {
                dealwithAction();
                getTTs(json);
            }
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //自定义的时间线程
    private class TimeRunnable implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer != null && flag) {
                try {
                    if (mediaPlayer != null && flag) {
                        // 每1秒更新一次时间
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = MSG_SEEKBAR_UPDATE;
                        //发送
                        if (handler != null) {
                            handler.sendMessage(msg);
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    flag = false;
                    e.printStackTrace();
                }
            }
        }
    }

    //提示主线程UI进行时间更新操作
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //通过消息的内容msg.what分别更新ui
            switch (msg.what) {
                case MSG_SEEKBAR_UPDATE:
                    try {
                        int musicTime = 0;
                        if (mediaPlayer != null && mediaPlayer.isPlaying() && flag) {
                            try {
                                musicTime = mediaPlayer.getCurrentPosition() / 1000;
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            DecimalFormat df = new DecimalFormat("00");
                            int minute = musicTime / 60;
                            String minuteStr = df.format(minute);
                            int seconds = musicTime % 60;
                            String secondsStr = df.format(seconds);
                            mSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                            mSeekbar.setMax(mediaPlayer.getDuration());
                            show = minuteStr + ":" + secondsStr;
                            mStartText.setText(show);
                            time = stringForTime();
                            mEndText.setText(time);
                            mPlayPause.setImageResource(R.mipmap.music_play);
                        }
                        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            mPlayPause.setImageResource(R.mipmap.music_pause);
                        }

                        break;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                case MSG_VIEW_UPDATE:
                    updateMusicList();
                    updateImageView();
                    updateView();
                    mListView.smoothScrollToPosition(mSongIndex);
                    mListView.setSelection(mSongIndex);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        permissionRequest();
        flag = true;
        isError=false;
        setListener();
        mediaPlayer = getMediaPlayer(getApplicationContext());
        mediaPlayerListener();
        SharedPreferences pref = getSharedPreferences("play", MODE_PRIVATE);
        mode = pref.getString("play_mode", getPlay_mode());
        if (mode == null) {
            mode = ORDER_PLAY;
        }
        switch (mode) {
            case RANDOM_PLAY:
                mMode.setImageResource(R.mipmap.music_shuffle_play_icon);
                break;
            case ORDER_PLAY:
                mMode.setImageResource(R.mipmap.music_order_play_icon);
                break;
            case CYCLE_PLAY:
                mMode.setImageResource(R.mipmap.music_all_play_icon);
                break;
        }
        //创建线程
        thread = new Thread(new TimeRunnable());
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        mediaPause();
        TTSManager.getInstance(getBaseContext()).stop();
        SharedPreferences.Editor editor = getSharedPreferences("play", MODE_PRIVATE).edit();
        editor.putString("play_mode", getPlay_mode());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (thread != null) {
            thread = null;
        }
    }

    private void setListener() {
        mTitle = findViewById(R.id.title);
        mArtist = findViewById(R.id.artist);
        mStartText = findViewById(R.id.startText);
        mEndText = findViewById(R.id.endText);
        mSeekbar = findViewById(R.id.seekBar1);
        mPlayPause = findViewById(R.id.play_pause);
        mBackground = findViewById(R.id.background_image);
        mNext = findViewById(R.id.next);
        mPrevious = findViewById(R.id.prev);
        mMode = findViewById(R.id.iv_mode);
        mListView = findViewById(R.id.music_list);
        mSeekbar.setOnSeekBarChangeListener(this);
        mPlayPause.setOnClickListener(this);
        mMode.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void updateMusicList() {
        if (mSong != null && mSong.size() > 0) {
            mMusicAdapter = new MusicAdapter(mSong);
            mListView.setAdapter(mMusicAdapter);
            mMusicAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mSongIndex);
            mListView.setSelection(mSongIndex);
        } else {
            noMessage();
        }
    }

    private void getTTs(String json) {
        if (json != null) {
            JsonData jsonData = JsonUtil.createJsonData(json);
            if (jsonData != null) {
                String tts = jsonData.getTts();
                TTSManager.getInstance(getBaseContext()).speak(tts, true);
            }
        }
    }

    //无数据时处理
    private void noMessage() {
        ToastUtil.showToast(this, getResources().getString(R.string.music_not_find));
    }

    //对话框消失
    private void dialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //播放第几首
    private void getIndexNumber(JsonData jsonData) {
        try {
            JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
            String total_count=jsonData.getContent().getString("total_count");
            if (Integer.parseInt(total_count) ==1){
                mIndexNumber = semantic.getString("index_number");
                //拿到位置信息，播放该音乐
                mSongIndex = Integer.parseInt(mIndexNumber) - 1;
                play(mSongIndex);
            }else {
                //对index_number存在数值,但有返回音乐列表进行处理
                dialogDismiss();
                parseToMusic(mJson);
                parseQueryId(mJson);
                if (mSong != null && mSong.size() > 0) {
                    //拿到列表中的第一首，让它的位置为0
                    mSongIndex = 0;
                    //播放该歌曲
                    play(mSongIndex);
                }
            }
        } catch (JSONException e) {
            //对json不存在index_number,但有返回音乐列表进行处理
            dialogDismiss();
            parseToMusic(mJson);
            parseQueryId(mJson);
            if (mSong != null && mSong.size() > 0) {
                //拿到列表中的第一首，让它的位置为0
                mSongIndex = 0;
                //播放该歌曲
                play(mSongIndex);
            }
            e.printStackTrace();
        }
    }

    //mediaPlayer监听事件
    private void mediaPlayerListener() {
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onError: " + what);
                isError=true;
                sendBroadcast(new Intent(Media.ACTION_ERROR));
                if (mErrorCount > 5) {
                    ToastUtil.showToast(MusicActivity.this, "网络异常,请尝试重新更换网络");
                }
                //处理音乐失效问题
                mIService.requestData(getResources().getString(R.string.music_query), new NLIRequest.onRequest() {
                    @Override
                    public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                        parseToMusic(s);
                        parseQueryId(s);
                        if (mSong != null && mSong.size() > 0) {
                            //拿到列表中的第一首，让它的位置为0
                            mSongIndex = 0;
                            //播放该歌曲
                            play(mSongIndex);
                            getTTs(s);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                mErrorCount++;
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!ButtonClickUtils.isFastClick()) {
            switch (view.getId()) {
                case R.id.next:
                    next(true);
                    break;
                case R.id.prev:
                    previous();
                    break;
                case R.id.play_pause:
                    if (mediaPlayer.isPlaying()) {
                        pause();
                    } else {
                        continuePlay();
                    }
                    break;
                case R.id.iv_mode:
                    clickSwitchPlayMode();
                    break;
                default:
                    break;
            }
        }
    }

    //判断播放指令
    private void dealwithAction() {
        String type = jsonData.getType();
        Log.i(TAG, "dealwithAction: " + type);
        switch (type) {
            case "next_song":
                //下一首
                next(true);
                break;
            case "last_song":
                //上一首
                previous();
                break;
            case "pause":
            case "end":
                //暂停
                pause();
                break;
            case "continue":
            case "play":
                //继续播放
                continuePlay();
                break;
            case "next":
                //换一组
                mIService.requestData(getResources().getString(R.string.music_query), new NLIRequest.onRequest() {
                    @Override
                    public void onResponse(@Nullable BaseContentEntity baseContentEntity, String s) {
                        parseToMusic(s);
                        parseQueryId(s);
                        if (mSong != null && mSong.size() > 0) {
                            //拿到列表中的第一首，让它的位置为0
                            mSongIndex = 0;
                            //播放该歌曲
                            play(mSongIndex);
                            getTTs(s);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                break;
            case "music_mode":
                // 切换播放模式
                switchPlayMode();
                break;
            case "skipto_start":
                //从头播放
                playToIndex();
                break;
            case "back":
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        play(i);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSeekbar.setSecondaryProgress(seekBar.getProgress());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int musicTime = i / 1000;
        DecimalFormat df = new DecimalFormat("00");
        int minute = musicTime / 60;
        String minuteStr = df.format(minute);
        int seconds = musicTime % 60;
        String secondsStr1 = df.format(seconds);
        String i1 = minuteStr + ":" + secondsStr1;
        mStartText.setText(i1);
    }

    //解析json数据
    private void parseToMusic(String json) {
        imoranResponseMusic = ImoranResponseToBeanUtils.handleMusicData(json);
        if (ImoranResponseToBeanUtils.isImoranMusicNull(imoranResponseMusic)) {
            contentMusic = imoranResponseMusic.getMusicData().getMusicContent();

            if (contentMusic.getMusicReply().getSongs() != null && contentMusic.getMusicReply().getSongs().size() > 0) {
                //拿到音乐列表，存入List中
                mSong = contentMusic.getMusicReply().getSongs();
                for (int i = 0; i < mSong.size(); i++) {
                    Log.i(TAG, " mSong=" + mSong.get(i).getName());
                }
            }
        }
    }

    //
    private void parseQueryId(String json) {
        imoranResponseMusic = ImoranResponseToBeanUtils.handleMusicData(json);
        if (ImoranResponseToBeanUtils.isImoranMusicNull(imoranResponseMusic)) {
            //加入场景
            mQueryId = imoranResponseMusic.getMusicData().getQueryid();
            String domain = imoranResponseMusic.getMusicData().getDomain();
            String intention = imoranResponseMusic.getMusicData().getIntention();
            String type = contentMusic.getType();
            mPageId = domain + "_" + intention + "_" + type + "_" + "MusicActivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
        }

    }

    //对domain是音乐,但是返回是movie的情况进行规避
    private void getDomainType(String mJson) {
        try {
            JSONObject jsonObject = new JSONObject(mJson);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (domain.equals("music") && type.equals("movie")) {
                TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.music_not_support), true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //界面显示
    private void updateView() {
        try {
            if (mSong != null && mSong.size() > 0) {
                mMusicAdapter.setClick(true);
                mMusicAdapter.setCurrentItem(mSongIndex);
                mMusicAdapter.notifyDataSetChanged();
                mTitle.setText(mSong.get(mSongIndex).getName());
                mTitle.setSelected(true);
                mTitle.setFocusable(true);
                mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTitle.setMarqueeRepeatLimit(-1);
                if (mSong.get(mSongIndex).getSinger() != null && mSong.get(mSongIndex).getSinger().length > 0) {
                    mArtist.setText(mSong.get(mSongIndex).getSinger()[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新专辑图片
    private void updateImageView() {
        if (mSong != null && mSong.size() > 0) {
            String url = mSong.get(mSongIndex).getPicUrl();
            Glide.with(this).load(url)
                    .placeholder(R.drawable.music_default)
                    .error(R.drawable.amap_bus)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(347, 347)
                    .into(mBackground);
        }
    }

    //暂停后继续播放
    public void continuePlay() {
        mediaPlay();
        mLastState = Media.States.PLAY;
        isPlaying = true;
        sendBroadcast(new Intent(Media.ACTION_PLAY));

    }

    //暂停播放
    public void pause() {
        mLastState = Media.States.PAUSE;
        isPlaying = false;
        sendBroadcast(new Intent(Media.ACTION_PAUSE));
        mediaPause();
    }

    //播放
    private void mediaPlay() {
        if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
            mediaPlayer.start();
        }
    }

    //暂停
    private void mediaPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    //从头开始播放
    public void playToIndex() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlay();
            mLastState = Media.States.PLAY;
            isPlaying = true;
            sendBroadcast(new Intent(Media.ACTION_PLAY));
        }
    }

    //按音乐列表进行播放
    public void play(int position) {
        if (mSong.size() < 0) {
            return;
        }
        if (position < 0) {
            position = mSong.size() - 1;
        } else if (position >= mSong.size()) {
            position = 0;
        }
        mSongIndex = position;
        String song = mSong.get(mSongIndex).getSongUrl();
        if (!song.equals("")) {
            play(song);
        }
    }

    public void play(String url) {
        flag = true;
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            mLastState = Media.States.PLAY;
            isPlaying = true;
            sendBroadcast(new Intent(Media.ACTION_PLAY));
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            Message msg = new Message();
            msg.what = MSG_VIEW_UPDATE;
            if (handler != null) {
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //开始播放
            mp.start();
            mLastState = Media.States.PLAY;
            isPlaying = true;
            sendBroadcast(new Intent(Media.ACTION_PLAY));
        }
    };
    //歌曲完成后，播放下一首
    public MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "onCompletion: 下一首");
            next(false);
        }
    };

    //语音控制播放模式
    private void switchPlayMode() {
        if (mLastState != null) {
            jsonData = JsonUtil.createJsonData(mJson);
            mode = dealWithJson(jsonData);
            if (mode != null) {
                switch (mode) {
                    case "random":
                        setPlay_mode(RANDOM_PLAY);
                        mMode.setImageResource(R.mipmap.music_shuffle_play_icon);
                        ToastUtil.showToast(this, getResources().getString(R.string.music_random));
                        break;
                    case "order":
                        setPlay_mode(ORDER_PLAY);
                        mMode.setImageResource(R.mipmap.music_order_play_icon);
                        ToastUtil.showToast(this, getResources().getString(R.string.music_order));
                        break;
                    case "cycle":
                    case "single":
                        setPlay_mode(CYCLE_PLAY);
                        mMode.setImageResource(R.mipmap.music_all_play_icon);
                        ToastUtil.showToast(this, getResources().getString(R.string.music_cycle));
                        break;
                }
                //通知切换模式成功
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));

                switch (mLastState) {
                    case STOP:
                        sendBroadcast(new Intent(Media.ACTION_STOP));
                        break;
                    case ERROR:
                        sendBroadcast(new Intent(Media.ACTION_ERROR));
                        break;
                    case PAUSE:
                        sendBroadcast(new Intent(Media.ACTION_PAUSE));
                        break;
                    case PLAY:
                        sendBroadcast(new Intent(Media.ACTION_PLAY));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //点击控制播放模式
    private void clickSwitchPlayMode() {
        if (mLastState != null) {
            mode = getPlay_mode();
            switch (mode) {
                case ORDER_PLAY:
                    mMode.setImageResource(R.mipmap.music_shuffle_play_icon);
                    setPlay_mode(RANDOM_PLAY);
                    ToastUtil.showToast(this, getResources().getString(R.string.music_random));
                    break;
                case RANDOM_PLAY:
                    mMode.setImageResource(R.mipmap.music_all_play_icon);
                    setPlay_mode(CYCLE_PLAY);
                    ToastUtil.showToast(this, getResources().getString(R.string.music_cycle));
                    break;
                case CYCLE_PLAY:
                    mMode.setImageResource(R.mipmap.music_order_play_icon);
                    setPlay_mode(ORDER_PLAY);
                    ToastUtil.showToast(this, getResources().getString(R.string.music_order));
                    break;
            }
            switch (mLastState) {
                case STOP:
                    sendBroadcast(new Intent(Media.ACTION_STOP));
                    break;
                case ERROR:
                    sendBroadcast(new Intent(Media.ACTION_ERROR));
                    break;
                case PAUSE:
                    sendBroadcast(new Intent(Media.ACTION_PAUSE));
                    break;
                case PLAY:
                    sendBroadcast(new Intent(Media.ACTION_PLAY));
                    break;
                default:
                    break;
            }
        }
    }

    //拿到音乐播放模式
    private String dealWithJson(JsonData jsonData) {
        try {
            synchronized (this) {
                JSONObject replay = jsonData.getContent().getJSONObject("reply");
                if (replay != null) {
                    JSONArray musicMode = replay.getJSONArray("music_mode");
                    JSONObject mode = musicMode.getJSONObject(0);
                    dis = mode.getString("mode");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dis;
    }

    //下一首
    public void next(boolean isClick) {
        if (mSong.size() <= 0) {
            return;
        }
        android.util.Log.i(TAG, "next: mSongIndex=" + mSongIndex);
        if (mode != null) {
            switch (mode) {
                case "random":
                    //随机播放
                    setPlay_mode(RANDOM_PLAY);
                    mSongIndex = new Random().nextInt(mSong.size());
                    play(mSongIndex);
                    break;
                case "order":
                    //顺序播放
                    setPlay_mode(ORDER_PLAY);
                    play(mSongIndex + 1);
                    break;
                case "cycle":
                    //循环播放
                    setPlay_mode(CYCLE_PLAY);
                    if (isClick) {
                        play(mSongIndex + 1);
                    } else {
                        play(mSongIndex);
                    }
                    break;
            }
        }
    }

    //上一首
    public void previous() {
        if (mSong.size() <= 0) {
            return;
        }
        if (mode != null) {
            switch (mode) {
                case "random":
                    //随机播放
                    setPlay_mode(RANDOM_PLAY);
                    mSongIndex = new Random().nextInt(mSong.size());
                    play(mSongIndex);
                    break;
                case "order":
                    //顺序播放
                    setPlay_mode(ORDER_PLAY);
                    play(mSongIndex - 1);
                    break;
                case "cycle":
                    //循环播放
                    setPlay_mode(CYCLE_PLAY);
                    play(mSongIndex - 1);
                    break;
            }
        }
    }

    //将int类型的时间转化成mm:ss格式
    public String stringForTime() {
        int time_all = mediaPlayer.getDuration();
        int musicTime = time_all / 1000;
        DecimalFormat df = new DecimalFormat("00");
        int minute = musicTime / 60;
        String minuteStr = df.format(minute);
        int seconds = musicTime % 60;
        String secondsStr1 = df.format(seconds);
        time = minuteStr + ":" + secondsStr1;
        return time;
    }

    //set方法
    private void setPlay_mode(String play_mode) {
        this.mode = play_mode;
    }

    //get方法
    private String getPlay_mode() {
        return mode;
    }

    @Override
    public void onSessionStateChanged(SessionState state) {
        super.onSessionStateChanged(state);
        Log.d(TAG, "onSessionStateChanged: " + state);
        if (mediaPlayer != null) {
            switch (state) {
                case START:
                    if (mLastState != null && mLastState.equals(Media.States.PLAY)) {
                        Log.d(TAG, "onSessionStateChanged: pause");
                        isPlaying = false;
                        mediaPlayer.pause();
                    }
                    break;
                case END:
                    if (mLastState != null && mLastState.equals(Media.States.PLAY) && !isPlaying) {
                        Log.d(TAG, "onSessionStateChanged: start");
                        isPlaying = true;
                        mediaPlayer.start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //申请权限
    private void permissionRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
