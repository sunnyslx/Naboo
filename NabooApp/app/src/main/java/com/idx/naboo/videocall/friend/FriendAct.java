package com.idx.naboo.videocall.friend;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.idx.naboo.BaseActivity;
import com.idx.naboo.R;
import com.idx.naboo.data.JsonData;
import com.idx.naboo.data.JsonUtil;
import com.idx.naboo.music.utils.ToastUtil;
import com.idx.naboo.service.IService;
import com.idx.naboo.service.SpeakService;
import com.idx.naboo.service.listener.DataListener;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.user.personal_center.Personal_center;
import com.idx.naboo.utils.NetStatusUtils;
import com.idx.naboo.videocall.Missed;
import com.idx.naboo.videocall.call.CallManager;
import com.idx.naboo.videocall.call.VideoCallActivity;
import com.idx.naboo.videocall.call.data.CallDataSource;
import com.idx.naboo.videocall.call.data.CallInjection;
import com.idx.naboo.videocall.call.data.CallRepository;
import com.idx.naboo.videocall.call.data.MissedCall;
import com.idx.naboo.videocall.friend.data.Friend;
import com.idx.naboo.videocall.friend.data.FriendDataSource;
import com.idx.naboo.videocall.friend.data.FriendInjection;
import com.idx.naboo.videocall.friend.data.FriendRepository;
import com.idx.naboo.videocall.friend.data.HxUser;
import com.idx.naboo.videocall.friend.data.NotificationMissedCallListener;
import com.idx.naboo.videocall.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友界面
 * Created by danny on 4/21/18.
 */

public class FriendAct extends BaseActivity implements View.OnClickListener, NotificationMissedCallListener {
    private static final String TAG = FriendAct.class.getSimpleName();
    private static final int FRIEND=0;
    private static final int SEARCH_FRIEND=1;
    private int mCurrentState=FRIEND;
    private Context mContext;

    private Button mBack;
    private ImageButton mMenu;
    private LinearLayout mLlSearchShow;
    private EditText mSearchContent;
    private ImageButton mSearchClear;
    private ImageButton mSearchBook;
    private ImageButton mSearch;
    private ListView mListView;
    private ImageView mNoFriend;
    private FriendAdapter mAdapter;
    private AlertDialog mDialog;

    private List<Missed> mMisseds;

    private List<Friend> mLists;//数据库保存好友信息
    private String mCurrentUser;//当前用户账号
    private String mFriAccount;//好友账号
    private String mFriName;//好友名字
    private boolean isSelecting = false;//是否正在选择

    private String mUserId;//数据库中存储的用户的uuid
    private FriendRepository mRepository;
    private CallRepository mCallRepository;

    private IService mIService;
    private SpeakService mService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mIService = (IService) service;
            mIService.setDataListener((DataListener) FriendAct.this);
            mService= ((SpeakService.SpeakBinder) service).getService();
            if (mService!=null){
                mService.setRejectFriendListener(new RejectFriendListener() {
                    @Override
                    public void reject(String rejectNumber) {Log.d(TAG, "reject: "+rejectNumber);}
                });

                mService.setDeleteFriendListener(new DeleteFriendListener() {
                    @Override
                    public void delete(String deleteNumber) {
                        query();
                        Log.d(TAG, "delete: "+deleteNumber);
                    }
                });

                mService.setAgreeFriendListener(new AgreeFriendListener() {
                    @Override
                    public void agree(String agreeNumber) {
                        query();
                        Log.d(TAG, "agree: "+agreeNumber);
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_friend);
        mContext = this;
        initView();
        mCurrentUser = EMClient.getInstance().getCurrentUser();
        VideoCallActivity.getInstance().setNotificationMissedCallListener(this);
    }

    @Override
    public void onJsonReceived(String json) {
        super.onJsonReceived(json);
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String type = jsonUtil.getType();
        Log.d(TAG, "onJsonReceived: "+ type);
        if (type.equals("back")){
            startActivity(new Intent(FriendAct.this,Personal_center.class));
            finish();
        }
    }

    /**
     * 收到好友邀请
     */
    private void showAddDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_receiver_and_reject, null);
        TextView friendName = view.findViewById(R.id.friend_name_and_phone);
        Button agree = view.findViewById(R.id.agree);
        Button reject = view.findViewById(R.id.reject);
        agree.setOnClickListener(this);
        reject.setOnClickListener(this);

        friendName.setText(mFriName + mFriAccount);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mDialog = builder.setView(view).create();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRepository = FriendInjection.getInstance(mContext);
        mCallRepository = CallInjection.getInstance(mContext);
        mBack = findViewById(R.id.friend_book_back);
        mMenu = findViewById(R.id.friend_book_menu);
        mLlSearchShow = findViewById(R.id.friend_book_search_text);
        mSearchContent = findViewById(R.id.friend_book_search_content);
        mSearchClear = findViewById(R.id.friend_book_search_delete);
        mSearchBook = findViewById(R.id.friend_book_search_friend);
        mSearch = findViewById(R.id.friend_book_search);
        mListView = findViewById(R.id.friend_book_list);
        mNoFriend = findViewById(R.id.friend_book_no_friend);
        mBack.setOnClickListener(this);
        mMenu.setOnClickListener(this);
        mSearchClear.setOnClickListener(this);
        mSearchBook.setOnClickListener(this);
        mSearch.setOnClickListener(this);

        //搜索键搜索
        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                nameSearchFriend();
                return true;
            }
        });

        //内容改变就搜索
        mSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {nameSearchFriend();}

            @Override
            public void afterTextChanged(Editable editable) {Log.d(TAG, "onTextChanged: editable:" + editable.toString());}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        query();
    }

    /**
     * 名字查询好友
     */
    private void nameSearchFriend(){
        mCurrentState=SEARCH_FRIEND;
        String content=mSearchContent.getText().toString().trim();
        mRepository.queryMoreAliasFriend(content + "%%", new FriendDataSource.LoadAllFriendCallback() {
            @Override
            public void onSuccess(List<Friend> friends) {
                Log.d(TAG, "onSuccess: 根据名字查到了");
                mLists = friends;
                isHaveFriend();
            }

            @Override
            public void onError() {
                if (mLists!=null){
                    mLists.clear();
                    mLists=null;
                }
                isHaveFriend();
                Log.d(TAG, "onError: 根据名字没查到");
            }
        });
    }

    /**
     * 查询好友
     */
    private void query() {
        mCurrentState=FRIEND;
        if (!TextUtils.isEmpty(mCurrentUser)) {
            if (mLists!=null){
                mLists.clear();
                mLists=null;
            }
            mRepository.queryUser(mCurrentUser, new FriendDataSource.LoadUserCallback() {
                @Override
                public void onSuccess(HxUser user) {
                    mUserId = user.id;
                    mRepository.queryAllFriend(mUserId, new FriendDataSource.LoadAllFriendCallback() {
                        @Override
                        public void onSuccess(List<Friend> friends) {
                            Log.d(TAG, "onSuccess: 查到好友");
                            mLists = friends;
                            isHaveFriend();
                            mMisseds=new ArrayList<>();
                            for (int i=0;i<friends.size();i++){
                                Missed missed = new Missed();
                                missed.account=friends.get(i).friendAccount;
                                Map<Integer,Integer> map=new HashMap<>();
                                map.put(0,0);
                                missed.flag=map;
                                Log.d(TAG, "onSuccess: "+missed.toString());
                                mMisseds.add(missed);
                            }
                            queryUnMissed();
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "onError: 还没有好友");
                            isHaveFriend();
                        }
                    });
                }

                @Override
                public void onError() {
                    Log.d(TAG, "onError: 刚刚登录");
                    isHaveFriend();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(new Intent(FriendAct.this, SpeakService.class), conn, BIND_AUTO_CREATE);
        mFriAccount = SpUtils.get(mContext, "notification_account", "");
        mFriName = SpUtils.get(mContext, "notification_name", "");
        Log.d(TAG, "onResume: " + mFriAccount + "-" + mFriName);
        if (!TextUtils.isEmpty(mFriAccount) && !TextUtils.isEmpty(mFriName)) {
            showAddDialog();
        } else {
            Log.d(TAG, "onResume: data null");
        }
    }

    /**
     * 是否有好友
     */
    private void isHaveFriend() {
        if (mLists != null && mLists.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            mNoFriend.setVisibility(View.GONE);
            setAdpter();
        } else {
            mListView.setVisibility(View.GONE);
            mNoFriend.setVisibility(View.VISIBLE);
            if (mCurrentState==FRIEND) {
                mNoFriend.setBackgroundResource(R.mipmap.add_bg);
            }else {
                mNoFriend.setBackgroundResource(R.mipmap.unfind_bg);
            }
        }
    }

    /**
     * 设置Adapter
     */
    private void setAdpter() {
        if(mAdapter==null) {
            mAdapter = new FriendAdapter();
            mListView.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                ASRManager.getInstance().stop();
                if (NetStatusUtils.isOnline(getBaseContext())) {
                    if (EMClient.getInstance().isLoggedInBefore()) {
                        Log.e(TAG, "delete: " + mLists.get(position).friendAccount);
                        SpUtils.put(FriendAct.this, Constant.VIDEO_CALL_TO_USERNAME, mLists.get(position).alias);
                        SpUtils.put(FriendAct.this, Constant.VIDEO_CALL_TO_USER_ACCOUNT, mLists.get(position).friendAccount);
                        Intent intent = new Intent(mContext, VideoCallActivity.class);
                        CallManager.getInstance().setToChatId(mLists.get(position).friendAccount);
                        CallManager.getInstance().setInComingCall(false);//呼出电话
                        startActivity(intent);
                        final String account = mLists.get(position).friendAccount;
                        Log.e(TAG, "delete: "+account);
                        mCallRepository.queryPointCall(account, new CallDataSource.LoadPointCallCallback() {//删除未接电话
                            @Override
                            public void onSuccess(MissedCall call) {
                                Log.e(TAG, "delete: "+call.callAccount);
                                mCallRepository.deleteCall(account);
                            }

                            @Override
                            public void onError() {}
                        });
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.huan_xin_logout), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    ToastUtil.showToast(getBaseContext(), getString(R.string.net_error));
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(TAG, "onItemLongClick: 条目长按事件");
                final String account = mLists.get(position).friendAccount;

                if (isSelecting) {
                    Log.d(TAG, "onItemLongClick: isSelecting = false");
                } else {
                    Log.d(TAG, "onItemLongClick: isSelecting = true");
                    deleteOrBook(true);
                    isSelecting = true;
                    mAdapter.notifyDataSetInvalidated();
                }
                return true;
            }
        });

        mAdapter.setOnItemDeleteClickListener(new OnItemDeleteListener() {
            @Override
            public void onDeleteClick(String account, int i) {showDeleteDialog(account, i);}
        });
    }

    /**
     * 判断是删除联系人界面还是通讯录界面
     */
    private void deleteOrBook(boolean delete){
        if (delete){
            mBack.setText("删除联系人");
            mMenu.setVisibility(View.GONE);
            mSearch.setVisibility(View.GONE);
        }else {
            mBack.setText(getResources().getString(R.string.call_book));
            mMenu.setVisibility(View.VISIBLE);
            mSearch.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示删除好友对话框
     */
    private void showDeleteDialog(final String account, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_delete_friend_certain, null);
        Button certain = view.findViewById(R.id.certain_delete);
        final Button cancel = view.findViewById(R.id.cancel);
        certain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {certain(account, position);}
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {cancel();}
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mDialog = builder.setView(view).create();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    /**
     * 删除好友
     */
    private void deleteFriend(final String account, final int position) {
        EMClient.getInstance().contactManager().aysncDeleteContact(account, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "删除好友成功", Toast.LENGTH_SHORT).show();
                        if (mLists!=null && mLists.size()>position) {
                            Log.d(TAG, "run: 账号删除好友" + account);
                            mRepository.deleteFriend(account, new FriendDataSource.DeleteFriendSuccessCallback() {
                                @Override
                                public void onSuccess() {
                                    query();
                                    mCallRepository.deleteCall(account);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {Toast.makeText(mContext, "你们还不是好友,没有服务器的弊端!",Toast.LENGTH_SHORT).show();}
                });
            }

            @Override
            public void onProgress(int i, String s) {}
        });
        mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_book_back:
                back();//返回键
                break;
            case R.id.friend_book_menu:
                bookMenu();//菜单
                break;
            case R.id.friend_book_search_delete:
                searchClear();//清除输入内容
                break;
            case R.id.friend_book_search_friend:
                searchFriend();//查询电话簿中好友
                break;
            case R.id.friend_book_search:
                search();//显示查询输入框
                break;
            case R.id.agree:
                agreeAdd();//同意添加好友
                break;
            case R.id.reject:
                reject();//拒绝添加好友
                break;
            default:
                break;
        }
    }

    /**
     * 返回
     */
    private void back() {
        finish();
    }

    /**
     * 菜单
     */
    private void bookMenu() {
        menu(mMenu);
        searchIsShow();
    }

    private void menu(ImageButton view){
        View popupView = FriendAct.this.getLayoutInflater().inflate(R.layout.friend_menu, null);
        Button add=popupView.findViewById(R.id.friend_menu_add);
        Button delete=popupView.findViewById(R.id.friend_menu_delete);
        final PopupWindow window = new PopupWindow(popupView, 300, 160);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddFriendActivity.class);
                startActivity(intent);
                window.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter!=null) {
                    if (isSelecting) {
                        Log.d(TAG, "onItemLongClick: isSelecting = false");
                    } else {
                        Log.d(TAG, "onItemLongClick: isSelecting = true");
                        isSelecting = true;
                        mAdapter.notifyDataSetInvalidated();
                    }
                }
                window.dismiss();
            }
        });
//        window.setAnimationStyle(R.style.popup_window_anim);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.option));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAsDropDown(view, -245, 15);
    }

    /**
     * 清除输入内容
     */
    private void searchClear() {
        if (mLlSearchShow.getVisibility() == View.VISIBLE) {
            mSearchContent.setText("");
        }
    }

    /**
     * 查询电话簿中好友
     */
    private void searchFriend() {nameSearchFriend();}

    /**
     * 显示查找文本框
     */
    private void search() {
        if (mSearch.getVisibility() == View.VISIBLE) {
            mSearch.setVisibility(View.GONE);
            if (mLlSearchShow.getVisibility() == View.GONE) {
                mLlSearchShow.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 显示查询按钮
     */
    private void searchIsShow() {
        if (mLlSearchShow.getVisibility() == View.VISIBLE) {
            if (mLlSearchShow.isFocusable()) {
                Log.d(TAG, "searchIsShow: 有焦点");
            } else {
                mLlSearchShow.setVisibility(View.GONE);
                if (mSearch.getVisibility() == View.GONE) {
                    mSearch.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 同意添加好友
     */
    private void agreeAdd() {
        EMClient.getInstance().contactManager().asyncAcceptInvitation(mFriAccount, new EMCallBack() {
            @Override
            public void onSuccess() {
                Friend friend = new Friend();
                friend.alias = mFriName;
                friend.userId = mUserId;
                friend.friendAccount = mFriAccount;
                mRepository.insertFriend(friend, new FriendDataSource.AddFriendSuccessCallback() {
                    @Override
                    public void onSuccess() {query();}
                });
                Log.d(TAG, "onSuccess: agree" + friend.toString());
                mDialog.dismiss();
                SpUtils.put(mContext, "notification_account", "");
                SpUtils.put(mContext, "notification_name", "");
            }

            @Override
            public void onError(int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "添加失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {}
        });
    }

    /**
     * 拒绝添加好友
     */
    private void reject() {
        try {
            EMClient.getInstance().contactManager().declineInvitation(mFriAccount);
            mDialog.dismiss();
            SpUtils.put(mContext, "notification_account", "");
            SpUtils.put(mContext, "notification_name", "");
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 确定删除
     */
    private void certain(String account, int position) {
        deleteFriend(account, position);
        deleteOrBook(false);
        isSelecting=false;
        mAdapter.notifyDataSetInvalidated();
    }

    /**
     * 取消删除好友
     */
    private void cancel() {
        mDialog.dismiss();
        deleteOrBook(false);
        isSelecting=false;
        mAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLists!=null){mLists.clear();mLists=null;}
        if (mService!=null){mService=null;}
        if (mIService!=null){mIService=null;}
        if (mCallRepository!=null){mCallRepository=null;}
        if (mDialog!=null){mDialog.dismiss();mDialog=null;}
        if (mContext!=null){mContext=null;}
        if (mRepository!=null){mRepository=null;}
    }

    private void queryUnMissed(){
        Log.d(TAG, "queryUnMissed: ");
        mCallRepository.queryAllCall(new CallDataSource.LoadCallCallback() {
            @Override
            public void onSuccess(List<MissedCall> calls) {
                Log.d(TAG, "111111111111: "+calls.size());
                for (int i=0;i<calls.size();i++){
                    for (int j=0; mMisseds!=null && j < mMisseds.size();j++){
                        Log.e(TAG, "未接号码: "+calls.get(i).callAccount);
                        if (mMisseds.get(j).account.equals(calls.get(i).callAccount)){
                            Map<Integer,Integer> map=mMisseds.get(j).flag;
                            map.put(1,calls.get(i).count);
                        }
                    }
                }
                if (mAdapter!=null){mAdapter.notifyDataSetChanged();}
            }

            @Override
            public void onError() {
                Log.d(TAG, "1111111111: 无未接电话");
            }
        });
    }

    @Override
    public void notification() {
        queryUnMissed();
    }

    class FriendAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public Object getItem(int position) {
            return mLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                //引入ViewHolder提升ListView的效率
                holder = new Holder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
                holder.mDelete = convertView.findViewById(R.id.friend_item_delete);
                holder.mCall = convertView.findViewById(R.id.friend_item_call);
                holder.mIndex = convertView.findViewById(R.id.friend_item_index);
                holder.mName = convertView.findViewById(R.id.friend_item_name);
                holder.mPhone = convertView.findViewById(R.id.friend_item_phone);
                holder.mMissedCallCount = convertView.findViewById(R.id.friend_item_missed_call_count);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            //赋值
            //序号
            holder.mIndex.setText(String.valueOf(position + 1));
            //名字
            holder.mName.setText(mLists.get(position).alias);
            //电话
            holder.mPhone.setText(mLists.get(position).friendAccount);

            holder.mPhone.setTag(mLists.get(position).friendAccount);

            //未接电话
            if (mMisseds!=null) {
                for (int i=0;i<mMisseds.size();i++) {
                    String phone = (String) holder.mPhone.getTag();
                    Log.d(TAG, "getView: "+mMisseds.get(i).account);
                    Log.d(TAG, "getView: "+phone);
                    Map<Integer, Integer> map = mMisseds.get(position).flag;
                    if (map.get(1) != null && map.get(1) != 0) {
                        if (phone.equals(mMisseds.get(i).account)) {
                            holder.mMissedCallCount.setVisibility(View.VISIBLE);
                            holder.mMissedCallCount.setText(map.get(1) + "");
                        }
                    } else {
                        holder.mMissedCallCount.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (isSelecting) {
                holder.mDelete.setVisibility(View.VISIBLE);
                holder.mCall.setVisibility(View.GONE);
            } else {
                holder.mDelete.setVisibility(View.GONE);
                holder.mCall.setVisibility(View.VISIBLE);
            }

            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemDeleteListener.onDeleteClick(mLists.get(position).friendAccount,position);
                }
            });
            return convertView;
        }


        public void removeItem(int position) {
            if (mLists.size() > position) {
                mLists.remove(position);
            }
            notifyDataSetChanged();
        }

        private OnItemDeleteListener mOnItemDeleteListener;

        public void setOnItemDeleteClickListener(OnItemDeleteListener onItemDeleteListener) {
            this.mOnItemDeleteListener = onItemDeleteListener;
        }
    }

    static class Holder {
        ImageButton mDelete;
        TextView mIndex;
        TextView mName;
        TextView mPhone;
        ImageView mCall;
        TextView mMissedCallCount;
    }

    /**
     * 删除按钮的监听接口
     */
    interface OnItemDeleteListener {
        void onDeleteClick(String account, int i);
    }
}
