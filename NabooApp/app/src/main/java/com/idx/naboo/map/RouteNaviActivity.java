package com.idx.naboo.map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.map.mapUtils.TTSController;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SessionState;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.listener.DataListener;


public class RouteNaviActivity extends Activity implements AMapNaviViewListener,DataListener{


	AMapNaviView mAMapNaviView;
	AMapNavi mAMapNavi;
	TTSController mTtsManager;
	private IService mIService;

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIService = (IService) service;
			mIService.setDataListener(RouteNaviActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity_basic_navi);
		mTtsManager = TTSController.getInstance(getApplicationContext());
		mTtsManager.init();
		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.addAMapNaviListener(mTtsManager);
		mAMapNavi.setEmulatorNaviSpeed(60);
		mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
		mAMapNaviView.onCreate(savedInstanceState);
		mAMapNaviView.setAMapNaviViewListener(this);
		boolean gps=getIntent().getBooleanExtra("gps", false);
		if(gps){
			mAMapNavi.startNavi(1);
		}else{
			mAMapNavi.startNavi(2);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAMapNaviView.onResume();
		//绑定Service
		bindService(new Intent(getBaseContext(), SpeakService.class), conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAMapNaviView.onPause();
		mTtsManager.stopSpeaking();
		//解绑
        unbindService(conn);
        //结束
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAMapNavi.stopNavi();
		mAMapNavi = null;
		mAMapNaviView.onDestroy();
		mAMapNaviView = null;
		mIService = null;
		mTtsManager = null;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	/**==================================================以下为AMapNaviViewListener监听回调======================================*/

	/**
	 * 界面右下角功能设置按钮的回调接口
	 */
	@Override
	public void onNaviSetting() {

	}

	/**
	 * 导航页面左下角返回按钮点击后弹出的『退出导航对话框』中选择『确定』后的回调接口
	 */
	@Override
	public void onNaviCancel() {
		finish();
	}

	/**
	 * 导航页面左下角返回按钮的回调接口 false-由SDK主动弹出『退出导航』对话框，true-SDK不主动弹出『退出导航对话框』，由用户自定义
	 * @return
	 */
	@Override
	public boolean onNaviBackClick() {
		return false;
	}

	/**
	 * 导航界面地图状态的回调
	 * isLock - 地图状态，0:车头朝上状态；1:非锁车状态,即车标可以任意显示在地图区域内。
	 * @param isLock
	 */
	@Override
	public void onNaviMapMode(int isLock) {

	}

	/**
	 * 界面左上角转向操作的点击回调
	 */
	@Override
	public void onNaviTurnClick() {

	}

	/**
	 * 界面下一道路名称的点击回调
	 */
	@Override
	public void onNextRoadClick() {

	}

	/**
	 * 界面全览按钮的点击回调
	 */
	@Override
	public void onScanViewButtonClick() {

	}

	/**
	 * 是否锁定地图的回调
	 * @param b
	 */
	@Override
	public void onLockMap(boolean b) {

	}

	/**
	 * 导航view加载完成回调
	 */
	@Override
	public void onNaviViewLoaded() {

	}


	@Override
	public void onJsonReceived(String json) {
		JsonData jsonData = JsonUtil.createJsonData(json);
		//处理返回的情况
		if (jsonData.getDomain().equals("cmd") && jsonData.getType().equals("back")) {
			//结束掉
			this.finish();
		}
	}

	@Override
	public void onSessionStateChanged(SessionState state) {

	}
}