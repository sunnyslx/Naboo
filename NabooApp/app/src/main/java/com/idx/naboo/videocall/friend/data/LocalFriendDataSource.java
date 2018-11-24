package com.idx.naboo.videocall.friend.data;


import com.idx.naboo.videocall.utils.NabooExecutors;

import java.util.List;

/**
 * 数据库操作类实现
 * Created by danny on 3/30/18.
 */

public class LocalFriendDataSource implements FriendDataSource {
    private static volatile LocalFriendDataSource sInstance;
    private NabooExecutors mNabooExecutors;
    private UserDao mUserDao;
    private HxUser mUser;
    private List<HxUser> mUsers;
    private Friend mFriend;
    private List<Friend> mFriends;
    private List<String> mAccounts;

    private LocalFriendDataSource(NabooExecutors nabooExecutors, UserDao userDao){
        mNabooExecutors=nabooExecutors;
        mUserDao=userDao;
    }

    public static LocalFriendDataSource getInstance(NabooExecutors loginExecutors, UserDao userDao){
        if (sInstance==null){
            synchronized (LocalFriendDataSource.class){
                if (sInstance==null){
                    sInstance=new LocalFriendDataSource(loginExecutors,userDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertUser(final HxUser user) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.insertUser(user);
            }
        });
    }

    @Override
    public void queryUser(final String account, final LoadUserCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUser=mUserDao.queryUser(account);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mUser!=null) {
                            if (callback != null) {
                                callback.onSuccess(mUser);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllUser(final LoadAllUserCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUsers=mUserDao.queryAllUser();
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mUsers.isEmpty()){
                            if (callback!=null){
                                callback.onSuccess(mUsers);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteUser() {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteUser();
            }
        });
    }

    @Override
    public void deleteUser(final String account) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteUser(account);
            }
        });
    }

    @Override
    public void insertFriend(final Friend friend, final AddFriendSuccessCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.insertFriend(friend);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }

    @Override
    public void queryFriend(final String friendAccount, final LoadFriendCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriend=mUserDao.queryFriend(friendAccount);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriend!=null){
                            if (callback!=null){
                                callback.onSuccess(mFriend);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAliasFriend(final String alias, final LoadFriendCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriend=mUserDao.queryAliasFriend(alias);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriend!=null){
                            if (callback!=null){
                                callback.onSuccess(mFriend);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryMoreAliasFriend(final String alias, final LoadAllFriendCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriends=mUserDao.queryMoreAliasFriend(alias);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriends!=null){
                            if (callback!=null){
                                callback.onSuccess(mFriends);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFriend(final String userId, final LoadAllFriendCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriends=mUserDao.queryAllFriend(userId);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mFriends.isEmpty()){
                            if (callback!=null){
                                callback.onSuccess(mFriends);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFriendAccount(final String userId, final LoadAllFriendAccountCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mAccounts=mUserDao.queryAllFriendAccount(userId);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mAccounts.isEmpty()){
                            if (callback!=null){
                                callback.onSuccess(mAccounts);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteFriend() {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteFriend();
            }
        });
    }

    @Override
    public void deleteFriend(final String friendAccount, final DeleteFriendSuccessCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteFriend(friendAccount);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }

    @Override
    public void deleteAliasFriend(final String alias, final DeleteFriendAliasSuccessCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteAliasFriend(alias);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }
}
