package com.idx.naboo.videocall.friend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.listener.DataListener;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.utils.SpUtils;

import java.util.List;

public class AddFriendActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AddFriendActivity.class.getSimpleName();
    private Context mContext;
    private Button mBack;
    private EditText mFriendName;
    private EditText mFriendAccount;
    private Button mSave;
    private String mName;
    private String mAccount;
    private String mCurrentUserName;//当前用户姓名
    private SharedPreferencesUtil mPreferencesUtil;
    private IService mIService;
    private InputMethodManager mInputMethodManager;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener((DataListener) AddFriendActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mContext=this;
        initView();
        mPreferencesUtil = new SharedPreferencesUtil(mContext);
        mCurrentUserName = mPreferencesUtil.getUUID("name");
        Log.d(TAG, "onCreate: "+mCurrentUserName);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("back")){finish();}
    }

    private void initView() {
        mBack=findViewById(R.id.add_friend_back);
        mFriendName = findViewById(R.id.add_friend_name);
        mFriendAccount = findViewById(R.id.add_friend_phone);
        mSave = findViewById(R.id.add_friend_save);
        mBack.setOnClickListener(this);
        mSave.setOnClickListener(this);

        mFriendAccount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    Log.d(TAG, "onKey: ");
//                    mInputMethodManager.hideSoftInputFromWindow(mNote.getWindowToken(), 0);//隐藏键盘

                    mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_friend_save:
                addFriend();
                break;
            case R.id.add_friend_back:
                back();
                break;
        }
    }

    /**
     * 返回
     */
    private void back() {
        finish();
        mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 添加好友
     */
    private void addFriend() {
        mName=mFriendName.getText().toString().trim();
        mAccount=mFriendAccount.getText().toString().trim();
        if (!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mName)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(new EMValueCallBack<List<String>>() {
                        @Override
                        public void onSuccess(List<String> list) {
                            if (list.contains(mAccount)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, getResources().getString(R.string.huan_xin_friend_exist), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                save();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            save();
                        }
                    });
                }
            }).start();
        }else {
            if (TextUtils.isEmpty(mAccount) && TextUtils.isEmpty(mName)){
                Toast.makeText(mContext,getResources().getString(R.string.huan_xin_friend_name_and_account_not_empty),Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(mAccount)){
                Toast.makeText(mContext, getResources().getString(R.string.huan_xin_friend_account_not_empty), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(mName)) {
                Toast.makeText(mContext, getResources().getString(R.string.huan_xin_friend_name_not_empty), Toast.LENGTH_SHORT).show();
            }
        }
        mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 保存好友
     */
    private void save(){
        try {
            EMClient.getInstance().contactManager().addContact(mAccount, mCurrentUserName);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, getResources().getString(R.string.huan_xin_friend_apply_send), Toast.LENGTH_SHORT).show();
                    SpUtils.put(mContext, Constant.VIDEO_CALL_TO_USER_NAME,mName);
                    SpUtils.put(mContext,Constant.VIDEO_CALL_TO_USERACCOUNT,mAccount);
                    back();
                }
            });
        } catch (HyphenateException e) {
            e.printStackTrace();
            Log.d(TAG, "addFriends: 添加好友失败");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, getResources().getString(R.string.huan_xin_friend_add_fail), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(AddFriendActivity.this, SpeakService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContext!=null){mContext=null;}
        if (mInputMethodManager!=null){mInputMethodManager=null;}
        if (mIService!=null){mIService=null;}
        if (mPreferencesUtil!=null){mPreferencesUtil=null;}
    }
}
