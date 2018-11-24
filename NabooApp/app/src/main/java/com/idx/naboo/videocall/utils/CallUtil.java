package com.idx.naboo.videocall.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.idx.naboo.imoran.TTSManager;
import com.idx.naboo.takeout.utils.ImoranResponseParseUtil;
import com.idx.naboo.utils.NetStatusUtils;
import com.idx.naboo.videocall.call.CallManager;
import com.idx.naboo.videocall.call.VideoCallActivity;
import com.idx.naboo.videocall.call.data.CallDataSource;
import com.idx.naboo.videocall.call.data.CallInjection;
import com.idx.naboo.videocall.call.data.MissedCall;
import com.idx.naboo.videocall.friend.data.Friend;
import com.idx.naboo.videocall.friend.data.FriendDataSource;
import com.idx.naboo.videocall.friend.data.FriendInjection;
import com.idx.naboo.videocall.friend.data.FriendRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 打电话工具类
 * Created by danny on 5/18/18.
 */

public class CallUtil {
    private static final String TAG = CallUtil.class.getSimpleName();
    private static String regex="[0-9]+";
    private static FriendRepository mRepository;
    /**
     * 打电话
     */
    public static void openVideoCall(final Context context, String json) {
        if (mRepository==null) {mRepository = FriendInjection.getInstance(context);}
        if (NetStatusUtils.isMobileConnected(context) || NetStatusUtils.isWifiConnected(context)) {
            if (!TextUtils.isEmpty(json)) {
                final String toUserName = ImoranResponseParseUtil.getNewAccount(json);
                final String currentUser = EMClient.getInstance().getCurrentUser();
                Log.d(TAG, "openVideoCall: " + currentUser);
                Log.d(TAG, "bugTest: " + toUserName);
                Pattern pattern=Pattern.compile(regex);
                Matcher matcher=pattern.matcher(toUserName);
                if (matcher.find()){
                    Log.d(TAG, "openVideoCall: 数字");
                    EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(new EMValueCallBack<List<String>>() {
                        @Override
                        public void onSuccess(List<String> list) {
                            Log.d(TAG, "bugTest"+list.size());
                            for (int i=0;i<list.size();i++){
                                if ((list.get(i).trim()).equals(toUserName.trim())){
                                    if (TextUtils.isEmpty(currentUser) && currentUser.equals(toUserName)) {
                                        Log.d(TAG, "openVideoCall: 小子，你不能和自己打电话!");
                                        Toast.makeText(context, "小子，你不能和自己打电话!", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        SpUtils.put(context, "toUsername", toUserName);
                                        SpUtils.put(context, "toUserAccount", toUserName);
                                    }
                                    if (!TextUtils.isEmpty(toUserName)) {
                                        Log.d(TAG, "onSuccess: " + toUserName);
                                        Intent intent = new Intent(context, VideoCallActivity.class);
                                        CallManager.getInstance().setToChatId(toUserName);
                                        CallManager.getInstance().setInComingCall(false);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        CallInjection.getInstance(context).queryPointCall(toUserName, new CallDataSource.LoadPointCallCallback() {//删除未接电话
                                            @Override
                                            public void onSuccess(MissedCall call) {
                                                CallInjection.getInstance(context).deleteCall(toUserName);
                                            }

                                            @Override
                                            public void onError() {}
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.d(TAG, "onError: "+s);
                            TTSManager.getInstance(context).speak(toUserName + "你还没有好友，先加好友再打电话吧！", false);
                        }
                    });
                }else {
                    Log.d(TAG, "openVideoCall: 汉字");
                    mRepository.queryAliasFriend(toUserName, new FriendDataSource.LoadFriendCallback() {
                        @Override
                        public void onSuccess(Friend friend) {
                            String toUserAccount = friend.friendAccount;
                            if (TextUtils.isEmpty(currentUser) && currentUser.equals(toUserAccount)) {
                                Log.d(TAG, "openVideoCall: 小子，你不能和自己打电话!");
                                Toast.makeText(context, "小子，你不能和自己打电话!", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                SpUtils.put(context, "toUsername", toUserName);
                                SpUtils.put(context, "toUserAccount", toUserAccount);
                            }
                            if (!TextUtils.isEmpty(toUserAccount)) {
                                Log.d(TAG, "onSuccess: " + toUserAccount);
                                Intent intent = new Intent(context, VideoCallActivity.class);
                                CallManager.getInstance().setToChatId(toUserAccount);
                                CallManager.getInstance().setInComingCall(false);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                final String temp = toUserAccount;
                                CallInjection.getInstance(context).queryPointCall(temp, new CallDataSource.LoadPointCallCallback() {//删除未接电话
                                    @Override
                                    public void onSuccess(MissedCall call) {
                                        CallInjection.getInstance(context).deleteCall(temp);
                                    }

                                    @Override
                                    public void onError() {}
                                });
                            }
                        }

                        @Override
                        public void onError() {
                            TTSManager.getInstance(context).speak(toUserName + "不在你的通讯录中，先加好友再打电话吧！", true);
                            Log.d(TAG, "onError: 你们还不是好友");
                        }
                    });
                }
            }
        } else {
            Toast.makeText(context, "没有网络,无法拨打电话！", Toast.LENGTH_SHORT).show();
        }
    }
}
