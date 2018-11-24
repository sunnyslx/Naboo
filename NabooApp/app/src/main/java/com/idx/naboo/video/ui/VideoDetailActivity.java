package com.idx.naboo.video.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.music.MusicActivity;
import com.idx.naboo.music.utils.MediaPlayerUtils;
import com.idx.naboo.music.utils.ToastUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.Media;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.video.data.ImoranResponseVideo;
import com.idx.naboo.video.data.Movie;
import com.idx.naboo.videocall.utils.SpUtils;
import com.idx.naboo.weather.WeatherActivity;

import net.imoran.sdk.entity.info.VUIDataEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;


public class VideoDetailActivity extends BaseActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, SurfaceHolder.Callback {
    private static final String TAG = VideoDetailActivity.class.getSimpleName();
    private static final int MSG_ONE = 1;
    private IService mIService;
    private ImageView video_back;
    private TextView video_title;
    private TextView mEnd, mStart;
    private SeekBar mSeekBar;
    private ImageView video_fast, video_down;
    private ProgressBar mProgressBar;
    private ImageView videoPlayPause;
    private LinearLayout mLinearTitle, mLinearSeekBar, mLinearImage;
    private int mIndex;
    private ImoranResponseVideo imoranResponseVideo;
    private List<Movie> movieList;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    //视图
    private SurfaceView mSurfaceView;
    //surface是否已经创建好
    private boolean isSurfaceCreated;
    private boolean flag;
    private int position;
    private JsonData jsonData;
    //当前播放时长
    private String show;
    //视频总时长
    private String time;
    private Thread thread;
    private String mJson;
    private String mFastTime,mJumpTime;
    private String mBackTime;
    private String new_url = "http://imoran.net/demo/video/sharp/Delta.mp4";
    private String new_url1 = "http://imoran.net/demo/video/sharp/gee.mp4";
    private String new_url2 = "http://imoran.net/demo/video/sharp/Girls.mp4";
    private String new_url3 = "http://imoran.net/demo/video/sharp/sun.mp4";
    private String new_url4="http://maoyan.meituan.net/movie/videos/854x480d365cf4cfec9488198fb075c983209bd.mp4";
    private String[] url = {new_url, new_url1, new_url2, new_url3};
    private Media.States mLastState = null;
    private boolean isPlaying = false;
    private String mQueryId;
    private String mPageId;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(VideoDetailActivity.this);
            mJson=mIService.getJson();
            if (mJson !=null && (!mJson.equals(""))){
                Log.i(TAG, "onServiceConnected: json 不为空");
                getType(mJson);
                pauseJson(mJson);
                dialogDismiss();
                updateView();
                if (movieList !=null && movieList.size()>0){
                    initPlayer();
                    playDelay();
                    TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.video_play_start)+movieList.get(0).getTitle(),true);
                }
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            }else {
                Log.i(TAG, "onServiceConnected: json为空 ");
                pauseJson(mJson);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //设置播放时打开屏幕
                mSurfaceView.getHolder().setKeepScreenOn(true);
                isSurfaceCreated = true;
                //创建surfaceView
                createSurface();
                flag = true;
                //解决视频由不可见变为可见时，黑屏问题
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isSurfaceCreated && flag) {
                            surfaceHolder = mSurfaceView.getHolder();
                            mediaPlayer.setDisplay(surfaceHolder);
                            if (mLastState !=null && mLastState.equals(Media.States.PLAY)){
                                onContinue();
                            }
                        }
                    }
                }, 20);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        try {
            JSONObject jsonObject=new JSONObject(json);
            String type=jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            String domain=jsonObject.getJSONObject("data").getString("domain");
            if (type.equals("movie")){
                mJson=json;
                getType(json);
                //将之前的mediaPlayer置空
                if (mediaPlayer!=null){
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                mediaPlayer=MediaPlayerUtils.getMediaPlayer(getApplicationContext());
                pauseJson(json);
                if (movieList !=null && movieList.size()>0){
                    dialogDismiss();
                    updateView();
                    playDelay();
                    TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.video_play_start)+movieList.get(0).getTitle(),true);
                    sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
                }else {
                    Toast.makeText(this,getResources().getString(R.string.video_not_find),Toast.LENGTH_SHORT).show();
                }
            }else if (domain.equals("cmd")){
                dealwithAction(json);
                getTTs(json);
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //自定义的时间线程
    private class TimeRunnable implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer != null && flag) {
                try {
                    try {
                        if (mediaPlayer != null && flag) {
                            // 每1秒更新一次时间
                            Thread.sleep(1000);
                            Message msg = new Message();
                            msg.what = MSG_ONE;
                            //发送
                            if (handler != null) {
                                handler.sendMessage(msg);
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
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
                case MSG_ONE:
                    try {
                        int musicTime = 0;
                        //显示在textview上
                        if (mediaPlayer != null && flag && mediaPlayer.isPlaying()) {
                            if (mediaPlayer.getCurrentPosition() == 0) {
                                mProgressBar.setVisibility(View.VISIBLE);
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                            }
                            musicTime = mediaPlayer.getCurrentPosition() / 1000;
                            DecimalFormat df = new DecimalFormat("00");
                            int minute = musicTime / 60;
                            String minuteStr = df.format(minute);
                            int seconds = musicTime % 60;
                            String secondsStr = df.format(seconds);
                            show = minuteStr + ":" + secondsStr;
                            mEnd.setText(formatTime());
                            mStart.setText(show);
                            mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                            mSeekBar.setMax(mediaPlayer.getDuration());
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mLinearImage.setVisibility(View.GONE);
//                                    mLinearSeekBar.setVisibility(View.GONE);
//                                    mLinearTitle.setVisibility(View.GONE);
//                                }
//                            },10000);
                        }
//                        else {
//                            mLinearImage.setVisibility(View.VISIBLE);
//                            mLinearSeekBar.setVisibility(View.VISIBLE);
//                            mLinearTitle.setVisibility(View.VISIBLE);
//                        }
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            videoPlayPause.setImageResource(R.mipmap.video_play);
                        } else {
                            videoPlayPause.setImageResource(R.mipmap.video_pause);
                        }
                        break;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mediaPlayer = MediaPlayerUtils.getMediaPlayer(getApplicationContext());
        setListener();
        flag = true;
        thread = new Thread(new TimeRunnable());
        thread.start();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.i(TAG, "onCreate: 拿到position");
                mIndex = bundle.getInt("video_index");
                movieList = (List<Movie>) bundle.getSerializable("video");
                updateView();
                initPlayer();
                playDelay();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        surfaceHolder.setFixedSize(i1, i2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isSurfaceCreated = false;
        try {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        position = mediaPlayer.getCurrentPosition();
                        mediaPlayer.stop();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPause();
        TTSManager.getInstance(getBaseContext()).stop();
        unbindService(connection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        isSurfaceCreated = false;
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

    private void pauseJson(String mJson) {
        imoranResponseVideo = ImoranResponseToBeanUtils.handleVedioData(mJson);
        if (ImoranResponseToBeanUtils.isImoranVideNull(imoranResponseVideo)) {
            //添加场景
            mQueryId=imoranResponseVideo.getVideoData().getQueryid();
            String domain=imoranResponseVideo.getVideoData().getDomain();
            String intention=imoranResponseVideo.getVideoData().getIntention();
            String type=imoranResponseVideo.getVideoData().getContent().getType();
            mPageId=domain+"_"+intention+"_"+type+"_"+"VideoDetailActivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
            if (imoranResponseVideo.getVideoData().getContent().getType().equals("movie")) {
                movieList = imoranResponseVideo.getVideoData().getContent().getVideoReply().getMoviesList();
            }
        }
    }

    private void setListener() {
        video_back = findViewById(R.id.video_back);
        video_title = findViewById(R.id.video_title);
        mSurfaceView = findViewById(R.id.surface_view);
        mEnd = findViewById(R.id.time_bar);
        mStart = findViewById(R.id.time_current_bar);
        videoPlayPause = findViewById(R.id.video_play);
        mSeekBar = findViewById(R.id.seek_bar);
        mProgressBar = findViewById(R.id.progress_show);
        video_down = findViewById(R.id.video_down);
        video_fast = findViewById(R.id.video_fast);
        mLinearTitle = findViewById(R.id.linear_title);
        mLinearSeekBar = findViewById(R.id.linear_seek);
        mLinearImage = findViewById(R.id.linear_image);
        video_back.setOnClickListener(this);
        videoPlayPause.setOnClickListener(this);
        video_fast.setOnClickListener(this);
        video_down.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mLinearTitle.setOnClickListener(this);
        mLinearSeekBar.setOnClickListener(this);
        mLinearImage.setOnClickListener(this);
        mSurfaceView.setOnClickListener(this);
    }

    private void updateView() {
        try {
            if (mJson != null) {
                //通过json传递过来的数据
                Log.i(TAG, "updateView: 电影播放");
                movieList = ImoranResponseToBeanUtils.handleVedioData(mJson).getVideoData()
                        .getContent().getVideoReply().getMoviesList();
                if (movieList !=null && movieList.size()>0){
                    video_title.setText(movieList.get(0).getTitle());
                }else {
                    noMessage();
                }
            } else {
                if (movieList != null) {
                    //通过intent传递过来的数据
                    video_title.setText(movieList.get(mIndex).getTitle());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_back:
                finish();
                break;
            case R.id.video_play:
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    onContinue();
                }
                break;
            case R.id.video_down:
                fastBackForward();
                break;
            case R.id.video_fast:
                fastForward();
                break;
            case R.id.surface_view:

//                mLinearTitle.setVisibility(View.VISIBLE);
//                mLinearSeekBar.setVisibility(View.VISIBLE);
//                mLinearImage.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 播放资源初始化
     **/
    private void initPlayer() {
        //设置视频流类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //设置播放时打开屏幕
        mSurfaceView.getHolder().setKeepScreenOn(true);
        isSurfaceCreated = false;
        //创建surfaceView
        createSurface();
        flag = true;
    }

    //创建完毕页面后需要将播放操作延迟10ms防止因surface创建不及时导致播放失败
    private void playDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSurfaceCreated && flag) {
                    try {
                        play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 20);
    }

    //创建视图
    private void createSurface() {
        surfaceHolder = mSurfaceView.getHolder();
        //兼容4.0以下的版本
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
    }

    //视频播放方法
    private void play() {
        int i = new Random().nextInt(url.length);
        mediaPlayer.reset();
        flag = true;
        try {
            surfaceHolder = mSurfaceView.getHolder();
            mediaPlayer.setDisplay(surfaceHolder);
            try {
                mediaPlayer.setDataSource(url[i]);
                mediaPlayer.prepareAsync();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            mLastState = Media.States.PLAY;
            isPlaying = true;
            sendBroadcast(new Intent(Media.ACTION_PLAY));
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.video_play_error), Toast.LENGTH_SHORT).show();
        }
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mLastState = Media.States.PLAY;
            isPlaying=true;
            sendBroadcast(new Intent(Media.ACTION_PLAY));
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int musicTime = i / 1000;
        DecimalFormat df = new DecimalFormat("00");
        int minute = musicTime / 60;
        String minuteStr = df.format(minute);
        int seconds = musicTime % 60;
        String secondsStr = df.format(seconds);
        String a = minuteStr + ":" + secondsStr;
        mStart.setText(a);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
    }

    //判断播放指令
    private void dealwithAction(String json) {
        jsonData = JsonUtil.createJsonData(json);
        if (jsonData != null) {
            String type = jsonData.getType();
            switch (type) {
                case "end":
                case "pause":
                    //暂停
                    pause();
                    break;
                case "continue":
                case "play":
                    //继续
                    onContinue();
                    break;
                case "skipto_start":
                    //从头播放
                    playIndex();
                    break;
                case "fast_backward":
                    //快退
                    mBackTime = getBackTime(jsonData);
                    if (mBackTime != null) {
                        backForawrd(mBackTime);
                    } else {
                        fastBackForward();
                    }
                    break;
                case "fast_forward":
                    //快进
                    mFastTime = getFastTime(jsonData);
                    if (mFastTime != null) {
                        fastTime(mFastTime);
                    } else {
                        fastForward();
                    }
                    break;
                case "jump_time":
                    //快进到多少秒
                    mJumpTime = getJumpTime(jsonData);
                    fastJump(mJumpTime);
                    break;
                case "back":
                    finish();
                    break;
                default:
                    break;
            }
            //通知service指令执行成功
            sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
        }
    }
    //对domain是movie,type不是movie进行处理
    private void getType(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (domain.equals("movie") && type.equals("movie_detail") ||
                    domain.equals("movie") && type.equals("movie_detail_director")
                    ||domain.equals("movie")&& type.equals("movie_photo")) {
                TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.music_not_support), true);
                ToastUtil.showToast(this,"暂不支持此种问话");
                noMessage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //无数据时界面显示
    private void noMessage() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.video_not_find));
        builder.setNegativeButton(getResources().getString(R.string.dish_yes), new DialogInterface.OnClickListener() {
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

    //暂停播放
    private void pause() {
        mLastState = Media.States.PAUSE;
        isPlaying=false;
        sendBroadcast(new Intent(Media.ACTION_PAUSE));
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            position = mediaPlayer.getCurrentPosition();
            videoPlayPause.setImageResource(R.mipmap.video_pause);
        }
    }
    private void mediaPause(){
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            position = mediaPlayer.getCurrentPosition();
            videoPlayPause.setImageResource(R.mipmap.video_pause);
        }
    }
    //继续播放
    private void onContinue() {
        mLastState = Media.States.PLAY;
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            videoPlayPause.setImageResource(R.mipmap.video_play);
            isPlaying=true;
            sendBroadcast(new Intent(Media.ACTION_PLAY));

        }
    }

    //从头播放
    private void playIndex() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                mLastState = Media.States.PLAY;
                isPlaying=true;
                sendBroadcast(new Intent(Media.ACTION_PLAY));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //快进
    private void fastForward() {
        try {
            if (mediaPlayer != null) {
                position = mediaPlayer.getCurrentPosition() + 15000;
                if (position <= mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                }else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                    mediaPlayer.start();
                }
                mLastState = Media.States.PLAY;
                isPlaying=true;
                sendBroadcast(new Intent(Media.ACTION_PLAY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //快退
    private void fastBackForward() {
        try {
            if (mediaPlayer != null) {
                position = mediaPlayer.getCurrentPosition() - 15000;
                if (position >= 0) {
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                }else {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
                mLastState = Media.States.PLAY;
                isPlaying=true;
                sendBroadcast(new Intent(Media.ACTION_PLAY));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //快进XXX秒
    private void fastTime(String time) {
        try {
            if (mediaPlayer != null) {
                position = mediaPlayer.getCurrentPosition() + Integer.parseInt(time) * 1000;
                Log.i(TAG, "fastTime: " + position);
                if (position <= mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                }else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                    mediaPlayer.start();
                }
                mLastState = Media.States.PLAY;
                isPlaying=true;
                sendBroadcast(new Intent(Media.ACTION_PLAY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //快进XXX秒，得到秒数
    private String getFastTime(JsonData jsonData) {
        try {
            JSONObject reply = jsonData.getContent().getJSONObject("reply");
            JSONArray jump_time = reply.getJSONArray("fast_forward");
            if (jump_time != null && jump_time.length() > 0) {
                JSONObject fast = jump_time.getJSONObject(0);
                mFastTime = fast.getString("time");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mFastTime;
    }

    //快进到XXX秒，得到秒数
    private String getJumpTime(JsonData jsonData) {
        try {
            JSONObject reply = jsonData.getContent().getJSONObject("reply");
            JSONArray jump = reply.getJSONArray("jump_time");
            JSONObject time = jump.getJSONObject(0);
            mJumpTime = time.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mJumpTime;
    }

    //快进到XXX秒
    private void fastJump(String time) {
        try {
            if (mediaPlayer != null) {
                position = Integer.parseInt(time) * 1000;
                if (position <= mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                }else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                    mediaPlayer.start();
                }
                mLastState = Media.States.PLAY;
                isPlaying=true;
                sendBroadcast(new Intent(Media.ACTION_PLAY));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //快退，得到快退时间
    private String getBackTime(JsonData jsonData) {
        try {
            JSONObject reply = jsonData.getContent().getJSONObject("reply");
            JSONArray back = reply.getJSONArray("fast_backward");
            JSONObject time = back.getJSONObject(0);
            mBackTime = time.getString("time");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBackTime;
    }

    //快退XXX秒
    private void backForawrd(String time) {
        try {
            if (mediaPlayer != null) {
                position = mediaPlayer.getCurrentPosition() - Integer.parseInt(time) * 1000;
                if (position >= 0) {
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                }else {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
                mLastState = Media.States.PLAY;
                isPlaying=true;
                sendBroadcast(new Intent(Media.ACTION_PLAY));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //將int时间转换成mm:ss
    private String formatTime() {
        int time_all = mediaPlayer.getDuration();
        int musicTime = time_all / 1000;
        DecimalFormat df = new DecimalFormat("00");
        int minute = musicTime / 60;
        String minuteStr = df.format(minute);
        int seconds = musicTime % 60;
        String secondsStr = df.format(seconds);
        time = minuteStr + ":" + secondsStr;
        return time;
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

    @Override
    public void onSessionStateChanged(SessionState state) {
        super.onSessionStateChanged(state);
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
}
