package com.idx.naboo;

import android.app.ActivityManager;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.idx.naboo.videocall.call.CallManager;
import com.idx.naboo.videocall.call.VideoCallReceiver;
import net.imoran.sdk.entity.info.VUIDataEntity;
import java.util.Iterator;
import java.util.List;

/**
 * Created by danny on 4/18/18.
 */

public class NabooApplication extends MultiDexApplication {
    private static final String TAG = NabooApplication.class.getSimpleName();
    private VideoCallReceiver mCallReceiver;
    private VUIDataEntity mDataEntity = new VUIDataEntity();
    private static NabooApplication mNabooApplication;

    public synchronized VUIDataEntity getDataEntity() {
        return mDataEntity;
    }

    public synchronized void setDataEntity(VUIDataEntity mDataEntity) {
        this.mDataEntity = mDataEntity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNabooApplication = this;
        initHyphenate();
        //LitePalApplication.initialize(mContext);
        MultiDex.install(this);
        VUIDataEntity.wrapData(mDataEntity, "pageId_default", "");
    }

    public synchronized static NabooApplication getInstance() {
        return mNabooApplication;
    }

    private void initHyphenate() {
        int pid = android.os.Process.myPid();
        String pName = getProcessName(pid);
        if (pName == null || !pName.equalsIgnoreCase(getBaseContext().getPackageName())) return;
        EMOptions options = new EMOptions();
        options.setAutoLogin(true);//自动登录
        options.setAcceptInvitationAlways(false);//不自动添加好友
        EMClient.getInstance().init(getBaseContext(), options);
//        EMClient.getInstance().setDebugMode(true);//测试阶段使用

        CallManager.getInstance().init(getBaseContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (mCallReceiver == null) {
            mCallReceiver = new VideoCallReceiver();
        }
        registerReceiver(mCallReceiver, filter);

    }

    private String getProcessName(int id) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List list = manager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) i.next();
            if (info.pid == id) {
                return info.processName;
            }
        }
        return null;
    }

}
