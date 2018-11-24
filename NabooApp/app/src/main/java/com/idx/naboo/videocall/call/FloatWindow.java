package com.idx.naboo.videocall.call;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMCallSurfaceView;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.videocall.VideoIntent;
import com.idx.naboo.videocall.utils.SpUtils;
import com.superrtc.sdk.VideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by danny on 2017/3/27.
 * 视频通话悬浮窗操作类
 */
public class FloatWindow {
    private static final String TAG = FloatWindow.class.getSimpleName();
    private Context context;// 上下文菜单

    // 当前单例类实例
    private static FloatWindow instance;

    private WindowManager windowManager = null;
    private WindowManager.LayoutParams layoutParams = null;

    // 悬浮窗需要显示的布局
    private View mFloatView;

    private EMCallSurfaceView localView;
    private EMCallSurfaceView oppositeView;

    public FloatWindow(Context context) {
        this.context = context.getApplicationContext();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static FloatWindow getInstance(Context context) {
        if (instance == null) {
            instance = new FloatWindow(context);
        }
        return instance;
    }

    /**
     * 添加悬浮窗
     */
    public void addFloatWindow() {
        if (mFloatView != null) {return;}
        EventBus.getDefault().register(this);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置窗口标志类型，其中 FLAG_NOT_FOCUSABLE 是放置当前悬浮窗拦截点击事件，造成桌面控件不可操作
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

        mFloatView = LayoutInflater.from(context).inflate(R.layout.video_call_float_window, null);
        windowManager.addView(mFloatView, layoutParams);
        setupSurfaceView();

        // 当点击悬浮窗时，返回到通话界面
        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, VideoCallActivity.class);
                SpUtils.putBoolean(context,"float_into_video_call",true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            boolean result = false;

            float x = 0;
            float y = 0;
            float startX = 0;
            float startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        result = false;
                        x = event.getX();
                        y = event.getY();
                        startX = event.getRawX();
                        startY = event.getRawY();
                        Log.d(" " + startX, " " + startY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(" " + event.getRawX(), " " + event.getRawY());
                        if (Math.abs(event.getRawX() - startX) > 20 || Math.abs(event.getRawY() - startY) > 20) {result = true;}
                        layoutParams.x = (int) (event.getRawX() - x);
                        layoutParams.y = (int) (event.getRawY() - y - 25);
                        windowManager.updateViewLayout(mFloatView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:break;
                }
                return result;
            }
        });
    }

    /**
     * 设置本地与远程画面显示控件
     */
    private void setupSurfaceView() {
        mFloatView.findViewById(R.id.layout_call_video).setVisibility(View.VISIBLE);
        RelativeLayout surfaceLayout = mFloatView.findViewById(R.id.layout_call_video);

        // 将 SurfaceView设置给 SDK
        surfaceLayout.removeAllViews();

        localView = new EMCallSurfaceView(context);
        oppositeView = new EMCallSurfaceView(context);

        int lw = (int)context.getResources().getDimension(R.dimen.float_little_surface_local_width);
        int lh = (int)context.getResources().getDimension(R.dimen.float_little_surface_local_height);
        int ow = (int)context.getResources().getDimension(R.dimen.float_little_surface_opposite_width);
        int oh = (int)context.getResources().getDimension(R.dimen.float_little_surface_opposite_height);
        RelativeLayout.LayoutParams localParams = new RelativeLayout.LayoutParams(lw, lh);
        RelativeLayout.LayoutParams oppositeParams = new RelativeLayout.LayoutParams(ow, oh);
        // 设置本地图像靠右
        localParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        // 设置本地预览图像显示在最上层
        localView.setZOrderOnTop(false);
        localView.setZOrderMediaOverlay(true);
        // 将 view 添加到界面
        surfaceLayout.addView(localView, localParams);
        surfaceLayout.addView(oppositeView, oppositeParams);

        // 设置通话界面画面填充方式
        localView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        oppositeView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        // 设置本地以及对方显示画面控件，这个要设置在上边几个方法之后，不然会概率出现接收方无画面
        EMClient.getInstance().callManager().setSurfaceView(localView, oppositeView);
    }

    /**
     * 停止悬浮窗
     */
    public void removeFloatWindow() {
        if (localView != null) {
            EventBus.getDefault().unregister(this);
            if (localView.getRenderer() != null) {
                localView.getRenderer().dispose();
            }
            localView.release();
            localView = null;
        }
        if (oppositeView != null) {
            if (oppositeView.getRenderer() != null) {
                oppositeView.getRenderer().dispose();
            }
            oppositeView.release();
            oppositeView = null;
        }
        if (windowManager != null && mFloatView != null) {
            windowManager.removeView(mFloatView);
            mFloatView = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(CallEvent event) {
        if (event.isState()) {
            refreshCallView(event);
        }
    }

    /**
     * 刷新通话界面
     */
    private void refreshCallView(CallEvent event) {
        EMCallStateChangeListener.CallError callError = event.getCallError();
        EMCallStateChangeListener.CallState callState = event.getCallState();
        switch (callState) {
            case CONNECTING:Log.i(TAG,"正在呼叫对方" + callError);break;
            case CONNECTED:Log.i(TAG,"正在连接" + callError);break;
            case ACCEPTED:Log.i(TAG,"通话已接通");break;
            case DISCONNECTED:
                Log.i(TAG,"通话已结束" + callError);
                CallManager.getInstance().cancelCallNotification();
                CallManager.getInstance().removeFloatWindow();
                CallManager.getInstance().endCall();
                mHandler.sendEmptyMessageDelayed(0,2000);
                break;
            case NETWORK_DISCONNECTED:
                Toast.makeText(context, context.getResources().getString(R.string.huan_xin_call_net_down), Toast.LENGTH_SHORT).show();
                CallManager.getInstance().cancelCallNotification();
                CallManager.getInstance().removeFloatWindow();
                CallManager.getInstance().endCall();
                mHandler.sendEmptyMessageDelayed(0,2000);
                break;
            case NETWORK_UNSTABLE:
                if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                    Log.i(TAG,"没有通话数据" + callError);
                } else {
                    Log.i(TAG,"网络不稳定" + callError);
                }
                break;
            case NETWORK_NORMAL:Log.i(TAG,"网络正常");break;
            case VIDEO_PAUSE:Log.i(TAG,"视频传输已暂停");break;
            case VIDEO_RESUME:Log.i(TAG,"视频传输已恢复");break;
            case VOICE_PAUSE:Log.i(TAG,"语音传输已暂停");break;
            case VOICE_RESUME:Log.i(TAG,"语音传输已恢复");break;
            default:break;
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "onCallStateChanged: ");
            Intent intent = new Intent();
            intent.setAction(VideoIntent.ACTION_CALL_END);
            NabooApplication.getInstance().getBaseContext().sendBroadcast(intent);
        }
    };
}
