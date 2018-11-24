package com.idx.naboo.video.ui;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.idx.naboo.BaseActivity;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.music.utils.ToastUtil;
import com.idx.naboo.service.Execute;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.utils.ImoranResponseToBeanUtils;
import com.idx.naboo.video.VideoContentInterface;
import com.idx.naboo.video.data.ImoranResponseVideo;
import com.idx.naboo.video.data.Movie;
import com.idx.naboo.video.data.VideoContentAdapter;
import com.idx.naboo.videocall.utils.SpUtils;

import net.imoran.sdk.entity.info.VUIDataEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseActivity {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private IService mIService;
    private RecyclerView mRecyclerView;
    private VideoContentAdapter mVideoAdapter;
    private List<Movie> mMovies = new ArrayList<>();
    private String mJson;
    private ImoranResponseVideo imoranResponseVideo;
    private LinearLayoutManager mLayoutManager;
    private Context mContext;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private String mQueryId;
    private String mPageId;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(VideoActivity.this);
            mJson = mIService.getJson();
            if (mJson != null && (!mJson.equals(""))) {
                getType(mJson);
//                SpUtils.put(VideoActivity.this, Constant.VIDEO_JSON,mJson);
                parseJson(mJson);
                parseQueryId(mJson);
                getTTs(mJson);
                dialogDismiss();
                updateView();
                listOnItemClick();
                //通知service数据更新成功
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            }else {
                VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);
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
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (type.equals("movie")) {
                getType(json);
                parseJson(json);
                parseQueryId(json);
                getTTs(json);
                if (mMovies != null && mMovies.size() > 0) {
                    dialogDismiss();
                    updateView();
                }
                listOnItemClick();
                //通知service数据更新成功
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            } else if (domain.equals("cmd")) {
                JsonData jsonData = JsonUtil.createJsonData(json);
                if (jsonData != null) {

                    String type1 = jsonData.getType();
                    dealWith(type1);
                    getTTs(json);
                }
                //通知service数据更新成功
                sendBroadcast(new Intent(Execute.ACTION_SUCCESS));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mRecyclerView = findViewById(R.id.video_recyclerView);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), SpeakService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTSManager.getInstance(getBaseContext()).stop();
        unbindService(connection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateView() {
        if (mMovies != null && mMovies.size() > 0) {
            //设置横向滑动
            mLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mVideoAdapter = new VideoContentAdapter(this, mMovies);
            mRecyclerView.setAdapter(mVideoAdapter);
            mRecyclerView.setItemViewCacheSize(mMovies.size());
        } else {
            noMessage();
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

    //解析json
    private void parseJson(String json) {
        imoranResponseVideo = ImoranResponseToBeanUtils.handleVedioData(json);
        if (ImoranResponseToBeanUtils.isImoranVideNull(imoranResponseVideo)) {
            //添加场景
            String type=imoranResponseVideo.getVideoData().getContent().getType();
            if (imoranResponseVideo.getVideoData().getIntention().equals("searching") &&
                    (!type.equals("cinema"))) {
                mMovies = imoranResponseVideo.getVideoData().getContent().getVideoReply().getMoviesList();
            }
        }
    }
    //设置场景
    private void parseQueryId(String json) {
        imoranResponseVideo = ImoranResponseToBeanUtils.handleVedioData(json);
        if (ImoranResponseToBeanUtils.isImoranVideNull(imoranResponseVideo)) {
            //添加场景
            mQueryId = imoranResponseVideo.getVideoData().getQueryid();
            String domain = imoranResponseVideo.getVideoData().getDomain();
            String intention = imoranResponseVideo.getVideoData().getIntention();
            String type = imoranResponseVideo.getVideoData().getContent().getType();
            mPageId = domain + "_" + intention + "_" + type + "_" + "VideoActivity";
            VUIDataEntity.wrapData(((NabooApplication) getApplication()).getDataEntity(), mPageId, mQueryId);

        }
    }
    //对recyclerView设置点击事件
    private void listOnItemClick() {
        try {
            mVideoAdapter.setVideoContentInterface(new VideoContentInterface() {
                @Override
                public void onVideoItem(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("video_index", position);
                    bundle.putSerializable("video", (Serializable) mMovies);
                    Intent intent = new Intent(VideoActivity.this, VideoDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTTs(String json) {
        JsonData jsonData = JsonUtil.createJsonData(json);
        if (jsonData != null) {
            String tts = jsonData.getTts();
            TTSManager.getInstance(getBaseContext()).speak(tts, true);

        }
    }
    //对domain是movie,type不是movie进行处理
    private void getType(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (domain.equals("movie") && type.equals("movie_detail") ||
                    domain.equals("movie") && type.equals("movie_detail_director")) {
                TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.music_not_support), true);
                ToastUtil.showToast(this,"暂不支持此种问话");
                noMessage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void dealWith(String type) {
        switch (type) {
            case "next_page"://下一页
                next();
                break;
            case "last_page"://上一页
                previous();
                break;
            case "back"://返回
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 下一页
     */
    private void next() {
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
        int count = lastItemPosition - firstItemPosition;
        Log.d(TAG, "firstItemPosition: " + firstItemPosition);
        Log.d(TAG, "lastItemPosition: " + lastItemPosition);
        if (lastItemPosition != mMovies.size() - 1) {
            mLayoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
            mLayoutManager.setStackFromEnd(true);
        }
        if (mMovies.size() - count < lastItemPosition) {
            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.video_page_last), true);
        }
    }

    /**
     * 上一页
     */
    private void previous() {
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
        int count = lastItemPosition - firstItemPosition;
        Log.d(TAG, "pre: " + firstItemPosition);
        if (firstItemPosition > count) {
            mLayoutManager.scrollToPositionWithOffset(firstItemPosition - count, 0);
        } else {
            mLayoutManager.scrollToPositionWithOffset(0, 0);
            TTSManager.getInstance(getBaseContext()).speak(getResources().getString(R.string.video_page_first), true);
        }
        mLayoutManager.setStackFromEnd(true);
    }
}
