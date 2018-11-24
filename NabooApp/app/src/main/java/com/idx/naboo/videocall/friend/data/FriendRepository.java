package com.idx.naboo.videocall.friend.data;

/**
 * Created by danny on 3/31/18.
 */

public class FriendRepository implements FriendDataSource {
    private static FriendRepository sInstance = null;
    private LocalFriendDataSource mSource;

    private FriendRepository(LocalFriendDataSource dataSource) {
        mSource = dataSource;
    }

    public static FriendRepository getInstance(LocalFriendDataSource dataSource) {
        if (sInstance == null) {
            sInstance = new FriendRepository(dataSource);
        }
        return sInstance;
    }

    @Override
    public void insertUser(HxUser user) {mSource.insertUser(user);}

    @Override
    public void queryUser(String account, LoadUserCallback callback) {mSource.queryUser(account, callback);}

    @Override
    public void queryAllUser(LoadAllUserCallback callback) {mSource.queryAllUser(callback);}

    @Override
    public void deleteUser() {mSource.deleteUser();}

    @Override
    public void deleteUser(String account) {mSource.deleteUser(account);}

    @Override
    public void insertFriend(Friend friend,AddFriendSuccessCallback callback) {mSource.insertFriend(friend, callback);}

    @Override
    public void queryFriend(String friendAccount, LoadFriendCallback callback) {mSource.queryFriend(friendAccount, callback);}

    @Override
    public void queryAliasFriend(String alias, LoadFriendCallback callback) {mSource.queryAliasFriend(alias,callback);}

    @Override
    public void queryMoreAliasFriend(String alias, LoadAllFriendCallback callback) {mSource.queryMoreAliasFriend(alias,callback);}

    @Override
    public void queryAllFriend(String userId, LoadAllFriendCallback callback) {mSource.queryAllFriend(userId, callback);}

    @Override
    public void queryAllFriendAccount(String userId, LoadAllFriendAccountCallback callback) {mSource.queryAllFriendAccount(userId, callback);}

    @Override
    public void deleteFriend() {mSource.deleteFriend();}

    @Override
    public void deleteFriend(String friendAccount, DeleteFriendSuccessCallback callback) {mSource.deleteFriend(friendAccount,callback);}

    @Override
    public void deleteAliasFriend(String alias, DeleteFriendAliasSuccessCallback callback) {mSource.deleteAliasFriend(alias,callback);}
}
